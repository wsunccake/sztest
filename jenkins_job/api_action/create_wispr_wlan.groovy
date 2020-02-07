pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'EXPECT_DIR', defaultValue: '/var/lib/jenkins/expect', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')
        string(name: 'API_PERF_VER', defaultValue: 'v9_1', description: '')

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

        stage('Create WISPr WLAN') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/wlans


auth_name=`sed -n 1p $VAR_DIR/input/auth/auth.inp | awk '{print \$1}'`
acct_name=`sed -n 1p $VAR_DIR/input/acct/acct.inp | awk '{print \$1}'`

# run
echo "start job:`date`"
for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do

  # get zone_id
  zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
  echo "zone: $zone_name, $zone_id"
  ./login.sh admin "$ADMIN_PASSWORD"

  # create wlan
  num=`grep wispr_wlan $VAR_DIR/input/wlans/$zone_name.inp | wc -l | awk '{print \$1}'`
  
  for n in `seq $num`; do
    echo "start time:`date`"
    wlan_name=`grep wispr_wlan $VAR_DIR/input/wlans/$zone_name.inp | sed -n ${n}p`
    hotspot_name=`sed -n ${n}p $VAR_DIR/input/hotspot/$zone_name.inp`
    
    echo "$wlan_name $zone_id $hotspot_name"
    ./create_wispr_wlan.sh $wlan_name $zone_id $hotspot_name $auth_name $acct_name | tee $VAR_DIR/output/wlans/${zone_name}_${wlan_name}.out
    echo "end time:`date`"
  done
  
done
echo "end job:`date`"
'''
            }
        }
    }
}
