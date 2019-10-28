pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')

        string(name: 'MADSZ_IMAGE', defaultValue: 'ubuntu-minimal-1804-bionic-v20191024', description: '')
        string(name: 'MADSZ_IMAGE_PROJECT', defaultValue: 'ubuntu-os-cloud', description: '')
        string(name: 'MADSZ_TGZ', defaultValue: 'madSZ-v5.2-24-u1804.tar.xz', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Launch Sim') {
            steps {
                sh '''#!/bin/bash
source $VAR_DIR/input/gce/gce.sh

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

mkdir -p $VAR_DIR/input/sim
[ -f $VAR_DIR/input/sim/sim.inp ] && rm $VAR_DIR/input/sim/sim.inp

TMP_DATE=`date +%s`


# launch sim
sim_num=`find $VAR_DIR/input/sim -maxdepth 1 -type d | wc -l`
sim_num=`expr $sim_num - 1`
for i in `seq $sim_num`; do
  vm_name=simtool-${ACCOUNT%%.*}-${RANDOM}
  if [ -f $VAR_DIR/input/sz/sz.inp ]; then
    vm_name=simtool`awk '{print \$1}' $VAR_DIR/input/sz/sz.inp | sed s/vscg//`
  fi
  vm_name=${vm_name}-${i}

  echo "gcloud compute instances create $vm_name --zone=$GCE_ZONE \\
  --machine-type=n1-standard-8 --image-project=$MADSZ_IMAGE_PROJECT --image=$MADSZ_IMAGE \\
  --boot-disk-size=100GB --boot-disk-type=pd-ssd \\
  --tags=$GCE_TAG"

  gcloud compute instances create $vm_name --zone=$GCE_ZONE \\
  --machine-type=n1-standard-8 --image-project=$MADSZ_IMAGE_PROJECT --image=$MADSZ_IMAGE \\
  --boot-disk-size=100GB --boot-disk-type=pd-ssd \\
  --tags=$GCE_TAG

  madsz_ip=`gcloud compute instances describe $vm_name | awk '/networkIP/ {print \$2}'`

  check_pingable ${madsz_ip}
  echo -e "${vm_name}\\t${madsz_ip}" >> $VAR_DIR/input/sim/sim.inp
  echo -e "${vm_name}\\tansible_connection=ssh\\tansible_ssh_host=${madsz_ip}\\tansible_ssh_port=22\\tansible_ssh_user=${SIM_USER}\n" >> $API_PERF_DIR/util/playbook/$TMP_DATE
done

# run playbook
echo "[madsz]" >> $API_PERF_DIR/util/playbook/$TMP_DATE
awk '{print $1}' $VAR_DIR/input/sim/sim.inp >> $API_PERF_DIR/util/playbook/$TMP_DATE
cd $API_PERF_DIR/util/playbook

echo "ansible-playbook -i $TMP_DATE deploy.yaml -t madsz -e madsz_package=$MADSZ_TGZ -v"
ansible-playbook -i $TMP_DATE deploy.yaml -t madsz -e "madsz_package=$MADSZ_TGZ" -v

echo "ansible madsz -i $TMP_DATE -m command -a \"sudo reboot\" -v"
ansible madsz -i $TMP_DATE -m command -a "sudo reboot" -v

sleep 30

for madsz_ip in `awk '{print \$2}' $VAR_DIR/input/sim/sim.inp`; do
  check_pingable ${madsz_ip}
done

echo "ansible madsz -i $TMP_DATE -m command -a \"uptime\" -v"
ansible madsz -i $TMP_DATE -m command -a "uptime" -v

rm $API_PERF_DIR/util/playbook/$TMP_DATE
'''
            }
        }
    }
}
