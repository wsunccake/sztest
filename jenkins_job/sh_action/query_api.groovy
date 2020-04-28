pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'TASK_DIR', defaultValue: 'phase1', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')
        string(name: 'API_PERF_VER', defaultValue: 'v9_1', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')

        string(name: 'NUM_CLIENT', defaultValue: '2', description: '')
        string(name: 'HATCH_RATE', defaultValue: '1', description: '')
        string(name: 'RUN_TIME', defaultValue: '20m', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Run Locust') {
            steps {
                sh '''#!/bin/bash
cd ${API_PERF_DIR}/util/locust_test
source venv/bin/activate
export ZONE_ID_FILE=${VAR_DIR}/output/id/zones.log
export DOMAIN_ID_FILE=${VAR_DIR}/output/id/domains.log
echo "DOMAIN_ID_FILE: ${VAR_DIR}/output/id/domains.log, ZONE_ID_FILE: ${VAR_DIR}/output/id/zones.log"

mkdir -p ${VAR_DIR}/output/api_performance


for py_file in `ls task/${TASK_DIR}/*.py`; do
  task=`basename ${py_file%.*}`
  echo ${task}
  echo "env PUBAPI_VERSION=$API_PERF_VER ./bin/run.sh -H https://${SZ_IP}:8443 -f ${py_file} --no-web -c${NUM_CLIENT} -r${HATCH_RATE} -t${RUN_TIME}"
  env PUBAPI_VERSION=$API_PERF_VER ./bin/run.sh -H https://${SZ_IP}:8443 -f ${py_file} --no-web -c${NUM_CLIENT} -r${HATCH_RATE} -t${RUN_TIME} |& tee ${VAR_DIR}/output/api_performance/${task}.log
done
'''
            }
        }
    }
}
