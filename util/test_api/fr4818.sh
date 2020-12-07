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

delete_zone() {
  local id=$1

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones/${id})
  pubapi_delete "$(declare -p api_data)"
}

delete_lbs() {
  local id=$1

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/profiles/lbs/${id})
  pubapi_delete "$(declare -p api_data)"
}

gen_qc_template_by_domain_id() {
  local domain_id=$1
  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  echo ${data}
}

query_all_lbs_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/profiles/lbs/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id' | tr -d \" | paste - -d '|'
}

###
### export function
###

export -f create_zone
export -f create_lbs
export -f create_ap_group
export -f patch_zone_with_lbs
export -f delete_zone
export -f delete_lbs

