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
ap_ue_state() {
  local h=$1
  local ssh_timeout=3
  local is_ssh_connect=`ssh -vvv -o ConnectTimeout=$ssh_timeout $h date >& $VAR_DIR/input/sim/$h.log && echo true || echo false`

  if [ "$is_ssh_connect" == "true" ]; then
    ssh $h 'sudo su - -c "cd /opt/madSZ/BUILD/scripts && ./madutil -v"' 2> /dev/null 1>> $VAR_DIR/input/sim/$h.log
    ssh $h 'sudo su - -c "ls /dev/shm/sim/ue | xargs -i /opt/madSZ/BUILD/bin/dumpTool -s {}"' 2> /dev/null 1>> $VAR_DIR/input/sim/$h.log
    echo "ssh connectio pass: $h"
  else
    echo "ssh connectio fail: $h"
  fi
}
export -f ap_ue_state

awk '{print \$2}' $VAR_DIR/input/sim/sim.inp | xargs -P ${NPROC} -i sh -c 'ap_ue_state {}'
'''
            }
        }

    }
}
