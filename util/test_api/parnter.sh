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
### partner domain
###

create_partner_domain() {
  local name=$1
  local parent_domain_id=$2
  parent_domain_id=${parent_domain_id:="${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainType\": \"PARTNER\",
    \"parentDomainId\": \"${parent_domain_id}\"
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


###
### zone
###

create_zone() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"domainId\": \"${domain_id}\",
    \"name\": \"${name}\",
    \"login\": {
        \"apLoginName\": \"admin\",
        \"apLoginPassword\": \"!lab4man1\"
    }
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/rkszones ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


###
### proxy auth
###

create_auth_service() {
  local name=$1
  local ip=$2
  local port=$3
  local secret=$4
  local domain_id=$5
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"primary\": {
        \"ip\": \"${ip}\",
        \"port\": ${port},
        \"sharedSecret\": \"${secret}\"
    }
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/auth/radius ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


query_all_proxy_auth_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/auth/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### proxy acct
###

create_acct_service() {
  local name=$1
  local ip=$2
  local port=$3
  local secret=$4
  local domain_id=$5
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"primary\": {
        \"ip\": \"${ip}\",
        \"port\": ${port},
        \"sharedSecret\": \"${secret}\"
    }
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/acct/radius ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


query_all_proxy_acct_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/acct/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### vlan pooling
###

create_vlan_pooling() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"pool\": \"10\",
    \"algo\": \"MAC_HASH\"
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/vlanpoolings ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


query_all_vlan_pooling_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/vlanpoolings/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### application policy
###

create_application_policy() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"avcEventEnable\": false,
    \"avcLogEnable\": false,
    \"applicationRules\": [
      {
        \"ruleType\": \"DENY\",
        \"applicationType\": \"SIGNATURE\",
        \"catId\": \"13\",
        \"appId\": \"0\",
        \"priority\": 1,
        \"catName\": \"Game\",
        \"appName\": \"All\",
        \"uplink\": 0,
        \"downlink\": 0
      }
    ]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/avc/applicationPolicyV2 ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


query_all_application_policy_v2_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/applicationPolicyV2 ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### user defined
###

create_user_defined() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"type\": \"PORT_ONLY\",
    \"destPort\": 4321,
    \"protocol\": \"TCP\"
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/avc/userDefined ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


###
### l2acl
###

create_l2acl() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"restriction\": \"BLOCK\",
    \"rules\": [
        {\"mac\": \"11:22:33:44:55:66\"}
    ],
    \"etherTypeRestriction\": \"ALLOW\"
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l2AccessControls ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


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

create_l3acp() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"defaultAction\": \"ALLOW\",
    \"l3AclRuleList\": []
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l3AccessControlPolicies ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


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


query_all_lbs_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/profiles/lbs/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### wifi calling
###

create_wifi_calling_policy() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"priority\": \"VOICE\",
    \"epdgs\": [
      {
        \"fqdn\": \"fake.com\",
        \"ip\": \"1.2.3.4\"
      }
    ]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/wifiCalling/wifiCallingPolicy ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


query_all_wifi_calling_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/wifiCalling/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id, .domainId' | tr -d \" | paste - - -d '|'
}


###
### device policy
###

create_domain_device_policy() {
  local name=$1
  local domain_id=$2
  domain_id=${domain_id:-"${DEFAULT_DOMAIN_UUID}"}
  local data="{
    \"name\": \"${name}\",
    \"domainId\": \"${domain_id}\",
    \"defaultAction\": \"ALLOW\",
    \"rule\": [
      {
        \"description\": \"rule1\",
        \"action\": \"ALLOW\",
        \"deviceType\": \"LAPTOP\",
        \"osVendor\": \"ALL\",
        \"vlan\": null,
        \"uplink\": 0,
        \"downlink\": 0
      }
    ]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/devicePolicy ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}


query_all_device_policy_by_domain_id() {
  local domain_id=$1
  local data="`gen_qc_template_by_domain_id ${domain_id}`"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/devicePolicy/query ['data']=$data)
  query_all_xx "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.list[] | .id' | tr -d \" | paste - -d '|'
}


delete_domain_device_policy() {
  local id=$1
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/devicePolicy/${id})
  pubapi_delete "$(declare -p api_data)"
}


###
### export function
###

export -f create_partner_domain
export -f create_zone
export -f create_auth_service
export -f create_acct_service
export -f create_vlan_pooling
export -f create_application_policy
export -f create_user_defined
export -f create_l2acl
export -f create_l3acp
export -f create_lbs
export -f create_wifi_calling_policy
export -f create_domain_device_policy

export -f delete_domain_device_policy
