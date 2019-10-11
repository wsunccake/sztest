def szIP

node {
    properties([
            parameters([string(name: 'version', defaultValue: '1.0.0.0'),
                        string(name: 'scenario', defaultValue: 'group0')])

    ])

    currentBuild.displayName = "${params.version} - ${params.scenario} - #${currentBuild.number}"

    stage('Clean Previous Output') {
        build job: 'clean_output_dir', propagate: false, parameters: [string(name: 'version', value: "${params.version}"),
                                                                      string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Setup SZ IP') {
        script {
            File szInp = new File("${VAR_DIR}/input/sz/sz.inp")
            szIP=szInp.readLines().get(0)
        }
    }

    stage('Fresh Install') {
        build job: 'fresh_install', parameters: [string(name: 'version', value: "${params.version}"),
                                                 string(name: 'scenario', value: "${params.scenario}"),
                                                 string(name: 'SZ_IP', value: "${szIP}"),]
    }

    try {
        stage('Configure Collectd') {
            build job: 'setup-collectd', parameters: [string(name: 'version', value: "${params.version}"),
                                                      string(name: 'scenario', value: "${params.scenario}"),
                                                      string(name: 'SZ_IP', value: "${szIP}"),]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    try {
        stage('Disable AP Cert Check') {
            build job: 'no_ap-cert-check', parameters: [string(name: 'version', value: "${params.version}"),
                                                        string(name: 'scenario', value: "${params.scenario}"),
                                                        string(name: 'SZ_IP', value: "${szIP}"),]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    try {
        stage('Configure PinPoint') {
            build job: 'setup-pinpoint', parameters: [string(name: 'version', value: "${params.version}"),
                                                      string(name: 'scenario', value: "${params.scenario}"),
                                                      string(name: 'SZ_IP', value: "${szIP}"),]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    try {
        stage('Configure Local License Server') {
            build job: 'update_local_license_server', parameters: [string(name: 'version', value: "${params.version}"),
                                                                   string(name: 'scenario', value: "${params.scenario}"),
                                                                   string(name: 'SZ_IP', value: "${szIP}"),]
        }
    } catch (Exception e) {
        echo "Stage ${currentBuild.result}, but we continue"
    }

    stage('Create Domain') {
        build job: 'create_domain', parameters: [string(name: 'version', value: "${params.version}"),
                                                               string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Analyze Domain') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${params.version}"),
                                                          string(name: 'scenario', value: "${params.scenario}"),
                                                          string(name: 'VAR_DATA', value: "domains"),]
    }

    stage('Create Zone') {
        build job: 'create_zone', parameters: [string(name: 'version', value: "${params.version}"),
                                               string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Analyze Zone') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${params.version}"),
                                                          string(name: 'scenario', value: "${params.scenario}"),
                                                          string(name: 'VAR_DATA', value: "zones"),]
    }

    stage('Create Open WLAN') {
        build job: 'create_open_wlan', parameters: [string(name: 'version', value: "${params.version}"),
                                                    string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Create DPSK WLAN') {
        build job: 'create_dpsk_wlan', parameters: [string(name: 'version', value: "${params.version}"),
                                                    string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Analyze WLAN') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${params.version}"),
                                                          string(name: 'scenario', value: "${params.scenario}"),
                                                          string(name: 'VAR_DATA', value: "wlans"),]
    }

    stage('Create WLAN Group') {
        build job: 'create_wlan_group', parameters: [string(name: 'version', value: "${params.version}"),
                                                     string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Analyze WLAN Group') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${params.version}"),
                                                          string(name: 'scenario', value: "${params.scenario}"),
                                                          string(name: 'VAR_DATA', value: "wlan_groups"),]
    }

    stage('Pre-Provision AP') {
        build job: 'create_ap', parameters: [string(name: 'version', value: "${params.version}"),
                                             string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Analyze AP') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${params.version}"),
                                                          string(name: 'scenario', value: "${params.scenario}"),
                                                          string(name: 'VAR_DATA', value: "aps"),]
    }

    stage('Create AP Group') {
        build job: 'create_ap_group', parameters: [string(name: 'version', value: "${params.version}"),
                                                   string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Analyze AP Group') {
        build job: 'statistics_performance', parameters: [string(name: 'version', value: "${params.version}"),
                                                          string(name: 'scenario', value: "${params.scenario}"),
                                                          string(name: 'VAR_DATA', value: "ap_groups"),]
    }

    stage('Startup SimPC') {
        build job: 'startup_sim_pc', parameters: [string(name: 'version', value: "${params.version}"),
                                                  string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Join AP') {
        build job: 'join_sim_ap', parameters: [string(name: 'version', value: "${params.version}"),
                                               string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Count On Line AP') {
        build job: 'monitor_ap', parameters: [string(name: 'version', value: "${params.version}"),
                                              string(name: 'scenario', value: "${params.scenario}"),
                                              string(name: 'AP_NUM', value: "1"),]
    }

    stage('Count Update-To-Date AP') {
        build job: 'monitor_ap_update-to-date', parameters: [string(name: 'version', value: "${params.version}"),
                                                             string(name: 'scenario', value: "${params.scenario}"),
                                                             string(name: 'AP_NUM', value: "1"),]
    }

    stage('Associate UE') {
        build job: 'associate_sim_ue', parameters: [string(name: 'version', value: "${params.version}"),
                                                    string(name: 'scenario', value: "${params.scenario}"),]
    }

    stage('Count UE') {
        build job: 'monitor_client', parameters: [string(name: 'version', value: "${params.version}"),
                                                  string(name: 'scenario', value: "${params.scenario}"),
                                                  string(name: 'UE_NUM', value: "1"),]
    }
}
