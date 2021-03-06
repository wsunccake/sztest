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

        stage('Startup Radius') {
            steps {
                sh '''#!/bin/bash
set -e

if [ ! -f $VAR_DIR/input/radius/radius.inp ]; then
  echo "no found $VAR_DIR/input/radius/radius.inp"
  exit 1
fi


for radius in `awk '{print \$1}' $VAR_DIR/input/radius/radius.inp`; do
  echo "start: `date`"
  time gcloud compute instances start $radius
  echo "end: `date`"
done

sleep 60
'''
            }
        }
    }
}