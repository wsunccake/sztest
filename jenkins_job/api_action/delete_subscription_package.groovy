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
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Delete Subscription Package') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/delete_subscription_package

# run
echo "start job:`date`"
./login.sh admin "$ADMIN_PASSWORD"
  
  # delete ap
  for subscription_package_name in `cat $VAR_DIR/input/subscription_package/subscription_package.inp`; do
    echo "start time:`date`"
    echo "$subscription_package_name"
    ./delete_subscription_package.sh $subscription_package_name | tee $VAR_DIR/output/delete_subscription_package/${subscription_package_name}.out
    echo "end time:`date`"
  done

echo "end job:`date`"
'''
            }
        }
    }
}
