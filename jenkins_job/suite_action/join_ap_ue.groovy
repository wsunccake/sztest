node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0'),
                        string(name: 'ap_version', defaultValue: '2.0.0.0'),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: ''),
                        string(name: 'SZ_IP', defaultValue: '1.2.3.4', description: ''),
                        string(name: 'AP_NUM', defaultValue: '1', description: ''),
                        string(name: 'UE_NUM', defaultValue: '1', description: ''),
                        string(name: 'MADSZ_TGZ', defaultValue: 'madSZ-v5.2-38-u1804.tar.xz', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    stage('Launch SimPC') {
        build job: 'launch_sim_pc', parameters: [string(name: 'version', value: "${version}"),
                                                 string(name: 'scenario', value: "${scenario}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'MADSZ_TGZ', value: "${MADSZ_TGZ}"),
        ]
    }


    stage('Join AP') {
        build job: 'join_sim_ap', parameters: [string(name: 'version', value: "${version}"),
                                               string(name: 'scenario', value: "${scenario}"),
                                               string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                               string(name: 'SZ_IP', value: "${SZ_IP}"),
                                               string(name: 'AP_VER', value: "${ap_version}"),
        ]
    }

    try {
        stage('Count On Line AP') {
            build job: 'monitor_ap', parameters: [string(name: 'version', value: "${version}"),
                                                  string(name: 'scenario', value: "${scenario}"),
                                                  string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                  string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                  string(name: 'SZ_IP', value: "${SZ_IP}"),
                                                  string(name: 'AP_NUM', value: "${AP_NUM}"),
                                                  string(name: 'WAITING_TIME', value: "1800")
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    try {
        stage('Count Update-To-Date AP') {
            build job: 'monitor_ap_update-to-date', parameters: [string(name: 'version', value: "${version}"),
                                                                 string(name: 'scenario', value: "${scenario}"),
                                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                                 string(name: 'SZ_IP', value: "${SZ_IP}"),
                                                                 string(name: 'AP_NUM', value: "${AP_NUM}"),
                                                                 string(name: 'WAITING_TIME', value: "6000"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    stage('Associate UE') {
        build job: 'associate_sim_ue', parameters: [string(name: 'version', value: "${version}"),
                                                    string(name: 'scenario', value: "${scenario}"),
                                                    string(name: 'VAR_DIR', value: "${VAR_DIR}"),
        ]
    }

    try {
        stage('Count UE') {
            build job: 'monitor_client', parameters: [string(name: 'version', value: "${version}"),
                                                      string(name: 'scenario', value: "${scenario}"),
                                                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                      string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                      string(name: 'SZ_IP', value: "${SZ_IP}"),
                                                      string(name: 'UE_NUM', value: "${UE_NUM}"),
                                                      string(name: 'WAITING_TIME', value: "9000"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }
}
