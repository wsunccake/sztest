#!/bin/bash

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

create_lbs() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"venue\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"url\": \"1.2.3.4\",
    \"port\": 8883,
    \"password\": \"admin!234\"
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/profiles/lbs ['data']=$data)
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

patch_zone_with_lbs() {
  local zone_id=$1
  local lbs_id=$2
  local data="{
    \"locationBasedService\": {
        \"id\": \"${lbs_id}\"
    }
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones/${zone_id} ['data']=$data)
  pubapi_patch "$(declare -p api_data)"
}

###
### export function
###

export -f create_zone
export -f create_lbs
export -f create_ap_group
export -f patch_zone_with_lbs
