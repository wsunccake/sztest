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

        stage('Create AP Registration Rule') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/ap_rule

# run
echo "start job:`date`"

for n in `ls $VAR_DIR/input/ap_rule/zone*.inp`; do
  zone_name=${n%.*}
  zone_name=${zone_name##*/}
  if [ -f $VAR_DIR/output/zones/$zone_name.out ]; then
    zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
    echo "zone: $zone_name, $zone_id"
    
    # login
    ./login.sh admin "$ADMIN_PASSWORD"

    # create ap_rule
    for ap_rule_name in `cat $VAR_DIR/input/ap_rule/$zone_name.inp`; do
      echo "start time:`date`"
      echo "$ap_rule_name $zone_id"
      ./create_ap_rule.sh $ap_rule_name $zone_id | tee $VAR_DIR/output/ap_rule/${zone_name}_${ap_rule_name}.out
      echo "end time:`date`"
    done
    
    # logout
    ./logout.sh

  fi

done

echo "end job:`date`"
'''
            }
        }
    }
}
