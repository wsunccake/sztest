def szIP

node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'phase2-group2'),
                        string(name: 'ap_version', defaultValue: '2.0.0.0'),
                        string(name: 'SRC_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: ''),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_1', description: ''),

                        string(name: 'AP_NUM', defaultValue: '10000', description: ': group1: 6000, group2: 2000, group3: 2000'),
                        string(name: 'UE_NUM', defaultValue: '100000', description: ' group1: 48000, group2: 48000, group3: 4000'),
                        string(name: 'MADSZ_TGZ', defaultValue: 'madSZ-v5.2-39-u1804.tar.xz', description: ''),

                        string(name: 'DATA_DIR', defaultValue: '/usr/share/nginx/html/api_perf/5.2.1/report/${scenario}', description: ''),

                        string(name: 'NUM_CLIENT', defaultValue: '2', description: ''),
                        string(name: 'HATCH_RATE', defaultValue: '1', description: ''),
                        string(name: 'RUN_TIME', defaultValue: '20m', description: ''),

                        string(name: 'GCE_IMAGE', defaultValue: 'vscg-cloud-${version}', description: '')
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
                                             string(name: 'GCE_IMAGE', value: "${GCE_IMAGE}"),
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

    stage('Fresh Install') {
        build job: 'fresh_install', parameters: [string(name: 'version', value: "${version}"),
                                                 string(name: 'scenario', value: "${scenario}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'SZ_IP', value: "${szIP}"),
                                                 string(name: 'CLUSTER_NAME', value: "alto-${scenario}"),
        ]
    }

    try {
        stage('Configure PinPoint') {
            build job: 'setup-pinpoint', parameters: [string(name: 'version', value: "${version}"),
                                                      string(name: 'scenario', value: "${scenario}"),
                                                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                      string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                      string(name: 'SZ_IP', value: "${szIP}"),
                                                      string(name: 'CLUSTER_NAME', value: "alto-${scenario}"),
            ]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

//    stage('Create Partner Domain') {
//        build job: 'create_partner_domain',
//                parameters: [
//                        string(name: 'version', value: "${version}"),
//                        string(name: 'scenario', value: "${scenario}"),
//                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                        string(name: 'SZ_IP', value: "${szIP}"),
//                ],
//                propagate: false
//    }
//
//    stage('Create Zone Per Partner Domain') {
//        build job: 'create_zone_per_partner_domain',
//                parameters: [
//                        string(name: 'version', value: "${version}"),
//                        string(name: 'scenario', value: "${scenario}"),
//                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                        string(name: 'SZ_IP', value: "${szIP}"),
//                ],
//                propagate: false
//    }
//
//    stage('Create Authentication Per Partner Domain') {
//        build job: 'create_auth_service_per_partner_domain',
//                parameters: [
//                        string(name: 'version', value: "${version}"),
//                        string(name: 'scenario', value: "${scenario}"),
//                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                        string(name: 'SZ_IP', value: "${szIP}"),
//                ],
//                propagate: false
//    }
//
//    stage('Create Accounting Per Partner Domain') {
//        build job: 'create_acct_service_per_partner_domain',
//                parameters: [
//                        string(name: 'version', value: "${version}"),
//                        string(name: 'scenario', value: "${scenario}"),
//                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                        string(name: 'SZ_IP', value: "${szIP}"),
//                ],
//                propagate: false
//    }
//
//    stage('Create L2ACL Per Partner Domain') {
//        build job: 'create_l2acl',
//                parameters: [
//                        string(name: 'version', value: "${version}"),
//                        string(name: 'scenario', value: "${scenario}"),
//                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                        string(name: 'SZ_IP', value: "${szIP}"),
//                ],
//                propagate: false
//    }
//
//    stage('Create L3ACP Per Partner Domain') {
//        build job: 'create_l3acp',
//                parameters: [
//                        string(name: 'version', value: "${version}"),
//                        string(name: 'scenario', value: "${scenario}"),
//                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                        string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                        string(name: 'SZ_IP', value: "${szIP}"),
//                ],
//                propagate: false
//    }

//    stage('Join AP and UE') {
//        build job: 'join_ap_ue', parameters: [string(name: 'version', value: "${version}"),
//                                              string(name: 'scenario', value: "${scenario}"),
//                                              string(name: 'ap_version', value: "${ap_version}"),
//                                              string(name: 'SRC_DIR', value: "${SRC_DIR}"),
//                                              string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                                              string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                                              string(name: 'SZ_IP', value: "${szIP}"),
//                                              string(name: 'AP_NUM', value: "${AP_NUM}"),
//                                              string(name: 'UE_NUM', value: "${UE_NUM}"),
//        ]
//    }
//
//    stage('Query API') {
//        build job: 'query_api', parameters: [string(name: 'version', value: "${version}"),
//                                             string(name: 'scenario', value: "${scenario}"),
//                                             string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                                             string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                                             string(name: 'TASK_DIR', value: 'phase2'),
//                                             string(name: 'SZ_IP', value: "${szIP}"),
//                                             string(name: 'NUM_CLIENT', value: "${NUM_CLIENT}"),
//                                             string(name: 'HATCH_RATE', value: "${HATCH_RATE}"),
//                                             string(name: 'RUN_TIME', value: "${RUN_TIME}"),
//
//        ]
//    }
//
//    stage('Create CSV') {
//        build job: 'create_csv', parameters: [string(name: 'version', value: "${version}"),
//                                              string(name: 'scenario', value: "${scenario}"),
//                                              string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                                              string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                                              string(name: 'DATA_DIR', value: "${DATA_DIR}"),
//
//        ]
//    }

    stage('Clean Env') {
        build job: 'clean_env', parameters: [string(name: 'version', value: "${version}"),
                                             string(name: 'scenario', value: "${scenario}"),
                                             string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                             string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                             string(name: 'SZ_IP', value: "${szIP}"),
        ]
    }
}
