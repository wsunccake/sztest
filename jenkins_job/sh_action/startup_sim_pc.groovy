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

        stage('Startup SimPC') {
            steps {
                sh '''#!/bin/bash
set -e

if [ ! -f $VAR_DIR/input/sim/sim.inp ]; then
  echo "no found $VAR_DIR/input/sim/sim.inp"
  exit 1
fi


for sim_pc in `awk '{print \$1}' $VAR_DIR/input/sim/sim.inp`; do
  echo "start: `date`"
  time gcloud compute instances start $sim_pc
  echo "end: `date`"
done

sleep 60
'''
            }
        }
    }
}