node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0'),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: ''),
                        string(name: 'SZ_IP', defaultValue: '1.2.3.4', description: '')
            ])
    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    stage('Delete AP') {
        build job: 'delete_ap', parameters: [string(name: 'version', value: "${version}"),
                                             string(name: 'scenario', value: "${scenario}"),
                                             string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                             string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                             string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Delete Zone') {
        build job: 'delete_zone', parameters: [string(name: 'version', value: "${version}"),
                                               string(name: 'scenario', value: "${scenario}"),
                                               string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                               string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                               string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Delete Domain') {
        build job: 'delete_domain', parameters: [string(name: 'version', value: "${version}"),
                                                 string(name: 'scenario', value: "${scenario}"),
                                                 string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                 string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                 string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

    stage('Delete Subscription Package') {
        build job: 'delete_subscription_package', parameters: [string(name: 'version', value: "${version}"),
                                                               string(name: 'scenario', value: "${scenario}"),
                                                               string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                                                               string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                                                               string(name: 'SZ_IP', value: "${SZ_IP}"),
        ]
    }

}
