node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0'),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: ''),
                        string(name: 'SZ_IP', defaultValue: '1.2.3.4', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    stage('Create Domain') {
        build job: 'create_domain', parameters: [string(name: 'version', value: "${version}"),
                                                 string(name: 'scenario', value: "${scenario}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                 string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

//    stage('Analyze Domain') {
//        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
//                                                          string(name: 'scenario', value: "${scenario}"),
//                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                                                          string(name: 'VAR_DATA', value: "domains"),
//        ]
//    }

    stage('Create Zone') {
        build job: 'create_zone', parameters: [string(name: 'version', value: "${version}"),
                                               string(name: 'scenario', value: "${scenario}"),
                                               string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                               string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                               string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

//    stage('Analyze Zone') {
//        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
//                                                          string(name: 'scenario', value: "${scenario}"),
//                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                                                          string(name: 'VAR_DATA', value: "zones"),
//        ]
//    }

    stage('Create Non Proxy Auth Service') {
        build job: 'create_non_proxy_auth_service', parameters: [string(name: 'version', value: "${version}"),
                                                                 string(name: 'scenario', value: "${scenario}"),
                                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                                 string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Create Non Proxy Acct Service') {
        build job: 'create_non_proxy_acct_service', parameters: [string(name: 'version', value: "${version}"),
                                                                 string(name: 'scenario', value: "${scenario}"),
                                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                                 string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Create Hotspot') {
        build job: 'create_hotspot', parameters: [string(name: 'version', value: "${version}"),
                                                  string(name: 'scenario', value: "${scenario}"),
                                                  string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                  string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                  string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Create WISPr WLAN') {
        build job: 'create_wispr_wlan', parameters: [string(name: 'version', value: "${version}"),
                                                     string(name: 'scenario', value: "${scenario}"),
                                                     string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                     string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                     string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Create EtherPort') {
        build job: 'create_etherport', parameters: [string(name: 'version', value: "${version}"),
                                                    string(name: 'scenario', value: "${scenario}"),
                                                    string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                    string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                    string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Create DHCP Pool') {
        build job: 'create_dhcppool', parameters: [string(name: 'version', value: "${version}"),
                                                   string(name: 'scenario', value: "${scenario}"),
                                                   string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                   string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                   string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Create Client Isolation') {
        build job: 'create_client_isolation', parameters: [string(name: 'version', value: "${version}"),
                                                           string(name: 'scenario', value: "${scenario}"),
                                                           string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                           string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                           string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Create Guest Access') {
        build job: 'create_guest_access', parameters: [string(name: 'version', value: "${version}"),
                                                       string(name: 'scenario', value: "${scenario}"),
                                                       string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                       string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                       string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

//    stage('Pre-Provision AP') {
//        build job: 'create_ap', parameters: [string(name: 'version', value: "${version}"),
//                                             string(name: 'scenario', value: "${scenario}"),
//                                             string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                                             string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
//                                             string(name: 'SZ_IP', value: "${SZ_IP}"),
//        ]
//    }

}
