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

ZONES=${ZONES:=1024}
OPEN_WLAN_PER_ZONE=${OPEN_WLAN_PER_ZONE:=1}

ZONE_FIRST=${ZONE_FIRST:=1}
OPEN_WLAN_FIRST=${OPEN_WLAN_FIRST:=4}

ZONE_LAST=`expr $ZONE_FIRST + $ZONES - 1`
OPEN_WLAN_LAST=`expr $OPEN_WLAN_FIRST + $OPEN_WLAN_PER_ZONE - 1`

ZONE_DIR=zones
WLAN_DIR=wlans


INPUT_DIRS=($ZONE_DIR $WLAN_DIR)
            

###
### generate function per zone
###

create_wlan_per_zone() {
  local zone_name=$1

  # Standard Open
  create_attribute_per_feature $zone_name $WLAN_DIR wlans open $OPEN_WLAN_FIRST $OPEN_WLAN_LAST
}


###
### main
###

clean_dirs "${INPUT_DIRS[*]}"
[[ x"$1" == x"clean" ]] && exit

create_dirs "${INPUT_DIRS[*]}"


for zone_num in `seq -w $ZONE_FIRST $ZONE_LAST`; do
  zone_name="zone${zone_num}"
  echo "${zone_name}" >> $ZONE_DIR/zones.inp

  create_wlan_per_zone ${zone_name}
done

