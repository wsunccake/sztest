pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'VAR_DATA', defaultValue: 'domains', description: '')
        string(name: 'EXPECT_DIR', defaultValue: '/var/lib/jenkins/expect', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - ${VAR_DATA} #${currentBuild.number}"
                }

            }
        }

        stage('Figure Performance') {
            steps {
                sh '''#!/bin/bash
echo "data dir: $VAR_DIR/output/$VAR_DATA"
#grep -A1 'Response code: 201' $VAR_DIR/output/$VAR_DATA/*.out

echo "find $VAR_DIR/output/$VAR_DATA -name \\*.out -exec grep -A1 'Response code: 201' {} \\;"
find $VAR_DIR/output/$VAR_DATA -name \\*.out -exec grep -A1 'Response code: 201' {} \\;

echo "find $VAR_DIR/output/$VAR_DATA -name \\*.out -exec grep -A1 'Response code: 201' {} \\; | awk '/Response time:/ {print \\$3}'"
find $VAR_DIR/output/$VAR_DATA -name \\*.out -exec grep -A1 'Response code: 201' {} \\; | awk '/Response time:/ {print \\$3}'


echo "find $VAR_DIR/output/$VAR_DATA -name \\*.out -exec grep -A1 'Response code: 201' {} \\; \\
| awk '/Response time:/ {print \\$3}'"
find $VAR_DIR/output/$VAR_DATA -name \\*.out -exec grep -A1 'Response code: 201' {} \\; \\
| awk '/Response time:/ {print \\$3}'

echo "find $VAR_DIR/output/$VAR_DATA -name \\*.out -exec grep -A1 'Response code: 201' {} \\; \\
| awk '/Response time:/ {print \\$3}' \\
|  $API_PERF_DIR/util/statistics.awk"
find $VAR_DIR/output/$VAR_DATA -name \\*.out -exec grep -A1 'Response code: 201' {} \\; \\
| awk '/Response time:/ {print \\$3}' \\
|  $API_PERF_DIR/util/statistics.awk

'''
            }
        }
    }
}
