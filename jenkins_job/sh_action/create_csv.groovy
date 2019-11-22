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


VAR_FILES=("domains" "zones" "wlans" "wlan_groups" "aps" "ap_groups")

mkdir -p $DATA_DIR/create

echo "create api data:"
for var_file in ${VAR_FILES[*]}; do
  echo "data file: $VAR_DIR/output/response_time/$var_file -> ${var_file}.csv"
  echo "$API_PERF_DIR/util/statistics.awk $VAR_DIR/output/response_time/${var_file}.log | $API_PERF_DIR/util/post_created_data.awk"
  tmp_data=`$API_PERF_DIR/util/statistics.awk $VAR_DIR/output/response_time/${var_file}.log | $API_PERF_DIR/util/post_created_data.awk`
  echo "$version,$tmp_data"
  echo "$version,$tmp_data" >> $DATA_DIR/create/${var_file}.csv
done
'''
            }
        }

        stage('Make Query CSV') {
            steps {
                sh '''#!/bin/bash
set -e


VAR_FILES=("01_query_wlan" "02_query_dpsk" "03_query_ap" "04_rkszones_zoneId_wlan" "05_rkszones" \\
"06_rkszones_id_apgroups" "07_rkszones_id_wlangroups" "09_domain_id_subdomain" "10_domain" "11_aps")

mkdir -p $DATA_DIR/query

echo "query api data:"
for var_file in ${VAR_FILES[*]}; do
  echo "data dir: $VAR_DIR/output/api_performance/$var_file -> ${var_file#*_}.csv"
  echo "grep -Pzo 'Running teardowns[\\s\\S]+Percentage' $VAR_DIR/output/api_performance/${var_file}.log \\
  | grep -Pzo '\\-[\\s\\S]+[\\-]' | grep -v '-' | grep -v 'session' | awk '{printf "%d,%d,%d,%d\\n", \\$5, \\$6, \\$7, \\$9}\'"
  tmp_data=`grep -Pzo 'Running teardowns[\\s\\S]+Percentage' $VAR_DIR/output/api_performance/${var_file}.log \\
  | grep -Pzo '\\-[\\s\\S]+[\\-]' | grep -v '-' | grep -v 'session' | awk '{printf "%d,%d,%d,%d\\n", \$5, \$6, \$7, \$9}'`
  echo "$version,$tmp_data"
  echo "$version,$tmp_data" >> $DATA_DIR/query/${var_file#*_}.csv
done
'''
            }
        }
    }
}
