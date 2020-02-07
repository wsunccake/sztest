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

        stage('Create Accounting') {
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
mkdir -p $VAR_DIR/output/acct


# run
num=`wc -l $VAR_DIR/input/acct/acct.inp | awk '{print \$1}'`
echo "start job:`date`"

for n in `seq $num`; do
  ./login.sh admin "$ADMIN_PASSWORD"
  
  line=`sed -n ${n}p $VAR_DIR/input/acct/acct.inp`
  acct_name=`echo "$line" | awk '{print $1}'`
  acct_ip=`echo "$line" | awk '{print $2}'`
  acct_port=`echo "$line" | awk '{print $3}'`
  acct_secret=`echo "$line" | awk '{print $4}'`

  echo "start time:`date`"
  ./create_acct_service.sh $acct_name $acct_ip $acct_port $acct_secret | tee $VAR_DIR/output/acct/$acct_name.out
  echo "end time:`date`"
done

echo "end job:`date`"
'''
            }
        }
    }
}
