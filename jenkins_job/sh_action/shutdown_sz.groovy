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

        stage('Shutdown SZ') {
            steps {
                sh '''#!/bin/bash
if [ ! -f $VAR_DIR/input/sz/sz.inp ]; then
  echo "no found $VAR_DIR/input/sz/sz.inp"
  exit 1
fi


for vm_name in `awk '{print \$1}' $VAR_DIR/input/sz/sz.inp`; do
  echo "start: `date`"
  gcloud compute instances stop $vm_name
  echo "end: `date`"
done
'''
            }
        }
    }
}
