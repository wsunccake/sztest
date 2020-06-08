pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'GCE_IMAGE', defaultValue: 'vscg-${version}', description: '')
        string(name: 'SZ_FILE', defaultValue: 'sz.inp', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Startup SZ') {
            steps {
                sh '''#!/bin/bash
source $VAR_DIR/input/gce/gce.sh

GCE_IMAGE=`echo "$GCE_IMAGE" | sed s'/\\./-/'g`
vm_name=${GCE_IMAGE}-${ACCOUNT%%.*}-${RANDOM}

gcloud compute instances create $vm_name --zone=$GCE_ZONE \\
--image-project=$GCE_IMAGE_PROJECT --image=$GCE_IMAGE \\
--custom-cpu=$GCE_CPU --custom-memory=$GCE_MEM \\
--boot-disk-size=$GCE_DISK_SIZE --boot-disk-type=$GCE_DISK_TYPE \\
--tags=$GCE_TAG


sz_ip=`gcloud compute instances describe $vm_name | awk '/networkIP/ {print \$2}'`

mkdir -p $VAR_DIR/input/sz
echo -e "${vm_name}\\t${sz_ip}" > $VAR_DIR/input/sz/$SZ_FILE
echo -e "${vm_name}\\t${sz_ip}" >> $VAR_DIR/input/sz/cluster.inp

for i in `seq 20`; do
  echo "$i: ping -c3 -W5 ${sz_ip}"
  ping -c3 -W5 ${sz_ip}
  is_ping=`ping -c3 -W5 ${sz_ip} >& /dev/null && echo "true" || echo "false"`
  [ "x${is_ping}" == "xtrue" ] && break
  sleep 10
done

echo "is ping: ${is_ping}"

[ "x${is_ping}" == "xfalse" ] && exit 1 || exit 0
'''
            }
        }
    }
}
