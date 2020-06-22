library identifier: 'dynamic-libary@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/wsunccake/sztest.git'])

pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'EXPECT_DIR', defaultValue: '/var/lib/jenkins/expect', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')
        string(name: 'API_PERF_VER', defaultValue: 'v9_1', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'NPROC', defaultValue: '2', description: '')

        string(name: 'RADIUS_PORT', defaultValue: '1813', description: '')
        string(name: 'RADIUS_SECRET', defaultValue: '1234', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Create Accounting Per Zone') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

# setup sz ip
if [ -z $SZ_IP ]; then
  SZ_IP=`sed -n 1p $VAR_DIR/input/sz/sz.inp`
fi 

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/non_proxy_acct

export radius_port=$RADIUS_PORT
export radius_secret=$RADIUS_SECRET

NEW_INPUT=partner_domain_non_proxy_acct.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do
  # get zone_id
  export zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`

  # create non proxy acct
  i=1
  for radius_ip in `cat $VAR_DIR/input/non_proxy_acct/$zone_name.inp`; do
    if [ ! -z $zone_id ]; then
      echo "zone: $zone_name $zone_id non_proxy_acct: $radius_ip $i" >> $TMP_DIR/$NEW_INPUT
      i=`expr $i + 1`
    fi
  done
done

split -l $INPUT_NUMBER $TMP_DIR/$NEW_INPUT $TMP_DIR/in_
cp -fv $TMP_DIR/$NEW_INPUT $VAR_DIR/input/non_proxy_acct/.

# run
echo "start job:`date`"
for f in `ls $TMP_DIR/in_*`; do
  # login
  ./login.sh admin "$ADMIN_PASSWORD"
  
  # create acct
  cat $f | xargs -n6 -P $NPROC sh -c './create_non_proxy_acct_service.sh acct$4.$5 $4 $radius_port $radius_secret $2 | tee $VAR_DIR/output/non_proxy_acct/$1_$4.$5.out'
    
  # logout
  ./logout.sh
done
echo "end job:`date`"

rm -rfv $TMP_DIR
'''
            }
        }

        stage('Check Response') {
            steps {
                script {
                    def result = util.checkResponseStatus "${VAR_DIR}/output/non_proxy_acct"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/non_proxy_acct"
                }
            }
        }

    }
}
