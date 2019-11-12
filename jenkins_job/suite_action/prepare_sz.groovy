node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0'),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: ''),
                        string(name: 'SZ_IP', defaultValue: '1.2.3.4', description: ''),
                        string(name: 'CLUSTER_NAME', defaultValue: 'api-perf-${scenario}', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    stage('Fresh Install') {
        build job: 'fresh_install', parameters: [string(name: 'version', value: "${version}"),
                                                 string(name: 'scenario', value: "${scenario}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'SZ_IP', value: "${SZ_IP}"),
                                                 string(name: 'CLUSTER_NAME', value: "${CLUSTER_NAME}"),
        ]
    }

    try {
        stage('Configure Collectd') {
            build job: 'setup-collectd', parameters: [string(name: 'version', value: "${version}"),
                                                      string(name: 'scenario', value: "${scenario}"),
                                                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                      string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                      string(name: 'SZ_IP', value: "${SZ_IP}"),
                                                      string(name: 'CLUSTER_NAME', value: "${CLUSTER_NAME}"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    try {
        stage('Disable AP Cert Check') {
            build job: 'no_ap-cert-check', parameters: [string(name: 'version', value: "${version}"),
                                                        string(name: 'scenario', value: "${scenario}"),
                                                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                        string(name: 'SZ_IP', value: "${SZ_IP}"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    try {
        stage('Configure PinPoint') {
            build job: 'setup-pinpoint', parameters: [string(name: 'version', value: "${version}"),
                                                      string(name: 'scenario', value: "${scenario}"),
                                                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                      string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                      string(name: 'SZ_IP', value: "${SZ_IP}"),
                                                      string(name: 'CLUSTER_NAME', value: "${CLUSTER_NAME}"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    try {
        stage('Configure Local License Server') {
            build job: 'update_local_license_server', parameters: [string(name: 'version', value: "${version}"),
                                                                   string(name: 'scenario', value: "${scenario}"),
                                                                   string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                                   string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                                   string(name: 'SZ_IP', value: "${SZ_IP}"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }
}
