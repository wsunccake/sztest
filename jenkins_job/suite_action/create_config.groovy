node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0'),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: ''),
                        string(name: 'SZ_IP', defaultValue: '1.2.3.4', description: ''),
                        string(name: 'AP_NUM', defaultValue: '1', description: ''),
                        string(name: 'UE_NUM', defaultValue: '1', description: ''),
                        string(name: 'DPSK_AMOUNT', defaultValue: "1", description: ''),

                        string(name: 'NPROC', defaultValue: '8', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    stage('Create Domain') {
        build job: 'create_domain',
                parameters: [
                        string(name: 'version', value: "${version}"),
                        string(name: 'scenario', value: "${scenario}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                ],
                propagate: false
    }

    stage('Analyze Domain') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "domains"),
        ]
    }

    stage('Create Zone') {
        build job: 'create_zone',
                parameters: [
                        string(name: 'version', value: "${version}"),
                        string(name: 'scenario', value: "${scenario}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                ],
                propagate: false
    }

    stage('Analyze Zone') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "zones"),
        ]
    }

    stage('Create Open WLAN') {
        build job: 'create_open_wlan',
                parameters: [
                        string(name: 'version', value: "${version}"),
                        string(name: 'scenario', value: "${scenario}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                ],
                propagate: false
    }

    stage('Create DPSK WLAN') {
        build job: 'create_dpsk_wlan', parameters: [string(name: 'version', value: "${version}"),
                                                    string(name: 'scenario', value: "${scenario}"),
                                                    string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                    string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                    string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Analyze WLAN') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "wlans"),
        ]
    }

    stage('Create DPSK ') {
        build job: 'create_dpsk_batch', parameters: [string(name: 'version', value: "${version}"),
                                                     string(name: 'scenario', value: "${scenario}"),
                                                     string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                     string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                     string(name: 'SZ_IP', value: "${SZ_IP}"),
                                                     string(name: 'DPSK_AMOUNT', value: "${DPSK_AMOUNT}"),
        ]
    }

    stage('Analyze DPSK') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "wlans/dpsk"),
        ]
    }

    stage('Create WLAN Group') {
        build job: 'create_wlan_group',
                parameters: [
                        string(name: 'version', value: "${version}"),
                        string(name: 'scenario', value: "${scenario}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                ],
                propagate: false
    }

    stage('Analyze WLAN Group') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "wlan_groups"),
        ]
    }

    stage('Pre-Provision AP') {
        build job: 'create_ap',
                parameters: [
                        string(name: 'version', value: "${version}"),
                        string(name: 'scenario', value: "${scenario}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                ],
                propagate: false
    }

    stage('Analyze AP') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "aps"),
        ]
    }

    stage('Create AP Group') {
        build job: 'create_ap_group_per_zone',
                parameters: [
                        string(name: 'version', value: "${version}"),
                        string(name: 'scenario', value: "${scenario}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                ],
                propagate: false
    }

    stage('Analyze AP Group') {
        build job: 'statistics_performance',
                parameters: [
                        string(name: 'version', value: "${version}"),
                        string(name: 'scenario', value: "${scenario}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'VAR_DATA', value: "ap_groups"),
                ],
                propagate: false
    }

    stage('Arrange Data') {
        build job: 'pickup_data', parameters: [string(name: 'version', value: "${version}"),
                                               string(name: 'scenario', value: "${scenario}"),
                                               string(name: 'VAR_DIR', value: "${VAR_DIR}"),
        ]
    }

}
