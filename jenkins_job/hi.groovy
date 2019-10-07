pipeline {
    agent any
    parameters { string(name: 'version', defaultValue: '1.0.0.0', description: '') }

    stages {
        stage('Show') {
            steps {
                script {
                    currentBuild.displayName = "${version} - #${BUILD_ID}"
                }

                echo "Current Version: ${params.version}"
                sh '''#!/bin/bash
echo "${version}"
'''
            }
        }
    }
}

