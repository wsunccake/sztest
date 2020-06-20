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

        stage('Associate UE') {
            steps {
                sh '''#!/bin/bash
#set -e

SIM_INPUT=$VAR_DIR/input/sim/sim.inp
SIM_USER="jenkins"


# run
cd $VAR_DIR/input/sim

if [ ! -f $SIM_INPUT ]; then
  echo "no found $SIM_INPUT"
  exit 1
fi

sim_number=`cat $SIM_INPUT | wc -l`
echo "start job:`date`"

for sim_config_dir in `seq $sim_number`; do
  sim_pc=`sed -n ${sim_config_dir}p $SIM_INPUT | awk '{print \$2}'`
  if [ -d $sim_config_dir ]; then
    echo "$sim_pc config"
    
    echo "start time:`date`"
    
    echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $VAR_DIR/input/sim/$sim_config_dir/ue_open.conf $SIM_USER@$sim_pc:/tmp/ue_open.conf"
    scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $VAR_DIR/input/sim/$sim_config_dir/ue_open.conf $SIM_USER@$sim_pc:/tmp/ue_open.conf
    echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_madue.sh /tmp/ue_open.conf'"
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_madue.sh /tmp/ue_open.conf'
    
    echo "end time:`date`"
  else
    echo "$sim_pc no found config $sim_config_dir"
    exit 1
  fi
done

echo "end job:`date`"
'''
            }
        }
    }
}