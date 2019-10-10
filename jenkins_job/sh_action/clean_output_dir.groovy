pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Clean Output Dir') {
            steps {
                sh '''#!/bin/bash
[ -d $VAR_DIR/output ] && rm -rf $VAR_DIR/output
mkdir -p $VAR_DIR/output
'''
                echo "output dir: ${VAR_DIR}/output"
            }
        }
    }
}