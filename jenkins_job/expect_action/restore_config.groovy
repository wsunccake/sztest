
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
        string(name: 'BACKUP_CONFIG_FILE', defaultValue: 'Configuration_20191202094725GMT_5.2.0.0.612.bak', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Restore Config') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
export CLUSTER_NAME=$CLUSTER_NAME
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, CLUSTER_NAME: $CLUSTER_NAME"
#load_setup_backup_restore_variable
export BACKUP_CONFIG_FILE=$BACKUP_CONFIG_FILE
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER


echo "setup restore backup-config"
$EXPECT_DIR/sz/common/restore-backup-config.exp
'''
            }
        }
    }
}
