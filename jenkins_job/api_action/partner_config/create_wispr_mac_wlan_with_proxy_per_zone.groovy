library identifier: 'dynamic-libary@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/wsunccake/sztest.git'])

pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'EXPECT_DIR', defaultValue: '/var/lib/jenkins/expect', description: '')
        string(name: 'API_PERF_DIR', defaultValue: '/var/lib/jenkins/api_perf', description: '')
        string(name: 'API_PERF_VER', defaultValue: 'v9_1', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'NPROC', defaultValue: '2', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Create WISPr MAC WLAN With Proxy Per Zone') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

# setup sz ip
if [ -z $SZ_IP ]; then
  SZ_IP=`sed -n 1p $VAR_DIR/input/sz/sz.inp`
fi 

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/wispr_mac_wlans


# run
echo "start job:`date`"
for dname in `cat $VAR_DIR/input/partner_domains/domains.inp`; do`
  export domain_main=$dmain
  echo "domain: $domain_name"

  for zname in `cat $VAR_DIR/input/zones/$domain_name.inp`; do
    export zone_name=$zname

    # get zone_id
    export zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
    echo "zone: $zone_name, $zone_id"

    n=1
    export hotspot_name=`sed -n 1p $VAR_DIR/input/hotspot/$zone_name.inp`
    export auth_ip=`sed -n ${n}p $VAR_DIR/input/proxy_auth/$domain_name.inp`
    export auth_id=`awk -F\\" '/id/ {print \\$4}' $VAR_DIR/output/proxy_auth/${domain_name}_${auth_ip}.${n}.out`
    export acct_ip=`sed -n ${n}p $VAR_DIR/input/proxy_acct/$domain_name.inp`
    export acct_id=`awk -F\\" '/id/ {print \\$4}' $VAR_DIR/output/proxy_acct/${domain_name}_${acct_ip}.${n}.out`

    # login
    ./login.sh admin "$ADMIN_PASSWORD"

    # create non proxy auth
    grep wisprmac $VAR_DIR/input/wlans/$zone_name.inp | xargs -P $NPROC -i sh -c "./create_wispr_mac_wlan_with_proxy.sh {} $zone_id $hotspot_name $auth_id $acct_id | tee $VAR_DIR/output/wispr_mac_wlans/${zone_name}_{}.out"
  
    # logout
    ./logout.sh

  done
done
echo "end job:`date`"
'''
            }
        }

        stage('Check Response') {
            steps {
                script {
                    def result = util.checkResponseStatus "${VAR_DIR}/output/wispr_mac_wlans"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/wispr_mac_wlans"
                }
            }
        }

    }
}
