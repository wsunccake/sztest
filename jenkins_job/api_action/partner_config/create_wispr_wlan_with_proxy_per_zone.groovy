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
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Create WISPr WLAN With Proxy Per Zone') {
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
mkdir -p $VAR_DIR/output/wispr_wlans

NEW_INPUT=zone_wispr_wlan.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

for domain_name in `cat $VAR_DIR/input/partner_domains/domains.inp`; do
  # get domain_id
  domain_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/partner_domains/$domain_name.out`
  
  if [ ! -z $domain_id ]; then
    for zone_name in `cat $VAR_DIR/input/zones/$domain_name.inp`; do
      # get zone_id
      zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
      
      if [ ! -z $zone_id ]; then      
        for name in `grep wispropen $VAR_DIR/input/wlans/$zone_name.inp`; do
          n=1
          hotspot_name=`sed -n 1p $VAR_DIR/input/hotspot/$zone_name.inp`
          auth_ip=`sed -n ${n}p $VAR_DIR/input/proxy_auth/$domain_name.inp`
          auth_id=`awk -F\\" '/id/ {print \$4}' $VAR_DIR/output/proxy_auth/${domain_name}_${auth_ip}.${n}.out`
          acct_ip=`sed -n ${n}p $VAR_DIR/input/proxy_acct/$domain_name.inp`
          acct_id=`awk -F\\" '/id/ {print \$4}' $VAR_DIR/output/proxy_acct/${domain_name}_${acct_ip}.${n}.out`
          
          echo "zone: $zone_name $zone_id wlan: $name hotspot: $hotspot_name proxy_auth: $auth_id proxy_acct: $acct_id" >> $TMP_DIR/$NEW_INPUT
        done
      fi

    done
  fi

done

split -l $INPUT_NUMBER $TMP_DIR/$NEW_INPUT $TMP_DIR/in_
cp -fv $TMP_DIR/$NEW_INPUT $VAR_DIR/input/wlans/.

# run
echo "start job:`date`"
for f in `ls $TMP_DIR/in_*`; do
  # login
  ./login.sh admin "$ADMIN_PASSWORD"
  
  # create wlan
  cat $f | xargs -n11 -P $NPROC sh -c './create_wispr_wlan_with_proxy.sh $4 $2 $6 $8 $10 | tee $VAR_DIR/output/wispr_wlans/$1_$4.out'
    
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
                    def result = util.checkResponseStatus "${VAR_DIR}/output/wispr_wlans"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/wispr_wlans"
                }
            }
        }

    }
}
