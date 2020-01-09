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

        stage('Delete AP') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/aps

# run
echo "start job:`date`"
for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do

  # get zone_id
  zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
  echo "zone: $zone_name, $zone_id"
  ./login.sh admin "$ADMIN_PASSWORD"
  
  # delete wlan
  for ap_mac in `cat $VAR_DIR/input/aps/$zone_name.inp`; do
    echo "start time:`date`"
    echo "$ap_mac $zone_id"
    ./delete_ap.sh $ap_mac "" "" $zone_id | tee $VAR_DIR/output/aps/$ap_mac.out
    echo "end time:`date`"
  done

done
echo "end job:`date`"
'''
            }
        }
    }
}
