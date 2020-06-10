def szIP
def szIP1

node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0'),
                        string(name: 'ap_version', defaultValue: '2.0.0.0'),
                        string(name: 'SRC_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: ''),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_1', description: ''),

                        string(name: 'NPROC', defaultValue: '2', description: ''),

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

    stage('Launch Second SZ') {
        build job: 'launch_sz', parameters: [string(name: 'version', value: "${version}"),
                                             string(name: 'scenario', value: "${scenario}"),
                                             string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                             string(name: 'GCE_IMAGE', value: "${GCE_IMAGE}"),
                                             string(name: 'SZ_FILE', value: "sz1.inp"),
        ]
    }

    stage('Setup Second SZ IP') {
        script {
            File szInp1 = new File("${VAR_DIR}/input/sz/sz1.inp")
            szIP1 = szInp1.readLines().get(0).split()[1]
            println "SZ Name: ${szInp1.readLines().get(0).split()[0]}"
            println "SZ IP: ${szIP1}"
        }
    }

    stage('Join SZ Cluster') {
        build job: 'cluster', parameters: [string(name: 'version', value: "${version}"),
                                           string(name: 'scenario', value: "${scenario}"),
                                           string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                           string(name: 'SZ_IP', value: "${szIP1}"),
                                           string(name: 'CLUSTER_NAME', value: "alto-${scenario}"),
                                           string(name: 'CLUSTER_IP', value: "${szIP}"),
        ]
    }

//    try {
//        stage('Configure PinPoint') {
//            build job: 'setup-pinpoint', parameters: [string(name: 'version', value: "${version}"),
//                                                      string(name: 'scenario', value: "${scenario}"),
//                                                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                                                      string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                                                      string(name: 'SZ_IP', value: "${szIP}"),
//                                                      string(name: 'CLUSTER_NAME', value: "alto-${scenario}"),
//            ]
//        }
//    } catch (Exception e) {
//        echo "Stage ${currentBuild.result}, but we continue"
//    }

    stage('Create Config') {
        build job: 'create_partner_domain_config', parameters: [string(name: 'version', value: "${version}"),
                                                                string(name: 'scenario', value: "${scenario}"),
                                                                string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                                string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                                string(name: 'SZ_IP', value: "${szIP}"),
                                                                string(name: 'NPROC', value: "${NPROC}"),
        ]
    }


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


    stage('Clean Env') {
        build job: 'clean_env', parameters: [string(name: 'version', value: "${version}"),
                                             string(name: 'scenario', value: "${scenario}"),
                                             string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                             string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                             string(name: 'SZ_IP', value: "${szIP}"),
        ]
    }
}
