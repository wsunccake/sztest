#!/bin/bash


add_entry_on_lls() {
  local host_id=$1
  local feature_name=$2
  local version=$3
  local count=$4
  local device_type=${LOCAL_LICENSE_SERVER_DEVICE_TYPE:=api-perf}
  local device_alias=${LOCAL_LICENSE_SERVER_IP_DEVICE_ALIAS:=api-perf}

  local tmp_dir=$(mktemp lls-${SZ_IP}-XXXXXXXXXX -d --tmpdir=/tmp)
  local method
  local url
  local token
  local data

  # send view request
  method="GET"
  url="http://${LOCAL_LICENSE_SERVER_IP}:8080/fneserver/reservation_view.action"

  echo "Request method: ${method}"
  echo "Request URL: ${url}"

  echo -n 'Response body: '
  curl --max-time "${CURL_TIMEOUT}" \
     --cookie-jar "/tmp/cookie-${LOCAL_LICENSE_SERVER_IP}" \
     --request "${method}"\
     "${url}" > ${tmp_dir}/view.html

  token=`sed 's/"/ /g' ${tmp_dir}/view.html | awk '/name= addReservationToken/{print $7}'`


  # send add request
  method="POST"
  url="http://${LOCAL_LICENSE_SERVER_IP}:8080/fneserver/reservation_add.action"
  data="reservation.device.hostId=${host_id}&idType=String&reservation.device.deviceType=${device_type}&reservation.device.deviceAlias=${device_type}&reservation.featureName=${feature_name}&reservation.version=${version}&reservation.count=${count}&struts.token.name=addReservationToken&addReservationToken=${token}"

  echo "Request method: ${method}"
  echo "Request URL: ${url}"
  echo "Request body: ${data}"

  echo -n 'Response body: '
  curl --max-time "${CURL_TIMEOUT}" \
     --cookie "/tmp/cookie-${LOCAL_LICENSE_SERVER_IP}" \
     --request "${method}"\
     --header "content-type: application/x-www-form-urlencoded" \
     --data "${data}" \
     "${url}" > ${tmp_dir}/add.html

  token=`sed 's/"/ /g' ${tmp_dir}/add.html | awk '/name= addReservationToken/{print $7}'`


  # send save request
  method="POST"
  url="http://${LOCAL_LICENSE_SERVER_IP}:8080/fneserver/reservation_save.action"
  data="reservation.device.hostId=${host_id}&idType=String&reservation.device.deviceType=${device_type}&reservation.device.deviceAlias=${device_type}&reservation.featureName=${feature_name}&reservation.version=${version}&reservation.count=${count}&struts.token.name=addReservationToken&addReservationToken=${token}"

  echo "Request method: ${method}"
  echo "Request URL: ${url}"
  echo "Request body: ${data}"

  echo -n 'Response body: '
  curl --max-time "${CURL_TIMEOUT}" \
     --cookie "/tmp/cookie-${LOCAL_LICENSE_SERVER_IP}" \
     --request "${method}"\
     --header "content-type: application/x-www-form-urlencoded" \
     --data "${data}" \
     "${url}" > ${tmp_dir}/save.html
}


del_entry_on_lls() {
  local host_id=$1

  local tmp_dir=$(mktemp lls-${SZ_IP}-XXXXXXXXXX -d --tmpdir=/tmp)
  local method
  local url
  local token
  local data

  # send view request
  method="GET"
  url="http://${LOCAL_LICENSE_SERVER_IP}:8080/fneserver/reservation_view.action"

  echo "Request method: ${method}"
  echo "Request URL: ${url}"

  echo -n 'Response body: '
  curl --max-time "${CURL_TIMEOUT}" \
     --cookie-jar "/tmp/cookie-${LOCAL_LICENSE_SERVER_IP}" \
     --request "${method}"\
     "${url}" > ${tmp_dir}/view.html

  # send del request
  method="POST"
  url="http://${LOCAL_LICENSE_SERVER_IP}:8080/fneserver/device_delete.action"
  data="device.hostId=${host_id}&device.deviceIdType=string&page=1"

  echo "Request method: ${method}"
  echo "Request URL: ${url}"
  echo "Request body: ${data}"

  echo -n 'Response body: '
  curl --max-time "${CURL_TIMEOUT}" \
     --cookie "/tmp/cookie-${LOCAL_LICENSE_SERVER_IP}" \
     --request "${method}"\
     --header "content-type: application/x-www-form-urlencoded" \
     --data "${data}" \
     "${url}" > ${tmp_dir}/del.html
}