#!/bin/bash


###
### env
###

export SZ_IP=
#export SZTEST_HOME=
export API_VERSION=v9_1
export SZ_USERNAME=
export SZ_PASSWORD=
export VAR_DIR=$SZTEST_HOME/job/fr4818
export NPROC=8

source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/util/test_api/common.sh
source $SZTEST_HOME/util/test_api/fr4818.sh


###
## function
###

usage() {
  echo "$0 <argument>"
  echo "    create: create 1000 zone and 1000 lbs"
  echo "    delete: delete 1000 zone and 1000 lbs"
  echo "    count : count lbs connection and disconnect state"
  echo "    help  : help"
  exit 1
}

create_testbed() {
  # create zone
  cat $VAR_DIR/input/zones/zones.inp | xargs -i -P $NPROC sh -c 'create_zone {} | tee $VAR_DIR/output/zones/{}.out'

  # create lbs
  cat $VAR_DIR/input/lbs/lbs.inp | xargs -i -P $NPROC sh -c 'create_lbs {} | tee $VAR_DIR/output/lbs/{}.out'

  # patch zone with lbs
  [ -f $VAR_DIR/input/zone_lbs.inp ] && rm $VAR_DIR/input/zone_lbs.inp
  line=0
  for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do
    line=`expr $line + 1`
    zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/${zone_name}.out`
  
    lbs_name=`sed -n ${line}p $VAR_DIR/input/lbs/lbs.inp`
    lbs_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/lbs/${lbs_name}.out`
    echo "zone: $zone_name $zone_id lsb: $lbs_name $lbs_id" >> $VAR_DIR/input/zone_lbs.inp
  done
  cat $VAR_DIR/input/zone_lbs.inp | xargs -n6 -P $NPROC sh -c 'patch_zone_with_lbs $2 $5 | tee $VAR_DIR/output/patch_zone_with_lbs/$1_$4.out'

  # patch lbs
  [ -f $VAR_DIR/input/lbs_password.inp ] && rm $VAR_DIR/input/lbs_password.inp
  i=0
  for n in `awk '{print $1}' $VAR_DIR/input/psk.txt`; do
    i=`expr $i + 1`
    id=`awk -F\" '/id/{print $4}' $VAR_DIR/output/lbs/${n}.out`
    psk=`sed -n ${i}p $VAR_DIR/input/psk.txt | awk '{print $2}'`
  
    echo "$n $id $psk" >> $VAR_DIR/input/lbs_password.inp
  done
  cat $VAR_DIR/input/lbs_password.inp | xargs -n 3 -P $NPROC sh -c 'patch_lbs ${1} ${2}'
}

delete_testbed() {
  # clean zone
  [ -f $VAR_DIR/input/zone.txt ] && rm $VAR_DIR/input/zone.txt
  get_all_zone >> $VAR_DIR/input/zone.txt
  awk -F'|' '{print $1}'  $VAR_DIR/input/zone.txt | xargs -i -P $NPROC sh -c 'delete_zone {}'

  # clean lbs
  [ -f $VAR_DIR/input/lbs.txt ] && rm $VAR_DIR/input/lbs.txt
  query_all_lbs_by_domain_id ${DEFAULT_DOMAIN_UUID} >> $VAR_DIR/input/lbs.txt
  cat $VAR_DIR/input/lbs.txt | xargs -i -P $NPROC sh -c 'delete_lbs {}'
}

count_lbs_connection() {
  [ -f $VAR_DIR/input/zone.txt ] && rm $VAR_DIR/input/zone.txt
  get_all_zone >> $VAR_DIR/input/zone.txt
  awk -F'|' '{print $1}' $VAR_DIR/input/zone.txt | xargs -i -P $NPROC sh -c 'get_lbs_state {}' | awk '{
  if ($2 == 1) 
  total_conn += 1
}

{
  if ($2 == 0) 
  total_disconn += 1
}

END {
  print "total connection:", total_conn
  print "total disconnection:", total_disconn
}'

}

###
### main
###

action=$1
action=${action:=help}

[ "$action" == "help" ] && usage

mkdir -p $VAR_DIR/input
mkdir -p $VAR_DIR/output/zones
mkdir -p $VAR_DIR/output/lbs
mkdir -p $VAR_DIR/output/patch_zone_with_lbs

setup_api_var


pubapi_login $SZ_USERNAME $SZ_PASSWORD

case $action in
  "create")
    create_testbed
    ;;
  "delete")
    delete_testbed
    ;;
  "count")
    count_lbs_connection
    ;;
  *)
    usage
    ;;
esac

pubapi_logout

