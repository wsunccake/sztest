pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'EXPECT_DIR', defaultValue: '/var/lib/jenkins/expect', description: '')
        string(name: 'EXPECT_VER', defaultValue: '5.2', description: '')
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

        stage('Fresh Install') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

# setup sz ip
if [ -z $SZ_IP ]; then
  SZ_IP=`sed -n 1p $VAR_DIR/input/sz/sz.inp`
fi 

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, CLUSTER_NAME: $CLUSTER_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER

./login.sh admin "$ADMIN_PASSWORD"
./update_local_license_server.sh $LOCAL_LICENSE_SERVER_IP
SN=`./get_sn.sh`
echo "SN: $SN"


cd $API_PERF_DIR/util
env LICENSE_SERVER_IP=$LOCAL_LICENSE_SERVER_IP ./local_license_server_add_entry.sh $SN CAPACITY-AP 1.0 10000
'''
            }
        }
    }
}