pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')

        string(name: 'RADIUS_IMAGE', defaultValue: 'ubuntu-minimal-1804-bionic-v20191024', description: '')
        string(name: 'RADIUS_IMAGE_PROJECT', defaultValue: 'ubuntu-os-cloud', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Launch Radius') {
            steps {
                sh '''#!/bin/bash
source $VAR_DIR/input/gce/gce.sh

TMP_DATE=`date +%s`
RADIUS_USER=jenkins


check_pingable() {
  local host_ip=$1
  for i in `seq 20`; do
    echo "$i: ping -c3 -W5 ${host_ip}"
    ping -c3 -W5 ${host_ip}
    is_ping=`ping -c3 -W5 ${host_ip} >& /dev/null && echo "true" || echo "false"`
    [ "x${is_ping}" == "xtrue" ] && break
    sleep 10
  done

  echo "${host_ip} is ping: ${is_ping}"
}


# launch radius
launch_radius() {
  radius_num=`find $VAR_DIR/input/radius -maxdepth 1 -type d | wc -l`
  radius_num=`expr $radius_num - 1`
  for i in `seq $radius_num`; do
    vm_name=radius-${ACCOUNT%%.*}-${RANDOM}
    if [ -f $VAR_DIR/input/sz/sz.inp ]; then
      vm_name=radius`awk '{print \$1}' $VAR_DIR/input/sz/sz.inp | sed s/vscg//`
    fi
    vm_name=${vm_name}-${i}
  
    echo "gcloud compute instances create $vm_name --zone=$GCE_ZONE \\
    --machine-type=n1-standard-2 --image-project=$RADIUS_IMAGE_PROJECT --image=$RADIUS_IMAGE \\
    --boot-disk-size=50GB --boot-disk-type=pd-ssd \\
    --tags=$GCE_TAG"

    gcloud compute instances create $vm_name --zone=$GCE_ZONE \\
    --machine-type=n1-standard-2 --image-project=$RADIUS_IMAGE_PROJECT --image=$RADIUS_IMAGE \\
    --boot-disk-size=50GB --boot-disk-type=pd-ssd \\
    --tags=$GCE_TAG

    radius_ip=`gcloud compute instances describe $vm_name | awk '/networkIP/ {print \$2}'`

    check_pingable ${radius_ip}
    echo -e "${vm_name}\\t${radius_ip}" >> $VAR_DIR/input/radius/radius.inp
    echo -e "${vm_name}\\tansible_connection=ssh\\tansible_ssh_host=${radius_ip}\\tansible_ssh_port=22\\tansible_ssh_user=${RADIUS_USER}
" >> $API_PERF_DIR/util/playbook/$TMP_DATE

    echo "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $VAR_DIR/input/radius/mac_auth.conf $RADIUS_USER@$radius_ip:/tmp/mac_auth.conf"
    scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $VAR_DIR/input/radius/mac_auth.conf $RADIUS_USER@$radius_ip:/tmp/mac_auth.conf
    echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $RADIUS_USER@$radius_ip 'sudo mkdir -p /etc/freeradius/3.0/mods-config/files'"
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $RADIUS_USER@$radius_ip 'sudo mkdir -p /etc/freeradius/3.0/mods-config/files'
    echo "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $RADIUS_USER@$radius_ip 'sudo cp /tmp/mac_auth.conf /etc/freeradius/3.0/mods-config/files/mac_auth.conf'"
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $RADIUS_USER@$radius_ip 'sudo cp /tmp/mac_auth.conf /etc/freeradius/3.0/mods-config/files/mac_auth.conf'

  done
}


# run radius playbook
run_radius_playbook() {
echo "[radius]" >> $API_PERF_DIR/util/playbook/$TMP_DATE
awk '{print $1}' $VAR_DIR/input/radius/radius.inp >> $API_PERF_DIR/util/playbook/$TMP_DATE
cd $API_PERF_DIR/util/playbook

echo "ansible-playbook -i $TMP_DATE deploy.yaml -t radius -v"
ansible-playbook -i $TMP_DATE deploy.yaml -t radius -v

ansible_status=$?
echo "after ansible-playbook status: $ansible_status"

if [ "x$ansible_status" != "x0" ]; then
  echo "ansible-playbook again"
  ansible-playbook -i $TMP_DATE deploy.yaml -t radius -v
  echo "after ansible-playbook status: $?"
fi

echo "ansible radius -i $TMP_DATE -m command -a "sudo reboot" -v"
ansible radius -i $TMP_DATE -m command -a "sudo reboot" -v

sleep 300

echo "ansible radius -i $TMP_DATE -m command -a "uptime" -v"
ansible radius -i $TMP_DATE -m command -a "uptime" -v

for radius_ip in `awk '{print \$2}' $VAR_DIR/input/radius/radius.inp`; do
  check_pingable ${radius_ip}
done
}


###
### main
###

mkdir -p $VAR_DIR/input/radius
[ -f $VAR_DIR/input/radius/radius.inp ] && rm $VAR_DIR/input/radius/radius.inp

launch_radius
run_radius_playbook

#rm $API_PERF_DIR/util/playbook/$TMP_DATE
'''
            }
        }
    }
}
