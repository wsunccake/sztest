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

        stage('Create PSK WLAN Per Zone') {
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
mkdir -p $VAR_DIR/output/psk_wlans


# run
echo "start job:`date`"
for name in `cat $VAR_DIR/input/zones/zones.inp`; do
  export zone_name=$name

  # get zone_id
  export zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
  echo "zone: $zone_name, $zone_id"

  # login
  ./login.sh admin "$ADMIN_PASSWORD"

  # create non proxy auth
  grep psk $VAR_DIR/input/wlans/$zone_name.inp | xargs -P $NPROC -i sh -c "./create_open_wlan_with_wpa2_and_aes.sh {} $zone_id | tee $VAR_DIR/output/psk_wlans/${zone_name}_{}.out"
  
  # logout
  ./logout.sh

done
echo "end job:`date`"
'''
            }
        }

        stage('Check Response') {
            steps {
                script {
                    def result = util.checkResponseStatus "${VAR_DIR}/output/psk_wlans"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/psk_wlans"
                }
            }
        }

    }
}
