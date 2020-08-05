#!/bin/bash


###
### controller
###

get_controller() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/controller)
  pubapi_get "$(declare -p api_data)"
}


###
### domain
###

get_domain() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains)
  pubapi_get "$(declare -p api_data)"
}


get_domain_by_name() {
  local name=$1

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains/byName/${name})
  pubapi_get "$(declare -p api_data)"
}


get_all_domain() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains)
  get_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .name' | tr -d \" | paste -d '|' - -
#  get_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .name' | tr -d \" | paste -d '|' - - | sort -k 2 -t '|'
}


###
### zone
###

get_zone() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones)
  pubapi_get "$(declare -p api_data)"
}


get_all_zone() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones)
  get_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .name' | tr -d \" | paste -d '|' - -
}


###
### wlan
###

query_all_wlan() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/wlan ['data']='{"attributes": ["*"]}')
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .wlanId, .name, .zoneId, .zoneName, .domainName' | tr -d \" | paste -d '|' - - - - -
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
  get_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .mac, .serial, .zoneId' | tr -d \" | paste -d '|' - - -
}


query_ap() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/ap ['data']='{"attributes": ["*"]}')
  pubapi_post "$(declare -p api_data)"
}


query_all_ap() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/ap ['data']='{"attributes": ["*"]}')
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .apMac, .serial, .zoneId, .domainId' | tr -d \" | paste -d '|' - - - -
}


monitor_ap() {
  local domain_id=$1
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"filters\": [{
      \"type\": \"DOMAIN\",
      \"value\": \"${domain_id}\"
    }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/monitor/ap ['data']=${data})
  pubapi_post "$(declare -p api_data)"
}


query_ap_up-to-date() {
  local data="{
    \"attributes\": [\"*\"],
    \"fullTextSearch\": {
        \"type\": \"AND\",
        \"value\": \"Up-to-date\"
    }
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/ap ['data']=${data})
  pubapi_post "$(declare -p api_data)"
}


###
### ue
###

monitor_ue() {
  local domain_id=$1
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"filters\": [{
      \"type\": \"DOMAIN\",
      \"value\": \"${domain_id}\"
    }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/monitor/client ['data']=${data})
  pubapi_post "$(declare -p api_data)"
}


###
### local license server
###

update_local_license_server() {
  local license_server_ip=$1
  local license_server_port=$2
  license_server_port=${license_server_port:-"3333"}
  local data="{
    \"ipAddress\": \"${license_server_ip}\",
    \"port\": ${license_server_port},
    \"useCloud\": false
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/licenseServer ['data']=${data})
  pubapi_put "$(declare -p api_data)"
}


sync_local_license_server() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/licenses/sync)
  pubapi_put "$(declare -p api_data)"
}
