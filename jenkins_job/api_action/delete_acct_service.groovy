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

        stage('Delete Accounting Service') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/delete_acct

# run
echo "start job:`date`"
for acct_name in `awk '{print \$1}' $VAR_DIR/input/acct/acct.inp`; do

  # get acct_id
  acct_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/output/acct/$acct_name.out`
  echo "accounting service: $acct_name, $acct_id"
  ./login.sh admin "$ADMIN_PASSWORD"
  
  # delete accounting service
  echo "start time:`date`"
  ./delete_acct_service.sh $acct_id | tee $VAR_DIR/output/delete_acct/$acct_id.out
  echo "end time:`date`"

done
echo "end job:`date`"
'''
            }
        }
    }
}
