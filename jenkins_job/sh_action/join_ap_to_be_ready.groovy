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
        string(name: 'AP_VER', defaultValue: '', description: '')
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

        stage('Join AP') {
            steps {
                sh '''#!/bin/bash
source $EXPECT_DIR/sz/var/expect-var.sh
set -e

create_ap_cfg() {
  local apsim_cfg=$1

  sed s/SZ_IP/$SZ_IP/ ${apsim_cfg}.template > $apsim_cfg
  sed -i s/AP_VER/$AP_VER/ $apsim_cfg
}


join_sim_ap() {
  local apsim_cfg=$1

  echo "start to join ap time:`date`"
  
  echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $apsim_cfg $SIM_USER@$sim_pc:/tmp/apsim.cfg"
  scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $apsim_cfg $SIM_USER@$sim_pc:/tmp/apsim.cfg
  echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_sim.sh /tmp/apsim.cfg'"
  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SIM_USER@$sim_pc 'sudo /root/run_sim.sh /tmp/apsim.cfg'
  
  echo "end to join ap time:`date`"
}


check_ap_online() {
  local ap_num=$1

  # work dir
  cd $API_PERF_DIR/public_api/$API_PERF_VER

  init_time=`date +%s`
  waiting_time=$WAITING_TIME
  interval=10

  echo "start job:`date`"
    while true; do
      ./login.sh admin "$ADMIN_PASSWORD"
      echo "ap on line"
      echo "start to check ap online time:`date`"

      ./monitor_ap.sh
      online_aps=`./monitor_ap.sh | awk '/onlineCount/ {print \$2}' | tr -d ,`
    
      end_time=`date +%s`
      echo "end to check ap online time:`date`"
      echo "ap num: $ap_num, $online_aps"    
      [ "$online_aps" -ge "$ap_num" ] && break
      [ "`expr $end_time - $init_time`" -gt "$waiting_time" ] && exit 1
    
      sleep $interval
    done
  echo "end job:`date`"
}


check_ap_up_to_date() {
  local ap_num=$1

  # work dir
  cd $API_PERF_DIR/public_api/$API_PERF_VER

  init_time=`date +%s`
  waiting_time=$WAITING_TIME
  interval=10

  echo "start job:`date`"
    while true; do
      echo "ap up to date"
      ./login.sh admin "$ADMIN_PASSWORD"
      echo "start to check ap up-to-date time:`date`"

      ./count_ap.sh
      up_to_date_aps=`./count_ap.sh`

      end_time=`date +%s`
      echo "end to check ap up-to-date time:`date`"
      echo "ap num: $ap_num, $up_to_date_aps"    
      [ "$up_to_date_aps" -ge "$ap_num" ] && break
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
total_ap_num=0
done_file=$VAR_DIR/input/sim/ap_done

# run
cd $VAR_DIR/input/sim

if [ ! -f $SIM_INPUT ]; then
  echo "no found $SIM_INPUT"
  exit 1
fi

sim_number=`cat $SIM_INPUT | wc -l`
touch $done_file

echo "start job:`date`"

for sim_config_dir in `seq $sim_number`; do
  cd $VAR_DIR/input/sim
  sim_pc=`sed -n ${sim_config_dir}p $SIM_INPUT | awk '{print \$2}'`
  if [ -d $sim_config_dir ]; then
    echo "$sim_pc config"

    res=`grep "^${sim_config_dir}\$" $done_file >& /dev/null && echo "True" || echo "False"`
    if [ "$res" == "False" ]; then
      create_ap_cfg $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg
      join_sim_ap $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg
      echo "$sim_config_dir" >> $done_file
    fi

    ap_num=`awk -F= '/APNUM/{print \$2}' $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg`
    total_ap_num=`expr $total_ap_num + $ap_num`
    check_ap_online $total_ap_num
    check_ap_up_to_date $total_ap_num
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