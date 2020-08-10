#!/bin/bash


create_ap_cfg() {
  local ap_sim_cfg=$1

  sed s/SZIP=.*/SZIP=${SZ_IP}/ ${ap_sim_cfg}.template > ${ap_sim_cfg}
  sed -i s/FWVER=.*/FWVER=${AP_VERSION}/ ${ap_sim_cfg}
  sed -i s/MODEL=.*/MODEL=${AP_MODEL}/ ${ap_sim_cfg}
}


join_sim_ap() {
  local ap_sim_cfg=$1
  local sim_pc=$2
  local ssh_timeout=10

  echo "start to join ap time:`date`"

  echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=${ssh_timeout} ${ap_sim_cfg} ${SIM_USER}@${sim_pc}:/tmp/apsim.cfg"
  scp -o UserKnownHostsFile=/dev/null -o ConnectTimeout=${ssh_timeout} -o StrictHostKeyChecking=no ${ap_sim_cfg} ${SIM_USER}@${sim_pc}:/tmp/apsim.cfg
  echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=${ssh_timeout} ${SIM_USER}@${sim_pc} 'sudo /root/run_sim.sh /tmp/apsim.cfg'"
  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=${ssh_timeout} ${SIM_USER}@${sim_pc} 'sudo /root/run_sim.sh /tmp/apsim.cfg'

  echo "end to join ap time:`date`"
}


associate_sim_ue() {
  local ue_cfg=$1
  local sim_pc=$2
  local ssh_timeout=10

  echo "start to associate ue time:`date`"

  echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${ue_cfg} ${SIM_USER}@${sim_pc}:/tmp/ue_open.conf"
  scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${ue_cfg} ${SIM_USER}@${sim_pc}:/tmp/ue_open.conf
  echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${SIM_USER}@${sim_pc} 'sudo /root/run_madue.sh /tmp/ue_open.conf'"
  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${SIM_USER}@${sim_pc} 'sudo /root/run_madue.sh /tmp/ue_open.conf'

  echo "end to associate ue time:`date`"
}


state_ap_ue() {
  local h=$1
  local ssh_timeout=3
  local is_ssh_connect=`ssh -vvv -o ConnectTimeout=${ssh_timeout} ${h} date >& ${VAR_DIR}/input/sim/${h}.log && echo true || echo false`

  if [ "${is_ssh_connect}" == "true" ]; then
    ssh ${h} 'sudo su - -c "cd /opt/madSZ/BUILD/scripts && ./madutil -v"' 2> /dev/null 1> ${VAR_DIR}/input/sim/${h}_ap.log
    ssh ${h} 'sudo su - -c "ls /dev/shm/sim/ue | xargs -i /opt/madSZ/BUILD/bin/dumpTool -s {}"' 2> /dev/null 1> ${VAR_DIR}/input/sim/${h}_ue.log
    echo "ssh connection pass: ${h} ue: `awk '/RUN/{print $2}' ${VAR_DIR}/input/sim/${h}_ue.log`"
  else
    echo "ssh connection fail: ${h}"
  fi
}
