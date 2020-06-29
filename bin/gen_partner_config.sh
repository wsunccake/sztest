#!/bin/bash

###
### for mac
###
#
#realpath() {
#    [[ $1 = /* ]] && echo "$1" || echo "$PWD/${1#./}"
#}


###
### import util
###

SCRIPT_DIR="$(dirname `realpath $0`)"
#echo -e "pwd: `pwd`\nSCRIPT_DIR: $SCRIPT_DIR"
LIB_DIR=$SCRIPT_DIR/../util
source $LIB_DIR/gen_tool.sh


###
### define variable
###

DOMAINS=${DOMAINS:=5}
ZONES_PER_DOMAIN=${ZONES_PER_DOMAIN:=5}
L2ACL_PER_DOMAIN=${L2ACL_PER_DOMAIN:=1}
L3ACP_PER_DOMAIN=${L3ACP_PER_DOMAIN:=1}
APPLICATION_POLICY_PER_DOMAIN=${APPLICATION_POLICY_PER_DOMAIN:=1}
USER_DEFINED_PER_DOMAIN=${USER_DEFINED_PER_DOMAIN:=1}
LBS_PER_DOMAIN=${LBS_PER_DOMAIN:=1}
WIFI_CALLING_POLICY_PER_DOMAIN=${WIFI_CALLING_POLICY_PER_DOMAIN:=1}
DEVICE_POLICY_PER_DOMAIN=${DEVICE_POLICY_PER_DOMAIN:=1}
VLAN_POOLING_PER_DOMAIN=${VLAN_POOLING_PER_DOMAIN:=1}
RADIUS_PER_DOMAIN=${RADIUS_PER_DOMAIN:=2}
RADIUS_IP_FILE=radius_ip.txt
RADIUS_PER_ZONE=${RADIUS_PER_ZONE:=1}
HOTSPOT_PER_ZONE=${HOTSPOT_PER_ZONE:=1}
PSK_WLAN_PER_ZONE=${PSK_WLAN_PER_ZONE:=1}
STD_8021X_WLAN_PER_ZONE=${STD_8021X_WLAN_PER_ZONE:=1}
WISPR_OPEN_WLAN_PER_ZONE=${WISPR_OPEN_WLAN_PER_ZONE:=1}
WISPR_MAC_WLAN_PER_ZONE=${WISPR_MAC_WLAN_PER_ZONE:=1}
APS_PER_ZONE=${APS_PER_ZONE:=1}
UES_PER_AP=${UES_PER_AP:=1}

DOMAIN_FIRST=${DOMAIN_FIRST:=1}
ZONE_FIRST=${ZONE_FIRST:=1}
L2ACL_FIRST=${L2ACL_FIRST:=1}
L3ACP_FIRST=${L3ACP_FIRST:=1}
APPLICATION_POLICY_FIRST=${APPLICATION_POLICY_FIRST:=1}
USER_DEFINED_FIRST=${USER_DEFINED_FIRST:=1}
LBS_FIRST=${LBS_FIRST:=1}
WIFI_CALLING_POLICY_FIRST=${WIFI_CALLING_POLICY_FIRST:=1}
DEVICE_POLICY_FIRST=${DEVICE_POLICY_FIRST:=1}
VLAN_POOLING_FIRST=${VLAN_POOLING_FIRST:=1}
HOTSPOT_FIRST=${HOTSPOT_FIRST:=1}
PSK_WLAN_FIRST=${PSK_WLAN_FIRST:=1}
STD_8021X_WLAN_FIRST=${STD_8021X_WLAN_FIRST:=1}
WISPR_OPEN_WLAN_FIRST=${WISPR_OPEN_WLAN_FIRST:=1}
WISPR_MAC_WLAN_FIRST=${WISPR_MAC_WLAN_FIRST:=1}

DOMAIN_LAST=`expr $DOMAIN_FIRST + $DOMAINS - 1`
ZONE_LAST=`expr $ZONE_FIRST + $ZONES_PER_DOMAIN - 1`
L2ACL_LAST=`expr $L2ACL_FIRST + $L2ACL_PER_DOMAIN - 1`
L3ACP_LAST=`expr $L3ACP_FIRST + $L3ACP_PER_DOMAIN - 1`
APPLICATION_POLICY_LAST=`expr $APPLICATION_POLICY_FIRST + $APPLICATION_POLICY_PER_DOMAIN - 1`
USER_DEFINED_LAST=`expr $USER_DEFINED_FIRST + $USER_DEFINED_PER_DOMAIN - 1`
LBS_LAST=`expr $LBS_FIRST + $LBS_PER_DOMAIN - 1`
WIFI_CALLING_POLICY_LAST=`expr $WIFI_CALLING_POLICY_FIRST + $WIFI_CALLING_POLICY_PER_DOMAIN - 1`
DEVICE_POLICY_LAST=`expr $DEVICE_POLICY_FIRST + $DEVICE_POLICY_PER_DOMAIN - 1`
VLAN_POOLING_LAST=`expr $VLAN_POOLING_FIRST + $VLAN_POOLING_PER_DOMAIN - 1`
HOTSPOT_LAST=`expr $HOTSPOT_FIRST + $HOTSPOT_PER_ZONE - 1`
PSK_WLAN_LAST=`expr $PSK_WLAN_FIRST + $PSK_WLAN_PER_ZONE - 1`
STD_8021X_WLAN_LAST=`expr $STD_8021X_WLAN_FIRST + $STD_8021X_WLAN_PER_ZONE - 1`
WISPR_OPEN_WLAN_LAST=`expr $WISPR_OPEN_WLAN_FIRST + $WISPR_OPEN_WLAN_PER_ZONE - 1`
WISPR_MAC_WLAN_LAST=`expr $WISPR_MAC_WLAN_FIRST + $WISPR_MAC_WLAN_PER_ZONE - 1`

SIM_PC=${SIM_PC:=10}
SIM_AP_START_MAC_NUM=${SIM_AP_START_MAC_NUM:=6576734208}
SIM_AP_START_IP=${SIM_AP_START_IP:=11.10.0.1}
SIM_UE_START_MAC_NUM=${SIM_UE_START_MAC_NUM:=557003571200}
SIM_UE_START_IP=${SIM_UE_START_IP:=172.10.0.1}

DOMAIN_DIR=partner_domains
ZONE_DIR=zones
L2ACL_DIR=l2acl
L3ACP_DIR=l3acp
APPLICATION_POLICY_DIR=application_policy
USER_DEFINED_DIR=user_defined
LBS_DIR=lbs
WIFI_CALLING_POLICY_DIR=wifi_calling_policy
DEVICE_POLICY_DIR=device_policy
VLAN_POOLING_DIR=vlan_pooling
PROXY_AUTH_DIR=proxy_auth
PROXY_ACCT_DIR=proxy_acct
NON_PROXY_AUTH_DIR=non_proxy_auth
NON_PROXY_ACCT_DIR=non_proxy_acct
WLAN_DIR=wlans
HOTSPOT_DIR=hotspot
AP_DIR=aps
SIM_PC_DIR=sim


INPUT_DIRS=($DOMAIN_DIR $ZONE_DIR
            $PROXY_AUTH_DIR $PROXY_ACCT_DIR
            $L2ACL_DIR $L3ACP_DIR
            $APPLICATION_POLICY_DIR $USER_DEFINED_DIR
            $LBS_DIR $WIFI_CALLING_POLICY_DIR
            $DEVICE_POLICY_DIR $VLAN_POOLING_DIR
            $WLAN_DIR $HOTSPOT_DIR
            $NON_PROXY_AUTH_DIR $NON_PROXY_ACCT_DIR
            $AP_DIR $SIM_PC_DIR)


APS=`expr $DOMAINS \* $ZONES_PER_DOMAIN \* $APS_PER_ZONE`
UES=`expr $APS \* $UES_PER_AP`


###
### generate function per zone
###

create_non_proxy_auth_and_acct_per_zone() {
  local zone_name=$1

  if [ -z $RADIUS_IP ]; then
    local addr1=1
    local addr2=`expr $addr1 + $RADIUS_PER_ZONE - 1`
    sed -n ${addr1},${addr2}p $RADIUS_IP_FILE > $NON_PROXY_AUTH_DIR/$zone_name.inp
    sed -n ${addr1},${addr2}p $RADIUS_IP_FILE > $NON_PROXY_ACCT_DIR/$zone_name.inp
  else

    seq $RADIUS_PER_ZONE | xargs -i echo $RADIUS_IP > $NON_PROXY_AUTH_DIR/$zone_name.inp
    seq $RADIUS_PER_ZONE | xargs -i echo $RADIUS_IP > $NON_PROXY_ACCT_DIR/$zone_name.inp
  fi
}


create_hotspot_profile_per_zone() {
  local zone_name=$1

  create_attribute_per_feature $zone_name $HOTSPOT_DIR wlans hotspot $HOTSPOT_FIRST $HOTSPOT_LAST
}


create_wlan_per_zone() {
  local zone_name=$1

  # Standard + OpenAuth + PSK (WPA2 + AES)
  create_attribute_per_feature $zone_name $WLAN_DIR wlans psk $PSK_WLAN_FIRST $PSK_WLAN_LAST

  # Standard + 8021x
  create_attribute_per_feature $zone_name $WLAN_DIR wlans std8021x $STD_8021X_WLAN_FIRST $STD_8021X_WLAN_LAST

  # WISPr + Open
  create_attribute_per_feature $zone_name $WLAN_DIR wlans wispropen $WISPR_OPEN_WLAN_FIRST $WISPR_OPEN_WLAN_LAST

  # WISPr + MAC
  create_attribute_per_feature $zone_name $WLAN_DIR wlans wisprmac $WISPR_MAC_WLAN_FIRST $WISPR_MAC_WLAN_LAST
}


###
### generate function per domain
###

create_zone_per_domain() {
  local domain_name=$1
  local domain_num=$2

#  create_attribute_per_feature $domain_name $ZONE_DIR zones zone${domain_num} $ZONE_FIRST $ZONE_LAST
  for num in `seq -w $ZONE_FIRST $ZONE_LAST`; do
    local name="zone${domain_num}${num}"
    echo "${name}" >> ${ZONE_DIR}/zones.inp
    # same attribute name in different feature
    echo "${name}" >> ${ZONE_DIR}/${domain_name}.inp

    create_non_proxy_auth_and_acct_per_zone $name
    create_hotspot_profile_per_zone $name
    create_wlan_per_zone $name

    # ap per zone
    sed -n 1,${APS_PER_ZONE}p $AP_DIR/macs.inp > $AP_DIR/${name}.inp
    sed -i 1,${APS_PER_ZONE}d $AP_DIR/macs.inp
  done
}

create_profile_or_service_per_domain() {
  local domain_name=$1

  # l2acl
  create_attribute_per_feature $domain_name $L2ACL_DIR l2acl l2acl $L2ACL_FIRST $L2ACL_LAST

  # l3acp
  create_attribute_per_feature $domain_name $L3ACP_DIR l3acp l3acp $L3ACP_FIRST $L3ACP_LAST

  # application policy
  create_attribute_per_feature $domain_name $APPLICATION_POLICY_DIR application_policy app $APPLICATION_POLICY_FIRST $APPLICATION_POLICY_LAST

  # user defined
  create_attribute_per_feature $domain_name $USER_DEFINED_DIR user_defined ud $USER_DEFINED_FIRST $USER_DEFINED_LAST

  # lbs
  create_attribute_per_feature $domain_name $LBS_DIR lbs lbs $LBS_FIRST $LBS_LAST

  # wifi calling policy
  create_attribute_per_feature $domain_name $WIFI_CALLING_POLICY_DIR wifi_calling_policy wcp $WIFI_CALLING_POLICY_FIRST $WIFI_CALLING_POLICY_LAST

  # device policy
  create_attribute_per_feature $domain_name $DEVICE_POLICY_DIR device_policy dp $DEVICE_POLICY_FIRST $DEVICE_POLICY_LAST

  # vlan pooling
  create_attribute_per_feature $domain_name $VLAN_POOLING_DIR vlan_pooling vlan $VLAN_POOLING_FIRST $VLAN_POOLING_LAST
}


###
### other generate function
###

create_ap_mac_file() {
 # mac_interval 134
  local ap_start=`expr ${SIM_AP_START_MAC_NUM} + 134 \* 100`
  local ap_end=`expr ${ap_start} + 134 \* ${APS} - 1`
  gen_mac ${ap_start} ${ap_end} 134 > ${AP_DIR}/macs.inp

}

###
### generate sim ap cfg
###

create_apsim_cfg_template() {
  local output_file=$1
  local start_ap_index=$2
  local start_ap_ip=$3
  local start_ap_mac=$4

  local ap_num=`expr $APS / $SIM_PC`

  cat << EOF > $output_file
# Set the configurations manually now until we have the cli

# Simulated AP model
MODEL=R710

# Simulated AP firmware version; CANNOT BE EMPTY
# If firmware version is different from the AP zone on SZ,
# simulated AP will simulate firmware upgrade and reports new AP firmware version to SZ
FWVER=5.2.1.0.253

# SZ IP address. If not specified, simulated AP will perform
# APR discovery operation
SZIP=

# APR_SERVER_IP can be ip or domain name.
# If AP do APR discovery and APR_SERVER_IP is not empty, it would query
# APR_SERVER_IP to get SZ ip. Otherwise, AP would query default
# APR server [ap-registrar.ruckuswireless.com].
APR_SERVER_IP=

# Set the number of port for each AP.
PORT_CNT=2

# Simulated AP host name is Sim-xxx, where xxx starts from
# START_AP_INDEX and incremented by one for each additional AP
# This value MUST greater than 0
START_AP_INDEX=$start_ap_index

# Simulated AP IP address = START_AP_IP + START_AP_INDEX
# IP address is incremented by one for each additional AP
START_AP_IP=$start_ap_ip

# Simulated AP IPv6 address = START_AP_IPV6 + START_AP_INDEX
# IP address is incremented by one for each additional AP
START_AP_IPV6=3022:0DB8:0200::AA

# Simulated AP MAC address = START_AP_MAC + START_AP_INDEX
# MAC address is incremented by one for each additional AP
START_AP_MAC=$start_ap_mac

# Simulated AP serial number. "R" for randomly generated
# serial number. If it is a numeric value, it is used
# as the starting serial number.
START_AP_SN=R

# Total number of AP simualted
APNUM=$ap_num

# Number of Simulated APs joining SZ per second
# If there are lots of ssh tunnel failed,
# you can set this number to 1 or lessn then 1. (e.g. 0.75)
AP_PER_SEC=3

# Background watchdog scanning interval
WATCHDOG_INTERVAL=60  # Seconds

# Set to 1/0 to enable/disable sending all events from wsgclient and hostapd.
ENABLE_EVENT=1

# Create GRE tunnel to SZ data plane. Should set to 1 when
# there is tunnel WLAN; otherwise, set to 0 to reduce the
# load of simulation
ENABLE_TUNNELMGR=0

# Specify tunnelmgr tx/rx interface. If "DISABLE", it will automatically route.
# Possible values: eth0, enp5s0, etc...
# NOTE: When NOT left blank, outer source IP addr. of RGRE packets would vary
#       according to which tunnel a packet belongs to.  And the value is
#       calculated from START_AP_IP.
#       When left blank, outer source IP addr. of RGRE packets would be the
#       single one addr. chosen by kernel routing procedures.
TUNNELMGR_OUTER_IF=DISABLE

# Set to 1/0 to enable/disable tunnelmgr to replace inner MAC/IP with UE's MAC/IP
TUNNELMGR_REPLACE_INNER=DISABLE
# tunnelmgr replace only TCP packets in port range
# [TUNNELMGR_REPLACE_INNER_PORT_BASE + 20000]
TUNNELMGR_REPLACE_INNER_PORT_BASE=DISABLE

# Assign GRE interface IP in CIDR notation for tunnel simulation
# IP address is incremented by one for each additional TUNNEL
# They are used as inner source IP addresses of RGRE packets
# IPv6 address limitation: DO NOT set same subnet as
# fc00:5255:434b:5553::afe:101/64 because SZ will send lots of NS packets to DP
# Note: Don't overlap the subnet of START_AP_IP/START_AP_IPV6
START_TUNNEL_IPV4=1.1.1.1/32
START_TUNNEL_IPV6=4022:0DB8:0200::AA/64

# If you want to use provision tag type as AP registration rule,
# please set this value as rule parameters. (e.g. testing123)
# The maximum length is 32 characters.
PROVISION_TAG=

# Set to 1/0 FIPS SKU/NOT FIPS SKU AP
IS_FIPS_SKU_AP=0
# Set to 1/0 enable/disable FIPS function
ENABLE_FIPS=0

# Set to 2/1/0 to enable (debug)/enable (error, warn)/disable simulation logging.
# Disable logging for long running simulation to conserve disk
# space
LOG_LEVEL=1

# Log files size threshold. If a log file size larger than LOG_ROTATE_THRESH,
# it will do log rotation.  Please set this value as 10k, 5M, 1G.
LOG_ROTATE_THRESH=256k
# Log rotation examines every N seconds as specified below.
LOG_ROTATE_INTERVAL=1800  # Seconds

# The following are default reporting interval
# for different AP stats. These are the same default values
# used by real AP. Only CERT_RI is in minutes. others RI/AI are in seconds.

COLLECTD_AI=180          # Internal stats generating interval.
COLLECTD_RI=180          # Report interval to SZ.
STATUS_RI=180            # AP status report interval.
DELTA_RI=180             # Delta data report interval. Which mapping to real AP apReport statistic.
CLIENT_RI=180            # Client stats
WIREDCLIENT_RI=180       # Wired client stats
BONJ_RI=180              # Bonjoure stats
AVC_RI=300               # AVC (application visbility&control)
MESH_RI=900              # Mesh stats
NEW_ROGUE_RI=180         # Report new rogue AP stats
TOTAL_ROGUE_RI=900       # Report All rogue AP stats
CERT_RI=30               # Certificate reload stats. In Minutes.
PERF_RI=180              # Perf stats.
HCCD_RI=180              # Hccd stats.
WITH_AVC=1               # Do AVC reporting
WITH_ROGUE=1             # Do rogue AP reporting
WITH_BONJ=1              # Do bonjoure reporting
WITH_MESH=1              # Do mesh stats reporting
WITH_HCCD=0              # Do HCCD reporting
WITH_PERF=0              # Do perf reporting
BONJ_NUM=32              # Total number of bunjour devices reported.
AVC_URL_NUM=50           # Total number of avc UrlFiltering reported. Max: 50
AVC_WFC_NUM=3            # Total number of avc Wifi-calling reported. Max: 10
AVC_WFC_SCORE=50         # Wifi-calling quality Score, range: 0 to 100
RI_BY_LOCAL=1            # Control collectd report interval by apsim.cfg or SZ configuration

#########################################################
#          ROGUE AP feature
#########################################################
NEW_ROGUE_NUM=10         # Number of new rogue reported. Max: 10
TOTAL_ROGUE_NUM=20       # Total number of rogue list reported.(Including NEW_ROGUE_NUM) Max: 100
SLIDE_WINDOW=10          # The size of slide window for total rogue AP report.
ROGUE_MAC_POOL=100       # The range of rogue AP mac address. Max: 65535
# The interval of total rogue AP report for reseting all rogue AP as new (not reported)
# Never: 0, X: renew after sending total rogue AP report every x times
RENEW_ROGUE_INTERVAL=0
# The interval of rogue AP report (including new and total) for reporting channel change
# (only work for those rouge APs whose type are update) 0: Never report channel change
# X: Report channel change after sending rogue AP report every x times
DIFF_ROGUE_CH_INTERVAL=120
# The interval of rogue AP report (including new and total) for reporting channel change
# (only work for those rouge APs whose type are update) 0: Never report type change
# X: Report type change after sending rogue AP report every x times
DIFF_ROGUE_TP_INTERVAL=0

#########################################################
#          ROGUE Client feature
#########################################################
NEW_ROGUE_C_NUM=10       # Number of new rogue client reported. Max: 100
TOTAL_ROGUE_C_NUM=20     # Total number of rogue client list reported.(Including NEW_RC_NUM) Max: 100
ROGUE_C_SLD_WIN=10       # The size of slide window for total rogue client report.
ROGUE_C_MAC_POOL=100     # The range of rogue client mac address. Max: 65535

#########################################################
#          HCCD features
#########################################################
# If enable MLISA_MODE, collectd will report HCCD according to the UE's connection activity.
# Otherwise, collectd would generate fake hccd data.
MLISA_MODE=0
# The maximum CCD num is 1024.
# TOTAL_CCD_NUM = CCD_AUTH_NUM + CCD_ASSOC_NUM + CCD_EAP_NUM + CCD_RADIUS_NUM + CCD_DHCP_NUM
# Please do NOT set TOTAL_CCD_NUM larger than 1024
CCD_AUTH_NUM=0        # Number of ccd auth fail
CCD_ASSOC_NUM=0       # Number of ccd assoc fail
CCD_EAP_NUM=0         # Number of ccd eap fail
CCD_RADIUS_NUM=0      # Number of ccd radius fail
CCD_DHCP_NUM=0        # Number of ccd dhcp fail
CCD_RADIO_ID=0        # 0: radio0, 1: radio1, 2: 50% radio0 and 50% radio1
CCD_WLAN_ID=0         # CCD wlan ID

# Number of MAC address nbrd query
NBRD_MAC_NUM=10

# Number of seconds between each nbrd query message sent (0 means disable)
NBRD_INTERVAL=0

# Number of percent mac address nbrd query is invalid.
# This value should be integer.
# e.g. NBRD_INVALID_MAC_RATIO=30, it means 30%.
NBRD_INVALID_MAC_RATIO=0

# Invalid mac address range hbrd query
# The format is XX:XX:XX:XX:XX:XX
NBRD_INVALID_MAC_START=
NBRD_INVALID_MAC_END=

# The base IP address of up to 1k IPsec Gateways (e.g., 192.168.1.1), set
# "DISABLE" to disable IPsec functions
MADSEC_IP_BASE=DISABLE

#########################################################
#          UE traffic features (daDP)
#########################################################

# device is reserved for DPDK
DPDK_NIC=eth999

# 0: disable daDP daemon
# 1: enable daDP to run in daemon
# 2: enable daDP to run in daemon and enable KNI interface
ENABLE_DADP=0

# number of packets sent out per second
# can read at most 10 value: DADP_PPS=1000,2000,3000 (without whitespace!)
# Note: PPS will change every DADP_INTERVAL second if DADP_INTERVAL > 0
# Recommendation: DADP_PPS=0 (send at fastest rate)
DADP_PPS=0

# total packets number
# daDP will stop sending packets once the number is reached
# Recommendation: DADP_NUM_PKTS=0 (never stop sending)
DADP_NUM_PKTS=0

# 0: turn off plot option
# 1: daDP will save counter data in file for plot_tool
DADP_PLOT=0

# access_side: generate and send packets
# core_side: receive and send bidirection traffic based on DADP_RECORD_PORT
DADP_MODE=access_side

# daDP in core_side mode will generate bidirection traffic if packets
# arrive at this port (match with "dest_port" in ue.conf)
DADP_RECORD_PORT=49152

# Core side daDP stops sending SB traffic if it doens't receive packets on DADP_RECORD_PORT
# for DADP_RECORD_CYCLE sec
DADP_RECORD_CYCLE=3

# 0: Trace   1: Debug   2: Info (default)
# 3: Warn    4: Error   5: Fatal
DADP_DBG_LEVEL=2

# save daDP logs to file
DADP_LOGFILE=dadp.log

# 0: static PPS
# larger than 0: the expiration time (sec) for PPS change
DADP_INTERVAL=0

# number of huge pages reserved for DPDK
# Recommendation: HUGEPAGES_NUM=1024
HUGEPAGES_NUM=1024

# how many core number to daDP run
# Recommendation: DADP_NUM_OF_CORE=2
DADP_NUM_OF_CORE=
EOF
}

create_sim_ap() {
  local ap_interval=`expr $APS / $SIM_PC`
  local start_ap_mac=`convert_decimal_to_mac $SIM_AP_START_MAC_NUM colon_upper`
  local num

  for num in `seq $SIM_PC`; do
    local tmpi=`expr $num - 1`
    local start_ap_index=`expr 100 + $ap_interval \* $tmpi`
    local start_ap_ip=`echo ${SIM_AP_START_IP} | awk -F. "{printf \"%s.%s.%s.%s\", \\$1, \\$2 + $tmpi, \\$3, \\$4}"`

    mkdir -p sim/${num}
#    echo "create_apsim_cfg_template sim/${num}/apsim.cfg.template ${start_ap_index} ${start_ap_ip} ${start_ap_mac}"
    create_apsim_cfg_template sim/${num}/apsim.cfg.template ${start_ap_index} ${start_ap_ip} ${start_ap_mac}
  done
}

###
### generate sim ue cfg
###


create_ue_conf() {
  local output_file=$1
  local start_ue_mac=$2
  local start_ue_ip=$3
  local wlan_num

  local ue_num=`expr ${UES} / ${SIM_PC}`
#  local total_wlan_profile=`expr ${WISPR_WLANS_PER_ZONE}`
  local total_wlan_profile=4

  cat << EOF > ${output_file}
#
# Support Comment, please use '#' in the first character of line
#

#=================================================================================
#
# STA (UE) SIMULATION FACTORS
#
#=================================================================================
#---------------------------------------------------------------------------------
# Following factors controls how many UE will be generated per second and how it
# is distributed.
# Formula:
#     1. number of UE generated per second == ue_per_sec
#     2. UE distribued in N seconds where N == total_sta_num/ue_per_sec
#---------------------------------------------------------------------------------
# Number of total clinet number
# Total STA (UE) number of each group
# total client number of all groups must be NOT more than 20000
total_sta_num=${ue_num}

# Max initial distribution (in sec)
# This is a parameter to control how many UE per sec will be generated
# 0 = random
# N = generate N UE per second
ue_per_sec=25

# UE distribution mode in a second
# 0 = random
# 1 = unify
# 2 = burst
ue_distribution=1

#---------------------------------------------------------------------------------
# Following factors control delays between each states (Auth, Assoc, Run..ect) of
# UE
# Formula:
#     total jiffies time = rand(min_jiffies, max_jiffies)
#---------------------------------------------------------------------------------

# Minimum jiffies between each state (in sec)
min_jiffies=1

# Maximum jiffies delay between each state (in sec)
max_jiffies=0.5

# How many round that UE should associate to an AP
# 0: infinity loop
# N: associate N times, N is a number equal or great than 1
run_round=0

# How many round that UE should secycle again for keep the TPS as ue_per_second
# It is mutually exclusive from run_round
recycle_ue=0

# Max number of loop waiting for each state
# It control how long should state to wait response from AP such as auth/assoc response
max_loop=3

# Set STA (UE) number of each group of specfic time
# The smallest unit is half an hour.
# The expression is 24-hour clock.
# Example: 0-10.5:800,10.5-11:900,11-24:700
# The above represents 800 UEs runs(including all states) during 0:00~10:30,
# 900 UEs runs(including all states) during 10:30~11:00, and
# 700 UEs runs(including all states) during 11:00~24:00.
sta_cnt_var_time=

# Set action before and after a state
# State <Init>,<Scan>,<Join>,<Auth>,<Assoc>,<Dot1x>
#       <4way_HS>,<GetIP>,<Web>,<Run>,<Leave>,<Idle>
# Action:
#     0=Continue
#     1=Repeat and rate control by ue_per_sec on continue
#     2=Repeat and burst on continue
#     3=Reset
pre_action=0,0,0,0,0,0,0,0,0,0,0,0
post_action=0,0,0,0,0,0,0,0,0,0,0,0

#---------------------------------------------------------------------------------
# Following factors control how to generate UE MAC address and IP address
#---------------------------------------------------------------------------------
# Base MAC for generating STA (UE) MAC
sta_base_mac=${start_ue_mac}

# IP assignment method: 0: sequential, 1: dhcp
ip_mode=0

# Set 0/1 to disable/enable sending ARP.
# If enable, UE would send a ARP to query SZ IP after it got IP in GETIP state.
send_arp=0

# Network interface name for DHCP. If enable tunnel, this parameter would not be used.
nic_name=eth0

# DHCP request timeout (Sec)
dhcp_timeout=15

# Simulate 2.4G or 5G
# 0 = all UEs are using 2.4G
# 1 = all UEs are using 5G
# 2 = random distribution. Some of UEs are 2.4G, others are 5G
ue_radio_mode=2

# Set ue os type ratio
# The order of OS type: <Linux>, <Windows>, <Mac>, <iOS>, <Android>
# The value of each type can be 0~100, but the sum of each type should be equal 100.
# Ex. ue_os_ratio = 10,50,30,0,10
# The above value means 10% UEs are Linux, 50% UEs are Windows, 30% UEs are Mac, 0% UEs are iOS, and 10% UEs are Android.
ue_os_ratio=100,0,0,0,0

# Base IP for generating STA (UE) IP
sta_base_ip=${start_ue_ip}

# Base IP for generating STA (UE) IPv6
sta_base_v6_ip=2001:0db8:85a3:08d3:1319:8a2e:0370:7344

# server ip and mac to connect when in RUN state or when in WEB state
# simulating user actions of using the Internet.
# If wlan setting includes TTG function, sta_online duration is more than 90,
# and release_dhcp value is 1, The following two items must be set.
traffic_svr_ip=
traffic_svr_mac=

#=================================================================================
#
# STA (UE) CONNECT/DISCONNECT/ROAMING FACTORS
#
#=================================================================================
#---------------------------------------------------------------------------------
# Following factors control how UE connect/disconnect to an AP
# 1. roaming_mode: tells simulator if UE will simulate UE roaming, if yes UE will
#    not send out deauth/disassoc/logout while leave an AP
# 2. assoc_mode: tells simulator how to pick up an AP and WLAN, either random or
#    sequence from AP/WLAN list
# 3. sta_online_duration: tells simulator UE will keep online (simulate sending
#    data to AP)
# 4. stat_offline_duratioin: tells simulator how long will UE keep idle or
#    disconnect from an AP
#---------------------------------------------------------------------------------

# mac_pool change UEs' mac
# 0 = disable ( mac will not be changed )
# N = change the mac every N rounds.
# If you want to turn on this function, please set "min_jiffies" 0.01+
mac_pool=0

# The maximum number for mac pool address
# N is a number equal or great than 1 but smaller then 255
pool_size=255

# auto_start: 0: disable, 1: enable
# If auto_start is disabled, UEs will NOT automatically connect to APs at madue
# start, and wait for for user commands
auto_start=1

# If AP should simulate roaming
# (0: disable
#  1: UE raoming between all APs
#  2: UE roaming in AP group, where you can specify which group to roam by setting ap_group_name.
#     If ap_group_name is not given, UE chooses one AP among all for the very first association
#     and roam in that group afterward.)
# If enable roaming, do NOT set ap_assoc_mode=2. It will cause UE connecting to previous AP.
# And do NOT set wlan_assoc_mode=1. It will cause UE choose different wlan from previous one,
# this is not romaing case.
roaming_mode=0

# How UE find an AP (0:random, 1:sequence, 2:balance)
ap_assoc_mode=2

# How UE find a WLAN (0:random, 1:sequence, 2:balance)
#                    (3:random+simple, 4:sequence+simple, 5:balance+simple)
wlan_assoc_mode=2

# How to arrange STA (UE) connect/disconnect
# (0:use online/offline duration 1:use start online/offline ue count)
sta_on_off_mode=0

# The connected STA (UE) count threshold that trigger UE start to disconnect
# do NOT exceed total_sta_num
sta_offline_start_cnt=500

# The disconnected STA (UE) count threshold that trigger UE start to connect
# do NOT exceed total_sta_num
sta_online_start_cnt=500

# Time that STA (UE) associate with an AP (in sec)
# this factor simulates how long UE connects to AP
# total oneline time = sta_online_duration + rand() % online_jiffies
sta_online_duration=7200

# Jiffies for each STA (UE) online time.
online_jiffies=0

# Time that STA (UE) disassociate with an AP (in sec)
# this factor simulates how long UE in idle or disconnect state
# total offline time = sta_offline_duration + rand() % offline_jiffies
sta_offline_duration=60

# Jiffies for each STA (UE) offline time.
offline_jiffies=0

#---------------------------------------------------------------------------------
# Log Options:
#---------------------------------------------------------------------------------
# Turn on STA (UE) log mask
# ( Support multuple input, for example: show_sta=0, 1 )
show_sta=

#---------------------------------------------------------------------------------
# AP/SZ RELATED FACTORS
#---------------------------------------------------------------------------------
# List of APs that STA (UE) will try to associate with
ap_mac_list=

# UE will only associate with AP in this group
# Only in effect when roaming_mode is 2.
ap_group_name=

# --------------------------------------------------------------------------------
# EAP-SIM/TTG RELATED FACTORS
# --------------------------------------------------------------------------------
# Based imsi for generating STA (UE) imsi (EX:100100000000001)
# This parameter is only used in 1x eap-sim or TTG-wlan.
eap_sim_start_imsi=

#---------------------------------------------------------------------------------
# Multicast Options:
# Please set is_mcast=1 and is_bcast=0 in wlan profile to enable mDNS
#---------------------------------------------------------------------------------
# type of dns message (0: query, 1: response)
dns_type=

# Used in both DNS query and DNS response
# DNS query: "question name"
# DNS response: "name" (so that clients know this answer is for which question)
# ( Support multiple input, for example:
#   dns_name=_A._tcp.local., _B._udp.local. )
dns_name=

# DNS response: "rdata" (the answer)
# ( Support multiple input, for example:
#   dns_rdata=192.168.100.1, 192.168.200.2  )
dns_rdata=

#---------------------------------------------------------------------------------
# UE traffic features (daDP)
#---------------------------------------------------------------------------------
#*********************************************************************************
# START_PKT_PROFILES, DO NOT DELETE IT, it tells parser it is a start point of
# pkt profiles
#*********************************************************************************

# Generated payload size, valid size should be in range 64 to 1400.
# ( Support multuple input, for example:
#   payload_size=100, 512, 256, 1400 )
payload_size=1400

START_PKT_PROFILES

# How many destination profile that UE will send traffic.
total_pkt_profiles=0

#---------------------------------------------------------------------------------
# pkt header values
# Each pkt profiles contians:
#     * pkt_type: UDP_RGRE, RGRE_MDNS, L2GRE_UDP, L2GRE_MDNS, ARP, IPV6_NS, IPV6_NA, RGRE
#     * pkt_dest_ip: destination ip (inner ip)
#     * pkt_dest_ipv6: destination ipv6 (inner ipv6)
#     * pkt_dest_port: destination port (inner port)
#     * pkt_src_port: source port
#---------------------------------------------------------------------------------

# pkt profile 1
pkt_type=UDP_RGRE
dest_ip=1.1.255.254
dest_ipv6=2001::10.250.255.206
dest_port=1025
src_port=1026
END_PKT_PROFILE

# pkt profile 2
pkt_type=RGRE_MDNS
dest_ip=1.1.255.254
dest_ipv6=2001::10.250.255.206
dest_port=1027
src_port=1028
END_PKT_PROFILE

#=================================================================================
#
# WLAN PROFILES
#
#=================================================================================
#*********************************************************************************
# START_WLAN_PROFILES, DO NOT DELETE IT, it tells parser it is a start point of
# WLAN profiles
#*********************************************************************************
START_WLAN_PROFILES

# How many WLAN profile that STA (UE) will use to associate
total_wlan_profiles=${total_wlan_profile}

#---------------------------------------------------------------------------------
# Each WLAN profiles contians:
#     * wlan_ssid: SSID of this WLAN profile
#     * auth_type: Standard=1, WISPr=2
#     * auth_method: Open=1, 802.1x=2, MAC=3 MAC+802.1x=4
#     Following items is for WLAN that needs authentication
#         * aaa_user: user name
#         * aaa_pwd: password
#         * sta_dot1x_method: PAP=0, EAP_MD5=1, EAP_SIM=2, PEAP=3
#         * protocol: http/https
#         * prefer_ip_mode: (0)Use IPv4 (1)Use IPv6 (2)Use Dual
#         * portal_src_port_base: (0)auto, or TUNNELMGR_REPLACE_INNER_PORT_BASE in apsim.cfg
#         * portal_port: portal port (http:9997, https:9998)
#         * web_fail_retry:
#                   0: UE would go back to INIT state if it got fail or timeout from WEB
#                   1: UE would retry the WEB state for [max_loop] times
#         * fail_retry_count: If an UE cannot be in RUN state for [fail_retry_count] times,
#                             it would stop the UE and put it into wait queue.
#                             -1 means UE would retry forever.
#                             IT ONLY WORKS WHEN THE wlan_assoc_mode=2
#         * ignore_get_sp:
#                   0: it will get SP page before post username/password
#                   1: it will post username/password without get SP page
#         * get_sp_content:
#                   0: disable, UE do not get the all SP page content
#                   1: Ue would send curl request to get sp_content_url
#         * sp_content_url: Please input format as <path>/<filename>.
#                           If you want to get multiple files, just using "," to seperate.
#                           example: sp_content_url=path1/photo1.jpg,path2/photo2.jpg
#         * login_delay: delay between SM path assoc. and CP/SP login req.,
#                        this option also applies to logout delay between
#                        CP/SP logout req. and SM path disassoc. (sec.)
#                        Precision could be 10^-9 sec., but it's limited by
#                        kernel and hardware, i.e., 10ms or so.
#                        It's at-least-value, not exact-value: specifying 1
#                        sec. promises at least 1 sec. delay, but not exactly 1
#                        sec. delay.
#         * req_timeout: CP/SP request timeout. (sec.)
#         * low_speed_limit: ave. transfer speed that should be below during
#                            "low_speed_time" for madue to consider it to be
#                            slow and abort. (bytes per sec.)
#         * low_speed_time: the duration that the transfer speed should be
#                           below "low_speed_limit" for madue to consider it
#                           too slow and abort. (sec.)
#         * logout: defines logout behavior:
#                   0: disable
#                   1 or higher: number of retries if logout fails
#                   -1: retry until succeed.
#         * release_dhcp: define if sending dhcp release message when ue disassociates.
#                   0: disable
#                   1: enable
#         * eap_sim_mcc: Please fill in mobile country code (2~3 digital)
#         * eap_sim_mnc: Please fill in mobile network code (2~3 digital)
#         * eap_sim_rand: Please fill in the rand of authentication triplets
#         * eap_sim_sres: Please fill in the sres of authentication triplets
#         * eap_sim_kc: Please fill in the kc of authentication triplets
#         * data_interval: interval of each udp data connections in RUN state
#         * passphrase_by_mac:
#                   0: Use UE mac as passphrase. If an UE mac is [01:0a:02:0b:03:0c],
#                      the passphrase will be [010a020b030c]
#                   1: Use the wpa_passphrase value as passphrase
#         * wpa_passphrase: Please fill in a passphrase for wpa encryption
#         * is_bcast:
#               0: packet won't be broadcast
#               1: packet will be broadcast
#         * is_mcast:
#               0: packet won't be multicast DNS
#               1: packet will be multicast DNS
#         (is_bcast and is_mcast are mutually exclusive. Only one of them can be set to 1.)
#         * bcast_src_port: source port of broadcast packet
#         * bcast_dest_port: source port of broadcast packet
#---------------------------------------------------------------------------------
# Each Wired profiles contains:
#     * is_wired: Specify this is a wired profile
#     * access_port: Which ethernet port ue would connect (start from 1)
#     * auth_type: 1 (Standard)
#     * auth_method: 2 (802.1x)
#     * aaa_user: user name
#     * aaa_pwd: password
#     * sta_dot1x_method: PAP=0, EAP_MD5=1, EAP_SIM=2, PEAP=3
#---------------------------------------------------------------------------------

# PSK WLAN
wlan_ssid=psk1
auth_type=1
auth_method=1
wpa_passphrase=password
fail_retry_count=-1
END_WLAN_PROFILE

# 802.1x WLAN
wlan_ssid=std8021x1
auth_type=1
auth_method=2
sta_dot1x_method=0
aaa_user=test
aaa_pwd=test
fail_retry_count=-1
END_WLAN_PROFILE

# WISPr WLAN
wlan_ssid=wispropen1
auth_type=2
auth_method=1
aaa_user=test
aaa_pwd=test
protocol=https
prefer_ip_mode=0
portal_src_port_base=0
portal_port=9998
web_fail_retry=1
fail_retry_count=-1
ignore_get_sp=0
get_sp_content=0
sp_content_url=path1/photo1.jpg,path2/photo2.jpg
login_delay=1
req_timeout=30
low_speed_limit=1024
low_speed_time=30
logout=0
END_WLAN_PROFILE

# WISPr MAC 802.1X WLAN
wlan_ssid=wisprmac1
auth_type=2
auth_method=3
aaa_user=test
aaa_pwd=test
sta_dot1x_method=0
protocol=https
prefer_ip_mode=0
portal_src_port_base=0
portal_port=9998
web_fail_retry=1
fail_retry_count=-1
ignore_get_sp=0
get_sp_content=0
sp_content_url=path1/photo1.jpg,path2/photo2.jpg
login_delay=1
req_timeout=30
low_speed_limit=1024
low_speed_time=30
logout=0
END_WLAN_PROFILE

EOF

}


create_radius_mac_auth_config() {
  local output_file=$1
  local start_ue_mac=$2
  local num

  ue_num=`expr ${UES} / ${SIM_PC}`
  tmp_hex=`echo ${start_ue_mac} | tr -d :`
  start_num=$((16#$tmp_hex))
  end_num=`expr ${start_num} + ${ue_num}`
  for num in `seq ${start_num} ${end_num}`; do
    local tmp_mac=`convert_decimal_to_mac ${num} lower`
    echo "$tmp_mac Cleartext-Password := ${tmp_mac}" >> ${output_file}
  done
}


create_sim_ue() {
  local sim_num

  for sim_num in `seq ${SIM_PC}`; do
    local tmpi=`expr ${sim_num} - 1`
    local tmp_mac=`convert_decimal_to_mac ${SIM_UE_START_MAC_NUM} colon_lower`
    local start_ue_mac=`echo ${tmp_mac} | awk -F: "{printf \"%s:%s:%s:%s:%s:%s\", \\$1, \\$2 + $tmpi, \\$3, \\$4, \\$5, \\$6}"`
    local start_ue_ip=`echo ${SIM_UE_START_IP} | awk -F. "{printf \"%s.%s.%s.%s\", \\$1, \\$2 + $tmpi, \\$3, \\$4}"`

    mkdir -p ${SIM_PC_DIR}/${sim_num}
    create_ue_conf $SIM_PC_DIR/${sim_num}/ue_open.conf ${start_ue_mac} ${start_ue_ip}
    create_radius_mac_auth_config ${SIM_PC_DIR}/${sim_num}/mac_auth.conf ${start_ue_mac}
  done
}



###
### main
###

clean_dirs "${INPUT_DIRS[*]}"
[[ x"$1" == x"clean" ]] && exit

create_dirs "${INPUT_DIRS[*]}"
[ -z $RADIUS_IP ] && gen_ip `expr $DOMAINS \* $RADIUS_PER_DOMAIN` > $RADIUS_IP_FILE

#echo "RADIUS_IP: $RADIUS_IP"
#echo "aps: $APS, ues: $UES"

create_sim_ap
create_sim_ue
create_ap_mac_file
cp ${AP_DIR}/macs.inp ${AP_DIR}/macs.inp.org


addr1=1
addr2=`expr $addr1 + $RADIUS_PER_DOMAIN - 1`
for domain_num in `seq -w $DOMAIN_FIRST $DOMAIN_LAST`; do
  domain_name="domain${domain_num}"
  echo "${domain_name}" >> $DOMAIN_DIR/domains.inp

  # zone per domain
  create_zone_per_domain $domain_name $domain_num

  # l2acl, l3acp, application policy, user defined, lbs,
  # wifi calling policy, device policy, vlan pooling
  create_profile_or_service_per_domain $domain_name

  # proxy auth and acct per domain
  if [ -z $RADIUS_IP ]; then
    sed -n ${addr1},${addr2}p $RADIUS_IP_FILE > $PROXY_AUTH_DIR/$domain_name.inp
    sed -n ${addr1},${addr2}p $RADIUS_IP_FILE > $PROXY_ACCT_DIR/$domain_name.inp
  else
    seq $RADIUS_PER_DOMAIN | xargs -i echo $RADIUS_IP > $PROXY_AUTH_DIR/$domain_name.inp
    seq $RADIUS_PER_DOMAIN | xargs -i echo $RADIUS_IP > $PROXY_ACCT_DIR/$domain_name.inp
  fi

  addr1=`expr $addr2 + 1`
  addr2=`expr $addr2 + $RADIUS_PER_DOMAIN`
done

mv -fv ${AP_DIR}/macs.inp.org ${AP_DIR}/macs.inp

