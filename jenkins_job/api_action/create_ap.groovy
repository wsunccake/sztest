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

        stage('Create AP') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/aps

# run
echo "start job:`date`"
for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do

  # get zone_id
  zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
  echo "zone: $zone_name, $zone_id"
  
  # login
  ./login.sh admin "$ADMIN_PASSWORD"
  
  # create wlan
  cat $VAR_DIR/input/aps/$zone_name.inp | xargs -P $NPROC -i sh -c "./create_ap.sh {} '' '' $zone_id | tee $VAR_DIR/output/aps/{}.out"
    
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
                    def cmd1 = ["bash", "-c", "grep 'Response code:' ${VAR_DIR}/output/aps/*.out | wc -l"]
                    def proc1 = Runtime.getRuntime().exec((String[]) cmd1.toArray())
                    def totalResponse = proc1.text.trim() as Integer

                    def cmd2 = ["bash", "-c", "grep 'Response code: 201' ${VAR_DIR}/output/aps/*.out | wc -l"]
                    def proc2 = Runtime.getRuntime().exec((String[]) cmd2.toArray())
                    def successfulResponse = proc2.text.trim() as Integer

                    println "total: ${totalResponse}"
                    println "successful: ${successfulResponse}"

                    if (successfulResponse == totalResponse) {
                        currentBuild.result = 'SUCCESS'
                    } else if (successfulResponse == 0) {
                        currentBuild.result = 'FAILURE'
                    } else {
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

    }
}
