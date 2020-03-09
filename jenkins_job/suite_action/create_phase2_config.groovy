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

    stage('Analyze Domain') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "domains"),
        ]
    }

    stage('Create Zone') {
        build job: 'create_zone', parameters: [string(name: 'version', value: "${version}"),
                                               string(name: 'scenario', value: "${scenario}"),
                                               string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                               string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                               string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Analyze Zone') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "zones"),
        ]
    }

    stage('Create Non Proxy Auth Service') {
        build job: 'create_non_proxy_auth_service', parameters: [string(name: 'version', value: "${version}"),
                                                                 string(name: 'scenario', value: "${scenario}"),
                                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                                 string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Analyze Non Proxy Auth Service') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "non_proxy_auth"),
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

    stage('Analyze Non Proxy Acct Service') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "non_proxy_acct"),
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

    stage('Analyze Hotspot') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "hotspot"),
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

    stage('Analyze WISPr WLAN') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "wlans"),
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

    stage('Analyze EtherPort') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "etherport"),
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

    stage('Analyze DHCP Pool') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "dhcppool"),
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

    stage('Analyze Client Isolation') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "client_isolation"),
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

    stage('Analyze Guest Access') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "guest_access"),
        ]
    }

    stage('Create Subscription Package') {
        build job: 'create_subscription_package', parameters: [string(name: 'version', value: "${version}"),
                                                               string(name: 'scenario', value: "${scenario}"),
                                                               string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                               string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                               string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Analyze Subscription Package') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "subscription_package"),
        ]
    }

    stage('Create AP Registration Rule') {
        build job: 'create_ap_rule', parameters: [string(name: 'version', value: "${version}"),
                                                  string(name: 'scenario', value: "${scenario}"),
                                                  string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                  string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                  string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Analyze AP Registration Rule') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${version}"),
                                                          string(name: 'scenario', value: "${scenario}"),
                                                          string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                          string(name: 'VAR_DATA', value: "ap_rule"),
        ]
    }

    stage('Pre-Provision AP') {
        build job: 'create_ap', parameters: [string(name: 'version', value: "${version}"),
                                             string(name: 'scenario', value: "${scenario}"),
                                             string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                             string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                             string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

}
