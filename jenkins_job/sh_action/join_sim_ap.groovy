pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'AP_VER', defaultValue: '', description: '')
        string(name: 'AP_MODEL', defaultValue: 'R710', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Join AP') {
            steps {
                sh '''#!/bin/bash
# set -e

create_ap_cfg() {
  local apsim_cfg=$1

  sed s/SZIP=.*/SZIP=$SZ_IP/ ${apsim_cfg}.template > $apsim_cfg
  sed -i s/FWVER=.*/FWVER=$AP_VER/ $apsim_cfg
  sed -i s/MODEL=.*/MODEL=$AP_MODEL/ $apsim_cfg
}


join_sim_ap() {
  local apsim_cfg=$1
  local sim_pc=$2
  local ssh_timeout=10
  
  echo "start to join ap time:`date`"

  echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=$ssh_timeout $apsim_cfg $SIM_USER@$sim_pc:/tmp/apsim.cfg"
  scp -o UserKnownHostsFile=/dev/null -o ConnectTimeout=$ssh_timeout -o StrictHostKeyChecking=no $apsim_cfg $SIM_USER@$sim_pc:/tmp/apsim.cfg
  echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=$ssh_timeout  $SIM_USER@$sim_pc 'sudo /root/run_sim.sh /tmp/apsim.cfg'"
  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=$ssh_timeout $SIM_USER@$sim_pc 'sudo /root/run_sim.sh /tmp/apsim.cfg'
  
  echo "start to join ap time:`date`"
}

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
echo "start job:`date`"

for sim_config_dir in `seq $sim_number`; do
  sim_pc=`sed -n ${sim_config_dir}p $SIM_INPUT | awk '{print \$2}'`
  if [ -d $sim_config_dir ]; then
    echo "$sim_pc config"
    create_ap_cfg $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg
    
    echo "start time:`date`"
    join_sim_ap $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg $sim_pc
    
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