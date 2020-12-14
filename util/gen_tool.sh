#!/bin/bash


clean_dirs() {
  local dirs=$1
  local d

  for d in ${dirs[@]}; do
    [[ -d $d ]] && rm -rf $d || true
  done
}


create_dirs() {
  local dirs=$1
  local d

  for d in ${dirs[@]}; do
    mkdir -p $d
  done
}


create_attribute_per_feature() {
  local feature_name=$1
  local attribute_dir=$2
  local attribute_file=$3
  local attribute_prefix=$4
  local first_num=$5
  local last_num=$6
  local fix_width=$7
  local num

  if [ -z ${fix_width} ]; then
    fix_opt='-w'
  elif [ ${fix_width} == 'false' ]; then
    fix_opt=' '
  else
    fix_opt='-w'
  fi

  for num in `seq ${fix_opt} ${first_num} ${last_num}`; do
    local name="${attribute_prefix}${num}"
    echo "${name}" >> ${attribute_dir}/${attribute_file}.inp
    # same attribute name in different feature
    echo "${name}" >> ${attribute_dir}/${feature_name}.inp
  done
}


gen_ip() {
#  16843009 (dec) -> 01010101 (hex)
  local ip_num=$1
  local ip_first=16843009
  local ip_last=`expr $ip_first + $ip_num - 1`
  local i

  for i in `seq $ip_first $ip_last`; do
    local hex_num=`printf "%08X" $i`
    local dec_4_num=`printf "%d.%d.%d.%d\n" 0x${hex_num:0:2} 0x${hex_num:2:2} 0x${hex_num:4:2} 0x${hex_num:6:2}`
#    echo "$i -> $hex_num -> $dec_4_num"
    echo "$dec_4_num"
  done
}


###
### gen mac begin
###

convert_decimal_to_mac() {
  # 6576734208 -> 00:01:88:01:00:00
  # 6593511424 -> 00:01:89:01:00:00
  # 6710951936 -> 00:01:90:01:00:00
  # 625723047936 -> 00:91:b0:00:00:00
  # 557003571200 -> 00:81:b0:00:00:00
  # 488284094464 -> 00:71:b0:00:00:00

  local decimal_num=$1
  local format_type=$2

  oct=`printf "%012X" ${decimal_num}`
  case ${format_type:=colon_lower} in
    "colon_lower")
      echo "${oct:0:2}:${oct:2:2}:${oct:4:2}:${oct:6:2}:${oct:8:2}:${oct:10:2}" | tr '[:upper:]' '[:lower:]'
      ;;
    "colon_upper")
      echo "${oct:0:2}:${oct:2:2}:${oct:4:2}:${oct:6:2}:${oct:8:2}:${oct:10:2}" | tr '[:lower:]' '[:upper:]'
      ;;
    "lower")
      echo "${oct:0:2}${oct:2:2}${oct:4:2}${oct:6:2}${oct:8:2}${oct:10:2}" | tr '[:upper:]' '[:lower:]'
      ;;
    "upper")
      echo "${oct:0:2}${oct:2:2}${oct:4:2}${oct:6:2}${oct:8:2}${oct:10:2}" | tr '[:lower:]' '[:upper:]'
      ;;
    *)
      echo "${oct:0:2}:${oct:2:2}:${oct:4:2}:${oct:6:2}:${oct:8:2}:${oct:10:2}"
      ;;
  esac
}


gen_mac() {
  local mac_start=$1
  local mac_end=$2
  local mac_interval=$3
  local num

  mac_interval=${mac_interval:-"1"}

  for num in `seq ${mac_start} ${mac_interval} ${mac_end}`; do
    convert_decimal_to_mac ${num} colon_upper
  done
}

###
### gen mac end
###
