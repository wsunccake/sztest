def szIP

node {
    properties([
            parameters([string(name: 'version', defaultValue: '5.2.0.0'),
                        string(name: 'scenario', defaultValue: 'phase1'),
                        string(name: 'ap_version', defaultValue: '5.2.0.0'),
                        string(name: 'SRC_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: ''),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: ''),

                        string(name: 'AP_NUM', defaultValue: '10000', description: ': group1: 6000, group2: 2000, group3: 2000'),
                        string(name: 'UE_NUM', defaultValue: '100000', description: ' group1: 48000, group2: 48000, group3: 4000'),
                        string(name: 'DPSK_AMOUNT', defaultValue: "10", description: ''),
                        string(name: 'MADSZ_TGZ', defaultValue: 'madSZ-v5.2-39-u1804.tar.xz', description: ''),

                        string(name: 'DATA_DIR', defaultValue: '/usr/share/nginx/html/api_perf/5.2/report/${scenario}', description: ''),

                        string(name: 'is_skip_join', defaultValue: 'false', description: ''),
                        string(name: 'is_skip_query', defaultValue: 'false', description: ''),
                        string(name: 'is_skip_csv', defaultValue: 'false', description: ''),
                        string(name: 'is_clean_env', defaultValue: 'true', description: ''),

                        string(name: 'NUM_CLIENT', defaultValue: '2', description: ''),
                        string(name: 'HATCH_RATE', defaultValue: '1', description: ''),
                        string(name: 'RUN_TIME', defaultValue: '20m', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    stage('Prepare Var Dir') {
        build job: 'prepare_var_dir', propagate: false, parameters: [string(name: 'version', value: "${version}"),
                                                                     string(name: 'scenario', value: "${scenario}"),
                                                                     string(name: 'SRC_DIR', value: "${SRC_DIR}"),
                                                                     string(name: 'VAR_DIR', value: "${VAR_DIR}"),
        ]
    }

    stage('Launch SZ') {
        build job: 'launch_sz', parameters: [string(name: 'version', value: "${version}"),
                                             string(name: 'scenario', value: "${scenario}"),
                                             string(name: 'VAR_DIR', value: "${VAR_DIR}"),
        ]
    }

    stage('Setup SZ IP') {
        script {
            File szInp = new File("${VAR_DIR}/input/sz/sz.inp")
            szIP = szInp.readLines().get(0).split()[1]
            println "SZ Name: ${szInp.readLines().get(0).split()[0]}"
            println "SZ IP: ${szIP}"
        }
    }

    stage('Prepare SZ') {
        build job: 'prepare_sz', parameters: [string(name: 'version', value: "${version}"),
                                              string(name: 'scenario', value: "${scenario}"),
                                              string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                              string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                              string(name: 'SZ_IP', value: "${szIP}"),
                                              string(name: 'CLUSTER_NAME', value: "api-perf-${scenario}"),]
    }

    stage('Create Config') {
        build job: 'create_config', parameters: [string(name: 'version', value: "${version}"),
                                                 string(name: 'scenario', value: "${scenario}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                 string(name: 'SZ_IP', value: "${szIP}"),
                                                 string(name: 'AP_NUM', value: "${AP_NUM}"),
                                                 string(name: 'UE_NUM', value: "${UE_NUM}"),
                                                 string(name: 'DPSK_AMOUNT', value: "${DPSK_AMOUNT}"),
        ]
    }

    if (params.is_skip_join == "false") {
        stage('Join AP and UE') {
            build job: 'join_ap_ue', parameters: [string(name: 'version', value: "${version}"),
                                                  string(name: 'scenario', value: "${scenario}"),
                                                  string(name: 'ap_version', value: "${ap_version}"),
                                                  string(name: 'SRC_DIR', value: "${SRC_DIR}"),
                                                  string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                  string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                  string(name: 'SZ_IP', value: "${szIP}"),
                                                  string(name: 'AP_NUM', value: "${AP_NUM}"),
                                                  string(name: 'UE_NUM', value: "${UE_NUM}"),
                                                  string(name: 'MADSZ_TGZ', value: "${MADSZ_TGZ}"),
            ]
        }
    }

    if (params.is_skip_query == "false") {
        stage('Test Query API') {
            build job: 'query_api', parameters: [string(name: 'version', value: "${version}"),
                                                 string(name: 'scenario', value: "${scenario}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                 string(name: 'TASK_DIR', value: 'phase1'),
                                                 string(name: 'SZ_IP', value: "${szIP}"),
                                                 string(name: 'NUM_CLIENT', value: "${NUM_CLIENT}"),
                                                 string(name: 'HATCH_RATE', value: "${HATCH_RATE}"),
                                                 string(name: 'RUN_TIME', value: "${RUN_TIME}"),

            ]
        }
    }

    if (params.is_skip_csv == "false") {
        stage('Create CSV') {
            build job: 'create_csv', parameters: [string(name: 'version', value: "${version}"),
                                                  string(name: 'scenario', value: "${scenario}"),
                                                  string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                  string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                  string(name: 'DATA_DIR', value: "${DATA_DIR}"),

            ]
        }
    }

    if (params.is_clean_env == "true") {
        stage('Clean Env') {
            build job: 'clean_env', parameters: [string(name: 'version', value: "${version}"),
                                                 string(name: 'scenario', value: "${scenario}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                 string(name: 'SZ_IP', value: "${szIP}"),
            ]
        }
    }
}
