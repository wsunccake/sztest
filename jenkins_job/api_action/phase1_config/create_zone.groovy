library identifier: 'dynamic-libary@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/wsunccake/sztest.git'])

pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: 'group0', description: '')
        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'NPROC', defaultValue: '2', description: '')
        string(name: 'API_VERSION', defaultValue: '', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Create Zone') {
            steps {
                sh '''#!/bin/bash
source $VAR_DIR/input/default/api_util_var.sh
source ./util/api_util.sh
source ./util/test_api/phase1.sh

setup_api_util_var
export -f create_zone

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/zones

NEW_INPUT=domain_zone.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

for domain_name in `cat $VAR_DIR/input/domains/domains.inp`; do
  # get domain_id
  domain_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/domains/$domain_name.out`
  
  # create zone
  for zone_name in `cat $VAR_DIR/input/zones/$domain_name.inp`; do
    if [ ! -z $domain_id ]; then
      echo "domain: $domain_name $domain_id zone: $zone_name" >> $TMP_DIR/$NEW_INPUT
    fi
  done
done

split -l $INPUT_NUMBER $TMP_DIR/$NEW_INPUT $TMP_DIR/in_
cp -fv $TMP_DIR/$NEW_INPUT $VAR_DIR/input/zones/.

# run
echo "start job:`date`"
for f in `ls $TMP_DIR/in_*`; do
  # login
  pubapi_login $SZ_USERNAME $SZ_PASSWORD
  
  # create ap per zone
  cat $f | xargs -n5 -P $NPROC sh -c './create_zone $4 $2 | tee $VAR_DIR/output/zones/$4.out'
    
  # logout
  pubapi_logout
done
echo "end job:`date`"

rm -rfv $TMP_DIR
'''
            }
        }

        stage('Check Response') {
            steps {
                script {
                    def result = util.checkResponseStatus "${VAR_DIR}/output/zones"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/zones"
                }
            }
        }

    }
}
