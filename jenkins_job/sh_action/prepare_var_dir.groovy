pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'SRC_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Prepare Var Dir') {
            steps {
                sh '''#!/bin/bash
[ -d ${VAR_DIR} ] && mv ${VAR_DIR} ${VAR_DIR}-`date +%s`
mkdir -p $VAR_DIR/..
echo "cp -rf ${SRC_DIR} ${VAR_DIR}/.."
cp -rf ${SRC_DIR} ${VAR_DIR}/..

#[ -d ${VAR_DIR}/output ] && rm -rf ${VAR_DIR}/output
mkdir -p $VAR_DIR/output
'''
                echo "var dir: ${VAR_DIR}"
                echo "input dir: ${VAR_DIR}/input"
                echo "output dir: ${VAR_DIR}/output"
            }
        }
    }
}