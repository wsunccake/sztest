pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')
        string(name: 'DATA_DIR', defaultValue: '/data/${scenario}', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} #${currentBuild.number}"
                }

            }
        }

        stage('Make Create CSV') {
            steps {
                sh '''#!/bin/bash
set -e


PHASE1_FILES=(domains zones wlans wlan_groups aps ap_groups)
PHASE2_FILES=(domains zones wlans aps hotspot \\
             non_proxy_acct non_proxy_auth guest_access \\
             dhcppool etherport client_isolation \\
             ap_rule subscription_package)
VAR_FILES=(${PHASE1_FILES[*]} ${PHASE2_FILES[*]})
VAR_FILES=(`echo "${VAR_FILES[*]}" | tr ' ' '\\n' | sort -u | tr '\\n' ' '`)

mkdir -p $DATA_DIR/create

echo "create api data:"
for var_file in ${VAR_FILES[*]}; do
  if [[ -f $VAR_DIR/output/response_time/${var_file}.log ]]; then
    echo "data file: $VAR_DIR/output/response_time/$var_file -> ${var_file}.csv"
    echo "$API_PERF_DIR/util/statistics.awk $VAR_DIR/output/response_time/${var_file}.log | $API_PERF_DIR/util/post_created_data.awk"
    tmp_data=`$API_PERF_DIR/util/statistics.awk $VAR_DIR/output/response_time/${var_file}.log | $API_PERF_DIR/util/post_created_data.awk`
    echo "$version,$tmp_data" >> $DATA_DIR/create/${var_file}.csv
    echo "$version,$tmp_data >> $DATA_DIR/create/${var_file}.csv"
  fi
done
'''
            }
        }

        stage('Make Query CSV') {
            steps {
                sh '''#!/bin/bash
set -e


PHASE1_FILES=(query_wlan query_dpsk query_ap rkszones_zoneId_wlan rkszones \\
              rkszones_id_apgroups rkszones_id_wlangroups domain_id_subdomain \\
              domain aps)
PHASE2_FILES=(query_criteria_client_isolation query_criteria_etherport query_criteria_guest_access \\
              query_criteria_hotspot query_criteria_non_proxy_acct query_criteria_non_proxy_auth \\
              query_criteria_subscriber_package query_criteria_wlan) 
VAR_FILES=(${PHASE1_FILES[*]} ${PHASE2_FILES[*]})
VAR_FILES=(`echo "${VAR_FILES[*]}" | tr ' ' '\\n' | sort -u | tr '\\n' ' '`)

mkdir -p $DATA_DIR/query

echo "query api data:"
for var_file in ${VAR_FILES[*]}; do
  if [[ -f $VAR_DIR/output/api_performance/${var_file}.log ]]; then
    echo "data dir: $VAR_DIR/output/api_performance/$var_file -> ${var_file#*_}.csv"
    echo "grep -Pzo 'Running teardowns[\\s\\S]+Percentage' $VAR_DIR/output/api_performance/${var_file}.log \\
    | grep -Pzo '\\-[\\s\\S]+[\\-]' | grep -v '-' | grep -v 'session' | awk '{printf "%d,%d,%d,%d\\n", \\$5, \\$6, \\$7, \\$9}\'"
    tmp_data=`grep -Pzo 'Running teardowns[\\s\\S]+Percentage' $VAR_DIR/output/api_performance/${var_file}.log \\
    | grep -Pzo '\\-[\\s\\S]+[\\-]' | grep -v '-' | grep -v 'session' | awk '{printf "%d,%d,%d,%d\\n", \$5, \$6, \$7, \$9}'`
    echo "$version,$tmp_data" >> $DATA_DIR/query/${var_file}.csv
    echo "$version,$tmp_data >> $DATA_DIR/query/${var_file}.csv"
  fi
done
'''
            }
        }
    }
}
