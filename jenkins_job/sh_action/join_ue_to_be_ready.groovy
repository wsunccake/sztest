pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'EXPECT_DIR', defaultValue: '/var/lib/jenkins/expect', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')
        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'WAITING_TIME', defaultValue: '3000', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Join UE') {
            steps {
                sh '''#!/bin/bash
source $EXPECT_DIR/sz/var/expect-var.sh
set -e


join_sim_ue() {
  local ue_cfg=$1

  echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $ue_cfg $SIM_USER@$sim_pc:/tmp/ue_open.conf"
  scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $ue_cfg $SIM_USER@$sim_pc:/tmp/ue_open.conf
  echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_madue.sh /tmp/ue_open.conf'"
  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_madue.sh /tmp/ue_open.conf\'

#  echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $apsim_cfg $SIM_USER@$sim_pc:/tmp/apsim.cfg"
#  scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $apsim_cfg $SIM_USER@$sim_pc:/tmp/apsim.cfg
#  echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_sim.sh /tmp/apsim.cfg'"
#  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_sim.sh /tmp/apsim.cfg\'
}


check_ue_online() {
  local ue_num=$1

  # work dir
  cd $API_PERF_DIR/public_api/$API_PERF_VER

  init_time=`date +%s`
  waiting_time=$WAITING_TIME
  interval=10

  echo "start job:`date`"
    while true; do
      ./login.sh admin "$ADMIN_PASSWORD"
      echo "ue on line"
      echo "start time:`date`"

      ./monitor_client.sh
      online_ues=`./monitor_client.sh | awk '/onlineCount/ {print \$2}' | tr -d ,`
    
      end_time=`date +%s`
      echo "end time:`date`"
      echo "ap num: $ue_num, $online_ues"    
      [ "$online_ues" -ge "$ue_num" ] && break
      [ "`expr $end_time - $init_time`" -gt "$waiting_time" ] && exit 1
    
      sleep $interval
    done
  echo "end job:`date`"
}


export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env


SIM_INPUT=$VAR_DIR/input/sim/sim.inp
SIM_USER="jenkins"
total_ue_num=0
done_file=$VAR_DIR/input/sim/ue_done

# run
cd $VAR_DIR/input/sim

if [ ! -f $SIM_INPUT ]; then
  echo "no found $SIM_INPUT"
  exit 1
fi

sim_number=`cat $SIM_INPUT | wc -l`
touch $done_file

for sim_config_dir in `seq $sim_number`; do
  cd $VAR_DIR/input/sim
  sim_pc=`sed -n ${sim_config_dir}p $SIM_INPUT | awk '{print \$2}'`
  if [ -d $sim_config_dir ]; then
    echo "$sim_pc config"

    res=`grep "^${sim_config_dir}\$" $done_file >& /dev/null && echo "True" || echo "False"`
    if [ "$res" == "False" ]; then
      join_sim_ue $VAR_DIR/input/sim/$sim_config_dir/ue_open.conf
      echo "$sim_config_dir" >> $done_file
    fi

    ue_num=`awk -F= '/^total_sta_num/{print \$2}' $VAR_DIR/input/sim/$sim_config_dir/ue_open.conf`
    total_ue_num=`expr $total_ue_num + $ue_num`
    check_ue_online $total_ue_num
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