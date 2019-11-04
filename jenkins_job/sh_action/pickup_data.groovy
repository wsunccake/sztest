pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'EXPECT_DIR', defaultValue: '/var/lib/jenkins/expect', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} #${currentBuild.number}"
                }

            }
        }

        stage('Figure Performance') {
            steps {
                sh '''#!/bin/bash
set -e


VAR_DIRS=("domains" "zones" "wlans" "wlan_groups" "aps" "ap_groups")

mkdir -p $VAR_DIR/output/response_time
mkdir -p $VAR_DIR/output/id

for var_dir in ${VAR_DIRS[*]}; do
  echo "data dir: $VAR_DIR/output/$var_dir"
  if [ -d $VAR_DIR/output/$var_dir ]; then
    find $VAR_DIR/output/$var_dir -name \\*.out -maxdepth 1 -exec grep -A1 'Response code: 201' {} \\; \\
    | awk '/Response time:/ {print \$3}' \\
    > $VAR_DIR/output/response_time/${var_dir}.log
    
    if [ "x$var_dir" == "xdomains" ] || [ "x$var_dir" == "xzones" ]; then
      find $VAR_DIR/output/$var_dir -name \\*.out -maxdepth 1 -exec grep -B1 'Response code: 201' {} \\; \\
      | awk -F\\" '/id/ {print \$4}' \\
      > $VAR_DIR/output/id/${var_dir}.log
    fi
    
    tar zcf ${VAR_DIR}/output/${var_dir}.tgz -C ${VAR_DIR}/output/${var_dir} .
    rm -rf ${VAR_DIR}/output/${var_dir}
  fi
done
'''
            }
        }
    }
}

