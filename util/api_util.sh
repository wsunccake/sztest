#!/bin/bash


###
### pubapi
###

pubapi_get() {
  eval "declare -A api_data="${1#*=}

  local method=GET
  local url=${api_data['url']}

  echo "Request method: ${method}"
  echo "Request URL: ${url}"
  echo "Request body: ${data}"

  echo -n 'Response body: '
  curl --insecure \
       --silent \
       --max-time "${CURL_TIMEOUT}" \
       --cookie "${SZ_COOKIE}" \
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
       --cookie "${SZ_COOKIE}" \
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

  local method="POST"
  local url="${PUBAPI_BASE_URL}/session"
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
       --cookie-jar "${SZ_COOKIE}" \
       --write-out "\nResponse code: %{http_code}\nResponse time: %{time_total}\n" \
       --request "${method}"\
       --header "content-type: application/json" \
       --data "${data}" \
       "${url}"
}


pubapi_logout() {
  local method="DELETE"
  local url="${PUBAPI_BASE_URL}/session"

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
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains)
  pubapi_get "$(declare -p api_data)"
}


get_all_domain() {
  local tmp_entity=$(mktemp domain-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains)
  local total_count=`pubapi_get "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=100
  for index in $(seq 0 $list_size $total_count); do
    local paging_url="index=${index}&listSize=${list_size}"
    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains?${paging_url})
    pubapi_get "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .name' \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


query_all_wlan() {
  local tmp_entity=$(mktemp wlan-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/wlan ['data']='{"attributes": ["*"]}')
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

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/wlan ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .wlanId, .name, .zoneId, .zoneName' \
    | tr -d \" \
    | paste - - - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


###
### export function
###

export -f pubapi_get
export -f pubapi_post
export -f pubapi_login
export -f pubapi_logout
