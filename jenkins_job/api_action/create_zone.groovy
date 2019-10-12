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

        stage('Create Zone') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/zones

# run
echo "start job:`date`"
for domain_name in `cat $VAR_DIR/input/domains/domains.inp`; do

  # get domain_id
  domain_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/domains/$domain_name.out`
  echo "domain: $domain_name, $domain_id"
  # login
  ./login.sh admin "$ADMIN_PASSWORD"
  
  # create zone
  for zone_name in `cat $VAR_DIR/input/zones/$domain_name.inp`; do
    echo "start time:`date`" 
    ./create_zone.sh $zone_name $domain_id | tee $VAR_DIR/output/zones/$zone_name.out
    echo "end time:`date`"
  done

done
echo "end job:`date`"
'''
            }
        }
    }
}
