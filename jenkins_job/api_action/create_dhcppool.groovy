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
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Create DHCP Pool') {
            steps {
                sh '''#!/bin/bash
# expect work
source $EXPECT_DIR/sz/var/expect-var.sh

export SZ_IP=$SZ_IP
echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"
#env

# work dir
cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/dhcppool

# run
echo "start job:`date`"
for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do

# get zone_id
  zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
  echo "zone: $zone_name, $zone_id"
  ./login.sh admin "$ADMIN_PASSWORD"

  # create dhcp pool
  num=`wc -l $VAR_DIR/input/dhcppool/$zone_name.inp | awk '{print \$1}'`
  for n in `seq $num`; do
    lines=(`sed -n ${n}p $VAR_DIR/input/dhcppool/$zone_name.inp`)
    dhcppool_name=${lines[0]}
    vlan_id=${lines[1]}
    subnet_network=${lines[2]}
    subnet_mask=${lines[3]}
    start_ip=${lines[4]}
    end_ip=${lines[5]}
    primary_dns=${lines[6]}
    
    echo "start time:`date`"
    echo "$dhcppool_name $zone_id $vlan_id $subnet_network $subnet_mask $start_ip $end_ip $primary_dns"
    ./create_dhcppool.sh $dhcppool_name $zone_id $vlan_id $subnet_network $subnet_mask $start_ip $end_ip $primary_dns \
    | tee $VAR_DIR/output/dhcppool/${zone_name}_${dhcppool_name}.out
    echo "end time:`date`"
  done
done
echo "end job:`date`"
'''
            }
        }
    }
}
