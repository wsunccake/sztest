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

        stage('Delete AP Registration Rule') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/delete_ap_rule

# run
echo "start job:`date`"
./login.sh admin "$ADMIN_PASSWORD"
  
for f in `ls $VAR_DIR/output/ap_rule/*.out`; do
  ap_rule_id=`awk -F\\" '/Response body.*id/{print \$4}' $f`
  echo "${f##*/} $ap_rule_id"
  ./delete_ap_rule.sh $ap_rule_id | tee $VAR_DIR/output/delete_ap_rule/${ap_rule_id}.out
done

echo "end job:`date`"
'''
            }
        }
    }
}
