pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'NPROC', defaultValue: '8', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Sim PC Connection Status') {
            steps {
                sh '''#!/bin/bash
source ./util/sim_util.sh
export -f ap_ue_state

awk '{print \$2}' $VAR_DIR/input/sim/sim.inp | xargs -P ${NPROC} -i sh -c 'ap_ue_state {}'
'''
            }
        }

    }
}
