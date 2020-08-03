#!/bin/bash

create_ap_cfg() {
  local apsim_cfg=$1

  sed s/SZIP=.*/SZIP=${SZ_IP}/ ${apsim_cfg}.template > ${apsim_cfg}
  sed -i s/FWVER=.*/FWVER=AP_VERSION/ ${apsim_cfg}
  sed -i s/MODEL=.*/MODEL=${AP_MODEL}/ ${apsim_cfg}
}


join_sim_ap() {
  local apsim_cfg=$1
  local sim_pc=$2
  local ssh_timeout=10

  echo "start to join ap time:`date`"

  echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=${ssh_timeout} ${apsim_cfg} ${SIM_USER}@${sim_pc}:/tmp/apsim.cfg"
  scp -o UserKnownHostsFile=/dev/null -o ConnectTimeout=${ssh_timeout} -o StrictHostKeyChecking=no ${apsim_cfg} ${SIM_USER}@${sim_pc}:/tmp/apsim.cfg
  echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=${ssh_timeout} ${SIM_USER}@${sim_pc} 'sudo /root/run_sim.sh /tmp/apsim.cfg'"
  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=${ssh_timeout} ${SIM_USER}@${sim_pc} 'sudo /root/run_sim.sh /tmp/apsim.cfg'

  echo "end to join ap time:`date`"
}


associate_sim_ue() {
  local uesim_cfg=$1
  local sim_pc=$2
  local ssh_timeout=10

  echo "start to associate ue time:`date`"

  echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${uesim_cfg} ${SIM_USER}@${sim_pc}:/tmp/ue_open.conf"
  scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${uesim_cfg} ${SIM_USER}@${sim_pc}:/tmp/ue_open.conf
  echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${SIM_USER}@${sim_pc} 'sudo /root/run_madue.sh /tmp/ue_open.conf'"
  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${SIM_USER}@${sim_pc} 'sudo /root/run_madue.sh /tmp/ue_open.conf'

  echo "end to associate ue time:`date`"
}
