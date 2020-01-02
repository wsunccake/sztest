def szIP

node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group1-2-3'),
                        string(name: 'ap_version', defaultValue: '2.0.0.0'),
                        string(name: 'SRC_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: ''),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v1_0', description: ''),
                        string(name: 'AP_NUM', defaultValue: '10000', description: ''),
                        string(name: 'UE_NUM', defaultValue: '100000', description: ''),
                        string(name: 'DPSK_AMOUNT', defaultValue: "10", description: ''),
                        string(name: 'MADSZ_TGZ', defaultValue: 'madSZ-v5.2-39-u1804.tar.xz', description: '')
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

    stage('Test Query API') {
        build job: 'test_query', parameters: [string(name: 'version', value: "${version}"),
                                              string(name: 'scenario', value: "${scenario}"),
                                              string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                              string(name: 'SZ_IP', value: "${szIP}"),
        ]
    }

    stage('Clean Env') {
        build job: 'clean_env', parameters: [string(name: 'version', value: "${version}"),
                                             string(name: 'scenario', value: "${scenario}"),
                                             string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                             string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                             string(name: 'SZ_IP', value: "${szIP}"),
        ]
    }
}
