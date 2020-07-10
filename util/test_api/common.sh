#!/bih/bash


get_zone() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones)
  pubapi_get "$(declare -p api_data)"
}


get_all_zone() {
  local tmp_entity=$(mktemp zone-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/aps)
  local total_count=`pubapi_get "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=100
  for index in $(seq 0 $list_size $total_count); do
    local paging_url="index=${index}&listSize=${list_size}"
    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones?${paging_url})
    pubapi_get "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .name' \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}



get_ap() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/aps)
  pubapi_get "$(declare -p api_data)"
}


get_all_ap() {
  local tmp_entity=$(mktemp ap-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/aps)
  local total_count=`pubapi_get "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=100
  for index in $(seq 0 $list_size $total_count); do
    local paging_url="index=${index}&listSize=${list_size}"
    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/aps?${paging_url})
    pubapi_get "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .mac, .serial, .zoneId' \
    | paste - - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}

query_ap() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/ap ['data']='{"attributes": ["*"]}')
  pubapi_post "$(declare -p api_data)"
}

query_all_ap() {
  local tmp_entity=$(mktemp ap-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/ap ['data']='{"attributes": ["*"]}')
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
    local data="{
    \"attributes\": [\"*\"],
    \"page\": ${page},
    \"limit\": ${list_size}
}"
    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/ap ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .apMac, .serial, .zoneId, .domainId' \
    | tr -d \" \
    | paste - - - - -d '|' \
    | tee -a "${tmp_entity}"
  done

}
