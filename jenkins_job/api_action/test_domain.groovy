library identifier: 'dynamic-libary@util', retriever: modernSCM(
  [$class: 'GitSCMSource',
   remote: 'https://github.com/wsunccake/sztest.git'])

pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'EXPECT_DIR', defaultValue: '/var/lib/jenkins/expect', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')
        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'NPROC', defaultValue: '2', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

      stage('Check Response util') {
            steps {
                script {
                    def result = util.checkResponseStatus("${VAR_DIR}/output/partner_domains", "2")
                    println result
                    currentBuild.result = result
                    
                    util.statisticizeResponse("${VAR_DIR}/output/partner_domains", "20", "/var/lib/jenkins/api_perf/util/statistics.awk")
                }

            }
        }

    }
}