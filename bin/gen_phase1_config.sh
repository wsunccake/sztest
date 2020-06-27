#!/bin/bash

###
### for mac
###

realpath() {
    [[ $1 = /* ]] && echo "$1" || echo "$PWD/${1#./}"
}


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
ZONES_PER_DOMAIN=${ZONES_PER_DOMAIN:=10}
OPEN_WLANS_PER_ZONE=${OPEN_WLANS_PER_ZONE:=5}
DPSK_WLANS_PER_ZONE=${DPSK_WLANS_PER_ZONE:=1}
APS_PER_ZONE=${APS_PER_ZONE:=1}
UES_PER_AP=${UES_PER_AP:=1}
AP_GROUPS_PER_ZONE=${AP_GROUPS_PER_ZONE:=5}
WLAN_GROUPS_PER_ZONE=${WLAN_GROUPS_PER_ZONE:=5}

SIM_PC=${SIM_PC:=10}
SIM_AP_START_MAC_NUM=${SIM_AP_START_MAC_NUM:=6576734208}
SIM_AP_START_IP=${SIM_AP_START_IP:=11.10.0.1}
SIM_UE_START_MAC_NUM=${SIM_UE_START_MAC_NUM:=557003571200}
SIM_UE_START_IP=${SIM_UE_START_IP:=172.10.0.1}

DOMAIN_FIRST=${DOMAIN_FIRST:=1}
ZONE_FIRST=${ZONE_FIRST:=1}
OPEN_WLAN_FIRST=${OPEN_WLAN_FIRST:=1}
DPSK_WLAN_FIRST=${DPSK_WLAN_FIRST:=1}

DOMAIN_LAST=`expr $DOMAIN_FIRST + $DOMAINS - 1`
ZONE_LAST=`expr $ZONE_FIRST + $ZONES_PER_DOMAIN - 1`
OPEN_WLAN_LAST=`expr $OPEN_WLAN_FIRST + $OPEN_WLANS_PER_ZONE - 1`
DPSK_WLAN_LAST=`expr $DPSK_WLAN_FIRST + $DPSK_WLANS_PER_ZONE - 1`

#DOMAIN_PREFIX=g1_
#ZONE_PREFIX=g1_

DOMAIN_DIR=domains
ZONE_DIR=zones
WLAN_DIR=wlans
AP_DIR=aps
AP_GROUP_DIR=ap_groups
WLAN_GROUP_DIR=wlan_groups
SIM_PC_DIR=sim

INPUT_DIRS=($DOMAIN_DIR $ZONE_DIR
            $WLAN_DIR $AP_DIR
            $AP_GROUP_DIR $WLAN_GROUP_DIR
            $SIM_PC_DIR)

APS=`expr $DOMAINS \* $ZONES_PER_DOMAIN \* $APS_PER_ZONE`
UES=`expr $APS \* $UES_PER_AP`



create_ap_mac_file() {
 # mac_interval 134
  local ap_start=`expr $SIM_AP_START_MAC_NUM + 134 \* 100`
  local ap_end=`expr $ap_start + 134 \* $APS - 1`
  gen_mac $ap_start $ap_end 134 > $AP_DIR/macs.inp
}


create_apsim_cfg_template() {
  local output_file=$1
  local start_ap_index=$2
  local start_ap_ip=$3
  local start_ap_mac=$4

  ap_num=`expr $APS / $SIM_PC`

  cat << EOF > $output_file
# Set the configurations manually now until we have the cli

# Simulated AP model
MODEL=R710

# Simulated AP firmware version; CANNOT BE EMPTY
# If firmware version is different from the AP zone on SZ,
# simulated AP will simulate firmware upgrade and reports new AP firmware version to SZ
FWVER=AP_VER

# SZ IP address. If not specified, simulated AP will perform
# APR discovery operation
SZIP=SZ_IP

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
# Please DON'T abbreviate to shorter notations, such as fd00::1
# Instead, use fd00:0000:0000:0000:0000:0000:0000:0001
START_AP_IPV6=3022:0DB8:0200:0000:0000:0000:0000:00AA

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
# For IPv6, please DON'T abbreviate to shorter notations, such as fd00::1/64
# Instead, use fd00:0000:0000:0000:0000:0000:0000:0001/64
# Note: Don't overlap the subnet of START_AP_IP/START_AP_IPV6
START_TUNNEL_IPV4=1.1.1.1/32
START_TUNNEL_IPV6=4022:0DB8:0200:0000:0000:0000:0000:00AA/64

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
LOG_LEVEL=2

# Log files size threshold. If a log file size larger than LOG_ROTATE_THRESH,
# it will do log rotation.  Please set this value as 10k, 5M, 1G.
LOG_ROTATE_THRESH=256k
# Log rotation examines every N seconds as specified below.
LOG_ROTATE_INTERVAL=1800  # Seconds

# The following are default reporting interval
# for different AP stats. These are the same default values
# used by real AP. Only CERT_RI is in minutes. others RI/AI are in seconds.

COLLECTD_AI=180       # Internal stats generating interval.
COLLECTD_RI=180       # Report interval to SZ.
STATUS_RI=180         # AP status report interval.
DELTA_RI=180          # Delta data report interval. Which mapping to real AP apReport statistic.
CLIENT_RI=180         # Client stats
WIREDCLIENT_RI=180    # Wired client stats
BONJ_RI=180           # Bonjoure stats
AVC_RI=300            # AVC (application visbility&control)
MESH_RI=900           # Mesh stats
NEW_ROGUE_RI=180      # Report new rogue AP stats
TOTAL_ROGUE_RI=900    # Report All rogue AP stats
CERT_RI=30            # Certificate reload stats. In Minutes.
PERF_RI=180           # Perf stats.
HCCD_RI=180           # Hccd stats.
WITH_AVC=1            # Do AVC reporting
WITH_ROGUE=1          # Do rogue AP reporting
WITH_BONJ=1           # Do bonjoure reporting
WITH_MESH=1           # Do mesh stats reporting
WITH_HCCD=0           # Do HCCD reporting
WITH_PERF=0           # Do perf reporting
NEW_ROGUE_NUM=10      # Number of new rogue reported. Max: 10
TOTAL_ROGUE_NUM=20    # Total number of rogue list reported.(Including NEW_ROGUE_NUM) Max: 100
SLIDE_WINDOW=10       # The size of slide window for total rogue AP report.
ROGUE_MAC_POOL=100    # The range of rogue AP mac address. Max: 65535
BONJ_NUM=32           # Total number of bunjour devices reported.
AVC_URL_NUM=50        # Total number of avc UrlFiltering reported. Max: 50
RI_BY_LOCAL=1         # Control collectd report interval by apsim.cfg or SZ configuration

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

# Number of simulated APs with slogger enabled. Slogger
# uses MD for trnasporting log messages to SZ log manager.
# Use logclient to stress MD server component on SZ
# SZ support maximum 100 logclients.
# However, We have test for more than 100, set LOGCLIENT_NUM=120, and it works fine.
LOGCLIENT_NUM=0

# Number of seconds between each log message sent
LOGCLIENT_INTERVAL=3

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

# set list of cores to run on. If you would like to use core 0,1,2,3,5,7
# please set DADP_CORE_LIST=0-3,5,7
# Recommendation: DADP_CORE_LIST=0,1,2
DADP_CORE_LIST=0,1,2

# how many generate core
# Recommendation: DADP_GEN_NUM= (left blank)
DADP_GEN_NUM=

# how many TX core
# Recommendation: DADP_TX_NUM= (left blank)
DADP_TX_NUM=
EOF
}


create_sim_ap() {
  local ap_interval=`expr $APS / $SIM_PC`
  local start_ap_mac=`convert_decimal_to_mac $SIM_AP_START_MAC_NUM`

  for sim_num in `seq $SIM_PC`; do
    tmpi=`expr $sim_num - 1`
    start_ap_index=`expr 100 + $ap_interval \* $tmpi`
    start_ap_ip=`echo ${SIM_AP_START_IP} | awk -F. "{printf \"%s.%s.%s.%s\", \\$1, \\$2 + $tmpi, \\$3, \\$4}"`

    mkdir -p sim/${sim_num}
#    echo "create_apsim_cfg_template sim/${sim_num}/apsim.cfg.template ${start_ap_index} ${start_ap_ip} ${start_ap_mac}"
    create_apsim_cfg_template sim/${sim_num}/apsim.cfg.template ${start_ap_index} ${start_ap_ip} ${start_ap_mac}
  done
}


create_ue_conf() {
  local output_file=$1
  local start_ue_mac=$2
  local start_ue_ip=$3

  ue_num=`expr $UES / $SIM_PC`
  total_wlan_profile=`expr $OPEN_WLANS_PER_ZONE + $DPSK_WLANS_PER_ZONE`

  cat << EOF > $output_file
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
total_sta_num=$ue_num

# Max initial distribution (in sec)
# This is a parameter to control how many UE per sec will be generated
# 0 = random
# N = generate N UE per second
ue_per_sec=50

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
min_jiffies=0.3

# Maximum jiffies delay between each state (in sec)
max_jiffies=0.5

# How many round that UE should associate to an AP
# 0: infinity loop
# N: associate N times, N is a number equal or great than 1
run_round=0

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
# State <Init>,<Scan>,<Join>,<Auth>,<Assoc>,<Dot1x>,<Web>,<Run>,<Leave>,<Idle>
# Action:
#     0=Continue
#     1=Repeat and rate control by ue_per_sec on continue
#     2=Repeat and burst on continue
#     3=Reset
pre_action=0,0,0,0,0,0,0,0,0,0
post_action=0,0,0,0,0,0,0,0,0,0

#---------------------------------------------------------------------------------
# Following factors control how to generate UE MAC address and IP address
#---------------------------------------------------------------------------------
# Base MAC for generating STA (UE) MAC
sta_base_mac=$start_ue_mac
# IP assignment method: 0: sequential, 1: dhcp
ip_mode=0

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
sta_base_ip=$start_ue_ip

# Base IP for generating STA (UE) IPv6
sta_base_v6_ip=2001:0db8:85a3:08d3:1319:8a2e:0370:7344

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

# mac_pool: 0: disable, 1: enable
# If you want to turn on this function, please set "min_jiffies" 0.01+
mac_pool=0

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
wlan_assoc_mode=1

# Time that STA (UE) associate with an AP (in sec)
# this factor simulates how long UE connects to AP
# total oneline time = sta_online_duration + rand() % online_jiffies
sta_online_duration=3600

# Jiffies for each STA (UE) online time.
online_jiffies=0

# Time that STA (UE) disassociate with an AP (in sec)
# this factor simulates how long UE in idle or disconnect state
# total offline time = sta_offline_duration + rand() % offline_jiffies
sta_offline_duration=300

# Jiffies for each STA (UE) offline time.
offline_jiffies=0

#---------------------------------------------------------------------------------
# AP/SZ RELATED FACTORS
#---------------------------------------------------------------------------------
# List of APs that STA (UE) will try to associate with
ap_mac_list=

# UE will only associate with AP in this group
# Only in effect when roaming_mode is 2.
ap_group_name=

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
#     * type: RGRE_UDP, RGRE_MDNS, L2GRE_UDP, L2GRE_MDNS, ARP, IPV6_NS, IPV6_NA
#     * dest_mac: destination mac (inner mac)
#     * dest_ip: destination ip (inner ip)
#     * dest_ipv6: destination ipv6 (inner ipv6)
#     * dest_port: destination port (inner port)
#     * src_port: source port
#---------------------------------------------------------------------------------

# pkt profile 1
pkt_type=RGRE_UDP
dest_mac=11:22:33:44:55:66
dest_ip=1.1.255.254
dest_ipv6=2001::10.250.255.206
dest_port=1025
src_port=1026
END_PKT_PROFILE

# pkt profile 2
pkt_type=RGRE_MDNS
dest_mac=11:22:33:44:55:66
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
total_wlan_profiles=$total_wlan_profile

#---------------------------------------------------------------------------------
# Each WLAN profiles contians:
#     * wlan_ssid: SSID of this WLAN profile
#     * auth_type: Standard=1, WISPr=2
#     * auth_method: Open=1, 802.1x=2, MAC=3
#     Following items is for WLAN that needs authentication
#         * aaa_user: user name
#         * aaa_pwd: password
#         * tunnel_portal_v6: (0)Use IPv4 (1)Use IPv6
#         * portal_port: portal port (http:9997, https:9998)
#         * get_sp_content:
#                   0: disable, UE do not get the all SP page content
#                   1: Ue would send curl request to get sp_content_url
#         * sp_content_url: Please input format as <path>/<filename>.
#                           If you want to get multiple files, just using "," to seperate.
#                           example: sp_content_url=path1/photo1.jpg,path2/photo2.jpg
#         * req_timeout: CP/SP request timeout. (sec.)
#         * low_speed_limit: ave. transfer speed that should be below during
#                            "low_speed_time" for madue to consider it to be
#                            slow and abort. (bytes per sec.)
#         * low_speed_time: the duration that the transfer speed should be
#                           below "low_speed_limit" for madue to consider it
#                           too slow and abort. (sec.)

EOF

#  for wlan_num in `seq $OPEN_WLANS_PER_ZONE`; do
  for wlan_num in `seq $OPEN_WLAN_FIRST $OPEN_WLAN_LAST`; do
    open_wlan_name="open_wlan_${wlan_num}"
    cat << EOF >> sim/${sim_num}/ue_open.conf
wlan_ssid=$open_wlan_name
auth_type=1
auth_method=1
END_WLAN_PROFILE

EOF
  done

#  for wlan_num in `seq $DPSK_WLANS_PER_ZONE`; do
  for wlan_num in `seq $DPSK_WLAN_FIRST $DPSK_WLAN_LAST`; do
    dpsk_wlan_name="dpsk_wlan_${wlan_num}"
    cat << EOF >> sim/${sim_num}/ue_open.conf
wlan_ssid=$dpsk_wlan_name
auth_type=1
auth_method=1
END_WLAN_PROFILE

EOF
  done
 
}


create_sim_ue() {
  for sim_num in `seq $SIM_PC`; do
    tmpi=`expr $sim_num - 1`
    start_ue_mac=`echo ${SIM_UE_START_MAC} | awk -F: "{printf \"%s:%s:%s:%s:%s:%s\", \\$1, \\$2 + $tmpi, \\$3, \\$4, \\$5, \\$6}"`
    start_ue_ip=`echo ${SIM_UE_START_IP} | awk -F. "{printf \"%s.%s.%s.%s\", \\$1, \\$2 + $tmpi, \\$3, \\$4}"`

    mkdir -p sim/${sim_num}
    create_ue_conf sim/${sim_num}/ue_open.conf $start_ue_mac $start_ue_ip
  done
}


create_open_wlan_per_zone() {
  local zone_name=$1

#  for wlan_num in `seq $OPEN_WLANS_PER_ZONE`; do
  for wlan_num in `seq $OPEN_WLAN_FIRST $OPEN_WLAN_LAST`; do
    open_wlan_name="open_wlan_${wlan_num}"
    echo "${open_wlan_name}" >> $WLAN_DIR/wlans.inp
    # same wlan name in different zone
    echo "${open_wlan_name}" >> $WLAN_DIR/${zone_name}.inp
  done
}


create_dpsk_wlan_per_zone() {
  local zone_name=$1

#  for wlan_num in `seq $DPSK_WLANS_PER_ZONE`; do
  for wlan_num in `seq $DPSK_WLAN_FIRST $DPSK_WLAN_LAST`; do
    dpsk_wlan_name="dpsk_wlan_${wlan_num}"
    echo "${dpsk_wlan_name}" >> $WLAN_DIR/wlans.inp
    # same wlan name in different zone
    echo "${dpsk_wlan_name}" >> $WLAN_DIR/${zone_name}.inp
  done
}


create_ap_group_per_zone() {
  local zone_name=$1

  for ap_group_num in `seq $AP_GROUPS_PER_ZONE`; do
    ap_group_name="ap_group_${ap_group_num}"
    echo "${ap_group_name}" >> $AP_GROUP_DIR/ap_groups.inp
    # same ap group name in different zone
    echo "${ap_group_name}" >> $AP_GROUP_DIR/${zone_name}.inp
  done
}


create_wlan_group_per_zone() {
  local zone_name=$1

  for wlan_group_num in `seq $WLAN_GROUPS_PER_ZONE`; do
    wlan_group_name="wlan_group_${wlan_group_num}"
    echo "${wlan_group_name}" >> $WLAN_GROUP_DIR/wlan_groups.inp
    # same wlan group name in different zone
    echo "${wlan_group_name}" >> $WLAN_GROUP_DIR/${zone_name}.inp
  done
}


###
### main
###

clean_dirs "${INPUT_DIRS[*]}"
[[ x"$1" == x"clean" ]] && exit

create_dirs "${INPUT_DIRS[*]}"

create_ap_mac_file
create_sim_ap
create_sim_ue

ap_index_start=1


for domain_num in `seq $DOMAIN_FIRST $DOMAIN_LAST`; do
#  domain_name="${DOMAIN_PREFIX}domain${domain_num}"
  domain_name="domain${domain_num}"
  echo "${domain_name}" >> $DOMAIN_DIR/domains.inp

  # zone per domain
  for zone_num in `seq $ZONE_FIRST $ZONE_LAST`; do
#    zone_name="${ZONE_PREFIX}zone${domain_num}_${zone_num}"
    zone_name="zone${domain_num}_${zone_num}"
    echo "${zone_name}" >> $ZONE_DIR/zones.inp
    echo "${zone_name}" >> $ZONE_DIR/${domain_name}.inp

    # open wlan per zone
    create_open_wlan_per_zone ${zone_name}

    # dpsk wlan per zone
    create_dpsk_wlan_per_zone ${zone_name}

    # ap per zone
    ap_index_end=`expr $ap_index_start + $APS_PER_ZONE - 1`
    sed -n ${ap_index_start},${ap_index_end}p $AP_DIR/macs.inp > $AP_DIR/${zone_name}.inp
    ap_index_start=`expr $ap_index_start + $APS_PER_ZONE`

    # ap group per zone
    create_ap_group_per_zone ${zone_name}

    # wlan group per zone
    create_wlan_group_per_zone ${zone_name}

  done
done

