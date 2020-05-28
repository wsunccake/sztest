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

        stage('Create Partner Domain') {
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
mkdir -p $VAR_DIR/output/partner_domains

# run
echo "start job:`date`"

# login
./login.sh admin "$ADMIN_PASSWORD"

cat $VAR_DIR/input/partner_domains/domains.inp | xargs -P $NPROC -i sh -c "./create_partner_domain.sh {} | tee $VAR_DIR/output/partner_domains/{}.out"

# logout
./logout.sh

echo "end job:`date`"
'''
            }
        }

        stage('Check Response') {
            steps {
                script {
                    def cmd1 = ["bash", "-c", "grep 'Response code:' ${VAR_DIR}/output/partner_domains/*.out | wc -l"]
                    def proc1 = Runtime.getRuntime().exec((String[]) cmd1.toArray())
                    def totalResponse = proc1.text.trim() as Integer

                    def cmd2 = ["bash", "-c", "grep 'Response code: 201' ${VAR_DIR}/output/partner_domains/*.out | wc -l"]
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