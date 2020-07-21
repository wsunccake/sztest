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

echo "export DOMAIN_ID_FILE=$DOMAIN_ID_FILE"
export DOMAIN_ID_FILE=${VAR_DIR}/output/id/domain_ids.log

echo "export AP_ZONE_DOMAIN_ID_FILE=$AP_ZONE_DOMAIN_ID_FILE"
export AP_ZONE_DOMAIN_ID_FILE=${VAR_DIR}/output/id/ap_zone_domain_id.log

# partner-update
partner_test_functions=(l2acl l3acp wifi_calling device_policy lbs application_policy_v2 user_defined proxy_auth proxy_acct vlan_pooling)
for f in ${partner_test_functions[@]}; do
  echo "export ${f^^}_FILE=$VAR_DIR/output/id/${f}_ids.log"
  export ${f^^}_FILE=$VAR_DIR/output/id/${f}_ids.log
done


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

