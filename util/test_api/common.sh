#!/bih/bash


###
### get all
###

get_all_xx() {
  eval "declare -A api_data="${1#*=}
  local tmp_entity=$(mktemp xx-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)
  local base_url=${api_data['url']}
  local total_count=`pubapi_get "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=100
  for index in $(seq 0 $list_size $total_count); do
    local paging_url="index=${index}&listSize=${list_size}"
    api_data['url']=${base_url}?${paging_url}
    pubapi_get "$(declare -p api_data)" \
    | tee -a "${tmp_entity}"
  done
}


###
### query all
###

  eval "declare -A api_data="${1#*=}
  local tmp_entity=$(mktemp xx-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)
#  local base_data=${api_data['data']}
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
    api_data['data']=${data}
    pubapi_post "$(declare -p api_data)" \
    | tee -a "${tmp_entity}"
  done
}


###
### domain
###

get_domain() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains)
  pubapi_get "$(declare -p api_data)"
}


get_all_domain() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains)
  get_all_xx "$(declare -p api_data)"
}


###
### zone
###

get_zone() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones)
  pubapi_get "$(declare -p api_data)"
}


get_all_zone() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/aps)
  get_all_xx "$(declare -p api_data)"
}


###
### wlan
###

query_all_wlan() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/wlan ['data']='{"attributes": ["*"]}')
  query_all_xx "$(declare -p api_data)"
}


###
### ap
###

get_ap() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/aps)
  pubapi_get "$(declare -p api_data)"
}


get_all_ap() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/aps)
  get_all_xx "$(declare -p api_data)"
}


query_ap() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/ap ['data']='{"attributes": ["*"]}')
  pubapi_post "$(declare -p api_data)"
}


query_all_ap() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/ap ['data']='{"attributes": ["*"]}')
  query_all_xx "$(declare -p api_data)"
}
