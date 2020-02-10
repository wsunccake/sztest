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
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Delete Domain') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/delete_auth

# run
echo "start job:`date`"
for acct_name in `awk '{print \\$1}' $VAR_DIR/input/auth/auth.inp`; do

  # get auth_id
  auth_id=`awk -F\\" '/id/{print \\$4}' $VAR_DIR/output/auth/$auth_name.out`
  echo "authentication service: $auth_name, $auth_id"
  ./login.sh admin "$ADMIN_PASSWORD"
  
  # delete authentication service
  echo "start time:`date`"
  ./delete_auth_service.sh $auth_id | tee $VAR_DIR/output/delete_auth/$auth_id.out
  echo "end time:`date`"

done
echo "end job:`date`"
'''
            }
        }
    }
}
