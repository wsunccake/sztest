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

        stage('Create Authentication') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

# setup sz ip
if [ -z $SZ_IP ]; then
  SZ_IP=`sed -n 1p $VAR_DIR/input/sz/sz.inp`
fi 

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/auth


# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/non_proxy_acct

# radius
secret=`cat $VAR_DIR/input/radius/secret`
radus_num=`wc -l $VAR_DIR/input/radius/radius.inp | awk '{print \$1}'`
#radius_ip=`sed -n ${i}p $VAR_DIR/input/radius/radius.inp | awk '{print \$2}'`
radius_ip=`sed -n 1p $VAR_DIR/input/radius/radius.inp | awk '{print \$2}'`

# run
echo "start job:`date`"
for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do

  # get zone_id
  zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
  echo "zone: $zone_name, $zone_id"
  ./login.sh admin "$ADMIN_PASSWORD"

  # create non proxy auth
  i=1
  for acct_name in `cat $VAR_DIR/input/non_proxy_acct/$zone_name.inp`; do
    echo "start time:`date`"
    echo "$acct_name $radius_ip $zone_id"
    ./create_non_proxy_acct_service.sh $acct_name $radius_ip 1813 $secret $zone_id | tee $VAR_DIR/output/non_proxy_acct/${zone_name}_${acct_name}.out
    echo "end time:`date`"
  done
done
echo "end job:`date`"
'''
            }
        }
    }
}