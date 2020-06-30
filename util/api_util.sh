#!/bin/bash

###
### pubapi
###

pubapi_get() {
  eval "declare -A api_data="${1#*=}

  CURL_TIMEOUT=${CURL_TIMEOUT:=30}
  COOKIE=${COOKIE:=/tmp/cookie-$SZ_IP}

  local method=GET
  local url=${api_data['url']}

  echo "Request method: ${method}"
  echo "Request URL: ${url}"
  echo "Request body: ${data}"

  echo -n 'Response body: '
  curl --insecure \
       --silent \
       --max-time "${CURL_TIMEOUT}" \
       --cookie "${COOKIE}" \
       --write-out "\nResponse code: %{http_code}\nResponse time: %{time_total}\n" \
       --request "${method}"\
       --header "content-type: application/json" \
       "${url}"
}


pubapi_post() {
  eval "declare -A input="${1#*=}

  local method=POST
  local url=${api_data['url']}
  local data=${api_data['data']}

  echo "Request method: ${method}"
  echo "Request URL: ${url}"
  echo "Request body: ${data}"

  echo -n 'Response body: '
  curl --insecure \
       --silent \
       --max-time "${CURL_TIMEOUT}" \
       --cookie "${COOKIE}" \
       --write-out "\nResponse code: %{http_code}\nResponse time: %{time_total}\n" \
       --request "${method}"\
       --header "content-type: application/json" \
       --data "${data}" \
       "${url}"
}


###
### pubapi login and logout
###

pubapi_login() {
  local username=$1
  local password=$2

  PROTOCOL=${PROTOCOL:=https}
  SZ_IP=${SZ_IP:=127.0.0.1}
  SZ_PORT=${SZ_PORT:=8443}
  CURL_TIMEOUT=${CURL_TIMEOUT:=30}
  COOKIE=${COOKIE:=/tmp/cookie-$SZ_IP}

  if [ -z $API_VERSION ]; then
    echo "no found var: API_VERSION"
    exit 1
  fi

  local method="POST"
  local url="${PROTOCOL}://${SZ_IP}:${SZ_PORT}/wsg/api/public/${API_VERSION}/session"
  local data="{
    \"username\": \"${username}\",
    \"password\": \"${password}\"
}"

  echo "Request method: ${method}"
  echo "Request URL: ${url}"
  echo "Request body: ${data}"

  echo -n 'Response body: '
  curl --insecure \
       --silent \
       --max-time "${CURL_TIMEOUT}" \
       --cookie-jar "${COOKIE}" \
       --write-out "\nResponse code: %{http_code}\nResponse time: %{time_total}\n" \
       --request "${method}"\
       --header "content-type: application/json" \
       --data "${data}" \
       "${url}"
}


pubapi_logout() {
  PROTOCOL=${PROTOCOL:=https}
  SZ_IP=${SZ_IP:=127.0.0.1}
  SZ_PORT=${SZ_PORT:=8443}
  CURL_TIMEOUT=${CURL_TIMEOUT:=30}
  COOKIE=${COOKIE:=/tmp/cookie-$SZ_IP}

  if [ -z $API_VERSION ]; then
    echo "no found var: API_VERSION"
    exit 1
  fi

  local method="DELETE"
  local url="${PROTOCOL}://${SZ_IP}:${SZ_PORT}/wsg/api/public/${API_VERSION}/session"

  echo "Request method: ${method}"
  echo "Request URL: ${url}"

  echo -n 'Response body: '
  curl --insecure \
       --silent \
       --max-time "${CURL_TIMEOUT}" \
       --cookie "${COOKIE}" \
       --write-out "\nResponse code: %{http_code}\nResponse time: %{time_total}\n" \
       --request "${method}"\
       --header "content-type: application/json" \
       "${url}"
}


###
### demo
###

get_domain() {
  declare -A api_data=(['url']=${PROTOCOL}://${SZ_IP}:${SZ_PORT}/wsg/api/public/${API_VERSION}/domains)
  pubapi_get "$(declare -p api_data)"
}


get_all_domain() {
  local tmp_entity=$(mktemp domain-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  declare -A api_data=(['url']=${PROTOCOL}://${SZ_IP}:${SZ_PORT}/wsg/api/public/${API_VERSION}/domains)
  local total_count=`pubapi_get "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=100
  for index in $(seq 0 $list_size $total_count); do
    local paging_url="index=${index}&listSize=${list_size}"
    declare -A api_data=(['url']=${PROTOCOL}://${SZ_IP}:${SZ_PORT}/wsg/api/public/${API_VERSION}/domains?${paging_url})
    pubapi_get "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .name' \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


query_all_wlan() {
  local tmp_entity=$(mktemp wlan-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  declare -A api_data=(['url']=${PROTOCOL}://${SZ_IP}:${SZ_PORT}/wsg/api/public/${API_VERSION}/query/wlan ['data']='{"attributes": ["*"]}')
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=100
  local page=1
  for index in $(seq 1 $list_size $total_count); do
    local data="{
    \"attributes\": [\"*\"],
    \"page\": ${page},
    \"limit\": ${list_size}
}"
    page=$(($page + 1))

    declare -A api_data=(['url']=${PROTOCOL}://${SZ_IP}:${SZ_PORT}/wsg/api/public/${API_VERSION}/query/wlan ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .wlanId, .zoneId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}

