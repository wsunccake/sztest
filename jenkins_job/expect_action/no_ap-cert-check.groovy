pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'EXPECT_DIR', defaultValue: '/var/lib/jenkins/expect', description: '')

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

        stage('Setup Collectd') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

# setup sz ip
if [ -z $SZ_IP ]; then
  SZ_IP=`sed -n 1p $VAR_DIR/input/sz/sz.inp`
fi 

export SZ_IP=$SZ_IP
export CLUSTER_NAME=$CLUSTER_NAME
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, CLUSTER_NAME: $CLUSTER_NAME"
#env


echo "no ap-cert-check"
$EXPECT_DIR/sz/common/no-ap-cert-check.exp
'''
            }
        }
    }
}