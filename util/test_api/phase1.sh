#!/bin/bash


create_domain() {
  local name=$1
  local data="{
    \"name\": \"${name}\"
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


create_zone() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:="${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"domainId\": \"${domain_id}\",
    \"name\": \"${name}\",
    \"login\": {
        \"apLoginName\": \"${AP_USERNAME}\",
        \"apLoginPassword\": \"${AP_PASSWORD}\"
    }
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


create_open_wlan() {
  local name=$1
  local zone_id=$2
  local data="{
    \"name\": \"${name}\",
    \"ssid\": \"${name}\"
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones/${zone_id}/wlans ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


create_dpsk_wlan() {
  local name=$1
  local zone_id=$2
  local data="{
    \"name\": \"${name}\",
    \"ssid\": \"${name}\",
    \"encryption\": {
        \"method\": \"WPA2\",
        \"algorithm\": \"AES\",
        \"passphrase\": \"1234567890\",
        \"mfp\": \"disabled\"
    },
    \"dpsk\": {
        \"dpskEnabled\": true,
        \"length\": 62,
        \"dpskType\": \"KeyboardFriendly\"
    }
}"
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones/${zone_id}/wlans ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


create_dpsk_batch() {
  local amount=$1
  local zone_id=$2
  local wlan_id=$3
  local data="{
    \"amount\": ${amount}
}"
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones/${zone_id}/wlans/${wlan_id}/dpsk/batchGenUnbound ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


create_wlan_group() {
  local name=$1
  local zone_id=$2
  local data="{
    \"name\": \"${name}\"
}"
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones/${zone_id}/wlangroups ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


create_ap_group() {
  local name=$1
  local zone_id=$2
  local data="{
    \"name\": \"${name}\"
}"
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones/${zone_id}/apgroups ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


###
### export function
###

export -f create_domain
export -f create_zone
export -f create_open_wlan
export -f create_dpsk_wlan
export -f create_dpsk_batch
export -f create_wlan_group
export -f create_ap_group
