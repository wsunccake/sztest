node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0'),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: ''),
                        string(name: 'SZ_IP', defaultValue: '1.2.3.4', description: ''),
                        string(name: 'NPROC', defaultValue: '8', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    stage('Create Non Proxy Auth Per Zone') {
        build job: 'create_non_proxy_auth_service_per_zone',
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

    stage('Create Non Proxy Acct Per Zone') {
        build job: 'create_non_proxy_acct_service_per_zone',
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

    stage('Create PSK WLAN Per Zone') {
        build job: 'create_psk_wlan_per_zone',
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

    stage('Create 802.1x WLAN With Non Proxy Per Zone') {
        build job: 'create_8021x_wlan_with_non_proxy_per_zone',
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

    stage('Create Hotspot Per Zone') {
        build job: 'create_hotspot_per_zone',
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

    stage('Create WISPr MAC WLAN With Proxy Per Zone') {
        build job: 'create_wispr_mac_wlan_with_proxy_per_zone',
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

    stage('Create WISPr WLAN With Proxy Per Zone') {
        build job: 'create_wispr_wlan_with_proxy_per_zone',
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

    stage('AP Pre-Provision Per Zone') {
        build job: 'create_ap_per_zone',
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

}
