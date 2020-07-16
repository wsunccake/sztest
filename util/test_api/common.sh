#!/bin/bash


###
### domain
###

get_domain() {
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains)
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
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .wlanId, .name, .zoneId, .zoneName' | tr -d \" | paste -d '|' - - - -
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
