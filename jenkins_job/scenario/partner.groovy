def szIP

node {
    properties([
            parameters([
                    string(name: 'SZ_VERSION', defaultValue: '5.2.1.0'),
                    string(name: 'SCENARIO', defaultValue: 'partner'),
                    string(name: 'AP_VERSION', defaultValue: '5.2.1.0'),
                    string(name: 'SRC_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${SCENARIO}', description: ''),
                    string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: ''),
                    string(name: 'GCE_IMAGE', defaultValue: 'vscg-cloud-${SZ_VERSION}', description: ''),
                    string(name: 'API_PERF_VER', defaultValue: 'v9_1', description: ''),

                    string(name: 'SZ_NUM', defaultValue: '1', description: ''),
                    string(name: 'AP_NUM', defaultValue: '10000', description: ': group1: 6000, group2: 2000, group3: 2000'),
                    string(name: 'UE_NUM', defaultValue: '100000', description: ' group1: 48000, group2: 48000, group3: 4000'),
                    string(name: 'MADSZ_TGZ', defaultValue: 'madSZ-v5.2.1-14-u1804.tar.xz  ', description: ''),

                    string(name: 'DATA_DIR', defaultValue: '/usr/share/nginx/html/api_perf/5.2/report/${SCENARIO}', description: ''),

                    string(name: 'is_skip_join', defaultValue: 'false', description: ''),
                    string(name: 'is_skip_query', defaultValue: 'false', description: ''),
                    string(name: 'is_skip_csv', defaultValue: 'false', description: ''),
                    string(name: 'is_clean_env', defaultValue: 'true', description: ''),

                    string(name: 'NUM_CLIENT', defaultValue: '2', description: ''),
                    string(name: 'HATCH_RATE', defaultValue: '1', description: ''),
                    string(name: 'RUN_TIME', defaultValue: '20m1s', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"

    stage('Prepare Var Dir') {
        build job: 'prepare_copy_var_dir',
              parameters: [
                      string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                      string(name: 'SCENARIO', value: "${SCENARIO}"),
                      string(name: 'SRC_DIR', value: "${SRC_DIR}"),
                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
              ],
              propagate: false
    }

    stage('Setup SZ') {
        build job: 'suite_sz_setup',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_NUM', value: "${SZ_NUM}"),
                        string(name: 'CLUSTER_NAME', value: "partner-${SCENARIO}"),
                        string(name: 'GCE_IMAGE', value: "${GCE_IMAGE}", description: ''),
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

    stage('Create Config Per Partner Domain') {
        build job: 'suite_partner_config_per_partner_domain',
              parameters: [
                      string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                      string(name: 'SCENARIO', value: "${SCENARIO}"),
                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                      string(name: 'SZ_IP', value: "${szIP}"),
              ]
    }

    stage('Create Config Per Zone') {
        build job: 'suite_partner_config_per_zone',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${szIP}"),
                ]
    }

    stage('Prepare ID Data') {
        build job: 'partner_prepare_id_data',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${szIP}"),
                ]
    }

    stage('Join AP and UE') {
        if (params.is_skip_join == "false") {
            build job: 'join_ap_ue', parameters: [string(name: 'version', value: "${SZ_VERSION}"),
                                                  string(name: 'scenario', value: "${SCENARIO}"),
                                                  string(name: 'ap_version', value: "${ap_version}"),
                                                  string(name: 'SRC_DIR', value: "${SRC_DIR}"),
                                                  string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                  string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                  string(name: 'SZ_IP', value: "${szIP}"),
                                                  string(name: 'AP_NUM', value: "${AP_NUM}"),
                                                  string(name: 'UE_NUM', value: "${UE_NUM}"),
                                                  string(name: 'MADSZ_TGZ', value: "${MADSZ_TGZ}"),
            ]
        } else {
            echo "Skip to Join AP and UE"
        }
    }

    stage('Test Query API') {
        if (params.is_skip_query == "false") {
            build job: 'query_api', parameters: [string(name: 'version', value: "${SZ_VERSION}"),
                                                 string(name: 'scenario', value: "${SCENARIO}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                 string(name: 'TASK_DIR', value: 'phase1'),
                                                 string(name: 'SZ_IP', value: "${szIP}"),
                                                 string(name: 'NUM_CLIENT', value: "${NUM_CLIENT}"),
                                                 string(name: 'HATCH_RATE', value: "${HATCH_RATE}"),
                                                 string(name: 'RUN_TIME', value: "${RUN_TIME}"),

            ]
        } else {
            echo "Skip to Test Query API"
        }
    }

    stage('Create CSV') {
        if (params.is_skip_csv == "false") {
            build job: 'create_csv', parameters: [string(name: 'version', value: "${SZ_VERSION}"),
                                                  string(name: 'scenario', value: "${SCENARIO}"),
                                                  string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                  string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                  string(name: 'DATA_DIR', value: "${DATA_DIR}"),

            ]
        } else {
            echo "Skip to Create CSV"
        }
    }

    stage('Clean Env') {
        if (params.is_clean_env == "true") {
            build job: 'clean_env', parameters: [string(name: 'version', value: "${SZ_VERSION}"),
                                                 string(name: 'scenario', value: "${SCENARIO}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                 string(name: 'SZ_IP', value: "${szIP}"),
            ]
        } else {
            echo "Skip to Clean Env"
        }
    }
}
