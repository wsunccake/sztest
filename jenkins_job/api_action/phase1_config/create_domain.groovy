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

        stage('Create Domain') {
            steps {
                sh '''#!/bin/bash
source $VAR_DIR/input/default/api_util_var.sh
source ./util/api_util.sh
source ./util/test_api/phase1.sh

setup_api_util_var
export -f create_domain

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/domains

NEW_INPUT=domains.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

cp $VAR_DIR/input/domains/domains.inp $TMP_DIR/$NEW_INPUT
split -l $INPUT_NUMBER $TMP_DIR/$NEW_INPUT $TMP_DIR/in_

# run
for f in `ls $TMP_DIR/in_*`; do
  # login
  pubapi_login $SZ_USERNAME $SZ_PASSWORD
  
  cat $f | xargs -P $NPROC -i sh -c "create_domain {} | tee $VAR_DIR/output/domains/{}.out"
  
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
                    def result = util.checkResponseStatus "${VAR_DIR}/output/domains"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/domains"
                }
            }
        }

    }
}
