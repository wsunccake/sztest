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

    stage('Create Partner Domain') {
        build job: 'create_partner_domain',
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

    stage('Create Zone Per Partner Domain') {
        build job: 'create_zone_per_partner_domain',
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

    stage('Create Authentication Per Partner Domain') {
        build job: 'create_auth_service_per_partner_domain',
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

    stage('Create Accounting Per Partner Domain') {
        build job: 'create_acct_service_per_partner_domain',
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

    stage('Create VLAN Pooling Per Partner Domain') {
        build job: 'create_vlan_pooling_per_partner_domain',
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

    stage('Create Application Policy Per Partner Domain') {
        build job: 'create_application_policy_per_partner_domain',
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

    stage('Create User Defined Per Partner Domain') {
        build job: 'create_user_defined_per_partner_domain',
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

    stage('Create L2ACL Per Partner Domain') {
        build job: 'create_l2acl_per_partner_domain',
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

    stage('Create L3ACP Per Partner Domain') {
        build job: 'create_l3acp_per_partner_domain',
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

    stage('Create LBS Per Partner Domain') {
        build job: 'create_lbs_per_partner_domain',
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

    stage('Create Wifi Calling Polciy Per Partner Domain') {
        build job: 'create_wifi_calling_policy_per_partner_domain',
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

    stage('Create Device Policy Per Partner Domain') {
        build job: 'create_device_policy_per_partner_domain',
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
