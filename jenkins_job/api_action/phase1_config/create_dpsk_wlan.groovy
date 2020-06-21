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
        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: '')

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

        stage('Create DPSK WLAN') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/wlans

NEW_INPUT=zone_dpsk_wlan.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do
  # get zone_id
  zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`

  for wlan_name in `grep dpsk $VAR_DIR/input/wlans/$zone_name.inp`; do
    if [ ! -z $zone_id ]; then
      echo "zone: $zone_name $zone_id wlan_name: $wlan_name" >> $TMP_DIR/$NEW_INPUT
    fi
  done

done

split -l $INPUT_NUMBER $TMP_DIR/$NEW_INPUT $TMP_DIR/in_
cp -fv $TMP_DIR/$NEW_INPUT $VAR_DIR/input/wlans/.

# run
echo "start job:`date`"
for f in `ls $TMP_DIR/in_*`; do
  # login
  ./login.sh admin "$ADMIN_PASSWORD"
  
  # create ap per zone
  cat $f | xargs -n5 -P $NPROC sh -c './create_wlan_dpsk.sh $4 $2 | tee $VAR_DIR/output/wlans/$1_$4.out'
    
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
                    def result = util.checkResponseStatus "${VAR_DIR}/output/wlans"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/wlans"
                }
            }
        }

    }
}
