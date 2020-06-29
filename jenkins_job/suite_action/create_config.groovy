node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0'),
                        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${version}/${scenario}', description: ''),
                        string(name: 'API_PERF_VER', defaultValue: 'v9_0', description: ''),
                        string(name: 'SZ_IP', defaultValue: '1.2.3.4', description: ''),
                        string(name: 'AP_NUM', defaultValue: '1', description: ''),
                        string(name: 'UE_NUM', defaultValue: '1', description: ''),
                        string(name: 'DPSK_AMOUNT', defaultValue: "1", description: ''),

                        string(name: 'NPROC', defaultValue: '8', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    stage('Create Domain') {
        build job: 'create_domain',
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

    stage('Create Zone') {
        build job: 'create_zone',
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

    stage('Create Open WLAN') {
        build job: 'create_open_wlan',
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

    stage('Create DPSK WLAN') {
        build job: 'create_dpsk_wlan',
              parameters: [
                      string(name: 'version', value: "${version}"),
                      string(name: 'scenario', value: "${scenario}"),
                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                      string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                      string(name: 'SZ_IP', value: "${SZ_IP}"),
              ],
              propagate: false
    }

    stage('Create DPSK ') {
        build job: 'create_dpsk_batch',
              parameters: [
                      string(name: 'version', value: "${version}"),
                      string(name: 'scenario', value: "${scenario}"),
                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                      string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                      string(name: 'SZ_IP', value: "${SZ_IP}"),
                      string(name: 'DPSK_AMOUNT', value: "${DPSK_AMOUNT}"),
              ],
              propagate: false
    }

    stage('Analyze DPSK') {
        build job: 'statistics_performance',
              parameters: [
                      string(name: 'version', value: "${version}"),
                      string(name: 'scenario', value: "${scenario}"),
                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                      string(name: 'VAR_DATA', value: "wlans/dpsk"),
              ],
              propagate: false
    }

    stage('Create WLAN Group') {
        build job: 'create_wlan_group',
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

    stage('Pre-Provision AP') {
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

    stage('Create AP Group') {
        build job: 'create_ap_group',
              parameters: [
                      string(name: 'version', value: "${version}"),
                      string(name: 'scenario', value: "${scenario}"),
                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                      string(name: 'SZ_IP', value: "${SZ_IP}"),
                      string(name: 'NPROC', value: "${NPROC}"),
              ],
              propagate: false
    }

    stage('Arrange Data') {
        build job: 'pickup_data',
              parameters: [
                      string(name: 'version', value: "${version}"),
                      string(name: 'scenario', value: "${scenario}"),
                      string(name: 'VAR_DIR', value: "${VAR_DIR}"),
              ],
              propagate: false
    }

}
