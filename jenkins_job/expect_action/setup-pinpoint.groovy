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
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Setup PinPoint') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

# setup sz ip
if [ -z $SZ_IP ]; then
  SZ_IP=`sed -n 1p $VAR_DIR/input/sz/sz.inp`
fi 

export SZ_IP=$SZ_IP
export CLUSTER_NAME=$CLUSTER_NAME
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, CLUSTER_NAME: $CLUSTER_NAME"
load_setup_pinpoint_variable
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER

# get sn
./login.sh admin "$ADMIN_PASSWORD"
SN=`./get_sn.sh`
echo "SN: $SN"

# get passphrase
PASSPHRASE=`curl ${SESAME2_URL}${SN} | awk '/Access Key/{print \\$3}' | tr -d \\'`
export PASSPHRASE=$PASSPHRASE


echo "setup pippoint"
$EXPECT_DIR/sz/common/setup-pinpoint.exp


init_time=`date +%s`
waiting_time=$WAITING_TIME
interval=10

# check in-service
echo "start job:`date`"
  while true; do
    echo "start time:`date`"

    ./login.sh admin "$ADMIN_PASSWORD"
    is_in_service=`./login.sh admin "$ADMIN_PASSWORD" | grep 'Response code: 200' >& /dev/null && echo "true" || echo "false"`

    end_time=`date +%s`
    echo "end time:`date`"
    [ "x$is_in_service" == "xtrue" ] && break
    [ "`expr $end_time - $init_time`" -gt "$waiting_time" ] && exit 1

    sleep $interval
  done
  
  ./login.sh admin "$ADMIN_PASSWORD"
echo "end job:`date`"
'''
            }
        }
    }
}
