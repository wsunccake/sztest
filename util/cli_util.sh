#!/bin/bash


###
### sz
###

launch_sz() {
  local cloud_service=$1
  local vm_name=$2
  local cmd

  case ${cloud_service} in
  "GCE")
    cmd="""\
gcloud compute instances create ${vm_name} \
--zone=${GCE_ZONE} \
--image-project=${GCE_IMAGE_PROJECT} \
--image=${GCE_IMAGE} \
--custom-cpu=${GCE_CPU} \
--custom-memory=${GCE_MEM} \
--boot-disk-size=${GCE_DISK_SIZE} \
--boot-disk-type=${GCE_DISK_TYPE} \
--tags=${GCE_TAG} \
--labels=${GCE_LABELS}
"""
  ;;
  *)
    echo "un support cloud service"
    exit 1
  ;;
  esac

  echo ${cmd}
  ${cmd}
}


###
### network command
###

pingable() {
  local host_ip=$1
  ping -c3 -W5 ${host_ip} >& /dev/null && echo "true" || echo "false"
}


wait_until_pingable() {
  local repeat_time=$1
  local wait_time=$2
  local host_ip=$3
  local is_ping="false"

  for i in `seq ${repeat_time}`; do
    is_ping=$(pingable $host_ip)
    [[ "x${is_ping}" == "xtrue" ]] && break
    sleep ${wait_time}
  done

  echo ${is_ping}
}
