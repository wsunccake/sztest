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
        string(name: 'UE_NUM', defaultValue: '1', description: '')
        string(name: 'WAITING_TIME', defaultValue: '600', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Count UE') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER


# run
init_time=`date +%s`
waiting_time=$WAITING_TIME
interval=10

echo "start job:`date`"
  while true; do
    ./login.sh admin "$ADMIN_PASSWORD"
    echo "start time:`date`"

    ./monitor_client.sh
    online_ues=`./monitor_client.sh | awk '/onlineCount/ {print \$2}' | tr -d ,`
    echo "end time:`date`"

    end_time=`date +%s`
    echo "end time:`date`"
    echo "ue num: $UE_NUM, $online_ues"    
    [ "$online_ues" -ge "$UE_NUM" ] && break
    [ "`expr $end_time - $init_time`" -gt "$waiting_time" ] && exit 1
    
    sleep $interval
  done
echo "end job:`date`"
'''
            }
        }
    }
}
