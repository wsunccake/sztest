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
### sim
###

launch_sim() {
  local cloud_service=$1
  local vm_name=$2
  local cmd

  case ${cloud_service} in
  "GCE")
    cmd="""\
gcloud compute instances create ${vm_name} \
--zone=${GCE_ZONE} \
--machine-type=${MADSZ_MACHINE_TYPE} \
--image-project=${MADSZ_IMAGE_PROJECT} \
--image=${MADSZ_IMAGE} \
--boot-disk-size=${MADSZ_DISK_SIZE} \
--boot-disk-type=${MADSZ_DISK_TYPE} \
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
### vm
###

shutdown_vm() {
  local cloud_service=$1
  local vm_name=$2
  local cmd

  case ${cloud_service} in
  "GCE")
    cmd="""\
gcloud compute instances stop ${vm_name}
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


delete_vm() {
  local cloud_service=$1
  local vm_name=$2
  local cmd

  case ${cloud_service} in
  "GCE")
    cmd="""\
gcloud compute instances delete ${vm_name}
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


ping_available() {
  local host_ip=$1
  host_ip=${host_ip:=127.0.0.1}

  ping -c3 -W5 ${host_ip} >& /dev/null && echo "true" || echo "false"
}


port_available() {
  local host_ip=$1
  local host_port=$2
  host_ip=${host_ip:=127.0.0.1}
  host_port=${host_port:=22}

  nc -w3 ${host_ip} ${host_port} < /dev/null >& /dev/null && echo "true" || echo "false"
}


ssh_available() {
  local host_ip=$1
  local user=$2
  local cmd=$3
  local host_port=$4

  host_ip=${host_ip:=127.0.0.1}
  host_port=${host_port:=22}
  username=${user:=root}
  run_command=${run_command:=uptime}

  ssh -o StrictHostKeyChecking=no -o ConnectTimeout=3 -p ${host_port} ${username}@${host_ip} ${run_command}>& /dev/null && echo "true" || echo "false"
}


wait_until_ping_available() {
  local repeat_time=$1
  local wait_time=$2
  local host_ip=$3
  local is_ping_available="false"

  for i in `seq ${repeat_time}`; do
    is_ping_available=$(ping_available $host_ip)
    [[ "x${is_ping_available}" == "xtrue" ]] && break
    sleep ${wait_time}
  done

  echo ${is_ping_available}
}


###
### other
###

statistics_locustio_output() {
  local f=$1
  awk -F, '/status: 20/{print $2}' $f | sed 's/.*response time://' | statistics.awk
}


collect_curl_output() {
  local file_pattern=$1
  local file_dir=$2
  file_pattern=${file_pattern:=*.out}
  file_dir=${file_dir:=.}

  find ${file_dir} -name "$file_pattern" -exec grep -H -E 'Response time|Response code' {} \;
}


statistics_curl_output() {
  local f=$1
  awk '/Response time/{print $3}' $f | statistics.awk
}

