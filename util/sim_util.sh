#!/bin/bash
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

