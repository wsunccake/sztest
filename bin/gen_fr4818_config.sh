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
LIB_DIR=${SCRIPT_DIR}/../util
source ${LIB_DIR}/gen_tool.sh


###
### define variable
###

ZONES=${ZONES:=1024}
LBS=${LSB:=1024}
AP_GROUPS_QUOTIENT=$(expr ${LBS} / ${ZONES})
AP_GROUPS_REMAINDER=$(expr ${LBS} % ${ZONES})
AP_GROUPS_PER_ZONE=${AP_GROUPS_PER_ZONE:=$AP_GROUPS_QUOTIENT}

ZONE_FIRST=${ZONE_FIRST:=1}
LBS_FIRST=${LBS_FIRST:=1}
AP_GROUPS_FIRST=${AP_GROUPS_FIRST:=1}

ZONE_LAST=`expr ${ZONE_FIRST} + ${ZONES} - 1`
LBS_LAST=`expr ${LBS_FIRST} + ${LBS} - 1`
AP_GROUPS_LAST=`expr ${AP_GROUPS_FIRST} + ${AP_GROUPS_PER_ZONE} - 1`


ZONE_DIR=zones
AP_GROUP_DIR=ap_groups
LBS_DIR=lbs

INPUT_DIRS=($ZONE_DIR $AP_GROUP_DIR $LBS_DIR)


###
### gernerate function per zone
###

create_ap_group_per_zone() {
  local zone_name=$1
  local first_num=$2
  local last_num=$3
  local zone_num=$4

  create_attribute_per_feature ${zone_name} ${AP_GROUP_DIR} ap_groups ap_group_${zone_num}_ ${ap_groups_start} ${ap_groups_end} false
}


###
### main
###

clean_dirs "${INPUT_DIRS[*]}"
[[ x"$1" == x"clean" ]] && exit

create_dirs "${INPUT_DIRS[*]}"


ap_groups_start=0
ap_groups_end=0
increment=0
for zone_num in `seq -w ${ZONE_FIRST} ${ZONE_LAST}`; do
  increment=`expr ${increment} + 1`

  zone_name="zone_${zone_num}"
  echo "${zone_name}" >> ${ZONE_DIR}/zones.inp

  # ap group
  ap_groups_start=`expr ${ap_groups_end} + 1`
  if [ ${increment} -le ${AP_GROUPS_REMAINDER} ]; then
    ap_groups_end=`expr ${ap_groups_start} + ${AP_GROUPS_PER_ZONE}`
  else
    ap_groups_end=`expr ${ap_groups_start} + ${AP_GROUPS_PER_ZONE} - 1`
  fi
  create_ap_group_per_zone ${zone_name} ${ap_groups_start} ${ap_groups_end} ${zone_num}
done

for lbs_num in `seq ${LBS_FIRST} ${LBS_LAST}`; do
  lbs_name="lbs-${lbs_num}"
  echo "${lbs_name}" >> ${LBS_DIR}/lbs.inp
done

