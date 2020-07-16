#!/bin/bash


###
### query criteria filter template
###

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


###
### l2acl
###

query_all_l2acl_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l2AccessControls/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


get_l2acl_by_id() {
  local l2acl_id=$1

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l2AccessControls/$l2acl_id ['data']=$data)
  pubapi_get "$(declare -p api_data)"
}


put_l2acl() {
  local l2acl_id=$1
  local file=$2

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l2AccessControls/$l2acl_id ['file']=$file)
  pubapi_put "$(declare -p api_data)"
}


###
### l3acp
###

query_all_l3acp_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l3AccessControlPolicies/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
  }

get_l3acp_by_id() {
  local l3acp_id=$1

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l3AccessControlPolicies/$l3acp_id ['data']=$data)
  pubapi_get "$(declare -p api_data)"
}


put_l3acp() {
  local l3acp_id=$1
  local file=$2

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l3AccessControlPolicies/$l3acp_id['file']=$file)
  pubapi_put "$(declare -p api_data)"
}


###
### lbs
###

query_all_lbs_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/profiles/lbs/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### wifi calling
###

query_all_wifi_calling_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/wifiCalling/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### device policy
###

query_all_device_policy_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/devicePolicy/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id' | tr -d \" | paste - -d '|'
}


###
### application policy
###

query_all_application_policy_v2_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/applicationPolicyV2 ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### user defined
###

query_all_user_defined_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/userDefined ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### proxy auth
###

query_all_proxy_auth_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/auth/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### proxy acct
###

query_all_proxy_acct_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/acct/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### vlan pooling
###

query_all_vlan_pooling_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/vlanpoolings/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### export function
###
