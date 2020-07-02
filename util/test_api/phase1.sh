#!/bin/bash


create_domain() {
  local name=$1
  local data="{
    \"name\": \"$name\"
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


create_zone() {
  local name=$1
  local domain_id
  domain_id=${domain_id=:"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"domainId\": \"$domain_id\",
    \"name\": \"$name\",
    \"login\": {
        \"apLoginName\": \"$AP_USERNAME\",
        \"apLoginPassword\": \"$AP_PASSWORD\"
    }
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/zones ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}
