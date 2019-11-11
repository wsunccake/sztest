node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0'),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'SZ_IP', defaultValue: '1.2.3.4', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    try {
        stage('Clean Local License Server') {
            build job: 'clean_local_license_server', parameters: [string(name: 'version', value: "${params.version}"),
                                                                  string(name: 'scenario', value: "${params.scenario}"),
                                                                  string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                                  string(name: 'SZ_IP', value: "${SZ_IP}"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    try {
        stage('Shutdown SZ') {
            build job: 'shutdown_sz', parameters: [string(name: 'version', value: "${params.version}"),
                                                   string(name: 'scenario', value: "${params.scenario}"),
                                                   string(name: 'VAR_DIR', value: "${VAR_DIR}"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    try {
        stage('Shutdown SimPC') {
            build job: 'shutdown_sim_pc', parameters: [string(name: 'version', value: "${params.version}"),
                                                       string(name: 'scenario', value: "${params.scenario}"),
                                                       string(name: 'VAR_DIR', value: "${VAR_DIR}"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }
}

