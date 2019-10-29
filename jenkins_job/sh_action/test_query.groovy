
pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')

        string(name: 'NUM_CLIENT', defaultValue: '1', description: '')
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
echo "ZONE_ID_FILE: ${VAR_DIR}/output/id/zones.log"
echo "DOMAIN_ID_FILE: ${VAR_DIR}/output/id/domains.log"


tasks=("01_query_wlan.py" "02_query_dpsk.py" "03_query_ap.py" "04_rkszones_zoneId_wlan.py" "05_rkszones.py" "06_rkszones_id_apgroups.py" "07_rkszones_id_wlangroups.py" "09_domain_id_subdomain.py" "10_domain.py" "11_aps.py")

                              
for t in ${tasks[*]}; do
  echo ${t%.*}
  echo "./bin/run.sh -H https://${SZ_IP}:8443 -f task/${t} --no-web -c${NUM_CLIENT} -r${HATCH_RATE} -t${RUN_TIME}"
 ./bin/run.sh -H https://${SZ_IP}:8443 -f task/${t} --no-web -c${NUM_CLIENT} -r${HATCH_RATE} -t${RUN_TIME} |& tee ${VAR_DIR}/output/${t%.*}.log
done
'''
            }
        }
    }
}
