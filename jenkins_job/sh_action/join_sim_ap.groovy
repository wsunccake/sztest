pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'AP_VER', defaultValue: '', description: '')
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

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env


SIM_INPUT=$VAR_DIR/input/sim/sim.inp
SIM_USER="jenkins"

# run
cd $VAR_DIR/input/sim

if [ ! -f $SIM_INPUT ]; then
  echo "no found $SIM_INPUT"
  exit 1
fi

sim_number=`cat $SIM_INPUT | wc -l`

for sim_config_dir in `seq $sim_number`; do
  sim_pc=`sed -n ${sim_config_dir}p $SIM_INPUT | awk '{print \$2}'`
  if [ -d $sim_config_dir ]; then
    echo "$sim_pc config"
    sed s/SZ_IP/$SZ_IP/ $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg.template > $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg
    sed -i s/AP_VER/$AP_VER/ $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg
    echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg $SIM_USER@$sim_pc:/tmp/apsim.cfg"
    scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg $SIM_USER@$sim_pc:/tmp/apsim.cfg
    echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_sim.sh /tmp/apsim.cfg'"
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_sim.sh /tmp/apsim.cfg'
  else
    echo "$sim_pc no found config $sim_config_dir"
    exit 1
  fi
done
'''
            }
        }
    }
}