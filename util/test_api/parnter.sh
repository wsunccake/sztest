#!/bin/bash


###
### l2acl
###

query_all_l2acl_by_domain_id() {
  local domain_id=$1
  local tmp_entity=$(mktemp l2-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"
 
  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l2AccessControls/query ['data']=$data)
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"
 
    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l2AccessControls/query ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .domainId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


get_l2acl_by_id() {
  local l2acl_id=$1
  local tmp_entity=$(mktemp l2-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

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
  local tmp_entity=$(mktemp l3-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l3AccessControlPolicies/query ['data']=$data)
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"

    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/l3AccessControlPolicies/query ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .domainId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}

get_l3acp_by_id() {
  local l3acp_id=$1
  local tmp_entity=$(mktemp l3-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

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
  local tmp_entity=$(mktemp lbs-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/profiles/lbs/query ['data']=$data)
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"

    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/profiles/lbs/query ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .domainId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


###
### wifi calling
###

query_all_wifi_calling_by_domain_id() {
  local domain_id=$1
  local tmp_entity=$(mktemp wifi-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/wifiCalling/query ['data']=$data)
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"

    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/wifiCalling/query ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .domainId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


###
### device policy
###

query_all_device_policy_by_domain_id() {
  local domain_id=$1
  local tmp_entity=$(mktemp device-policy-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/devicePolicy/query ['data']=$data)
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"

    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/devicePolicy/query ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id' \
    | tr -d \" \
    | paste - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


###
### application policy
###

query_all_application_policy_v2_by_domain_id() {
  local domain_id=$1
  local tmp_entity=$(mktemp application-policy-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/applicationPolicyV2 ['data']=$data)
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"

    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/applicationPolicyV2 ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .domainId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


###
### user defined
###

query_all_user_defined_by_domain_id() {
  local domain_id=$1
  local tmp_entity=$(mktemp user-defined-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/userDefined ['data']=$data)
  pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.'
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"

    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/query/userDefined ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .domainId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


###
### proxy auth
###

query_all_proxy_auth_by_domain_id() {
  local domain_id=$1
  local tmp_entity=$(mktemp proxy-auth-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/auth/query ['data']=$data)
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"

    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/auth/query ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .domainId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


###
### proxy acct
###

query_all_proxy_acct_by_domain_id() {
  local domain_id=$1
  local tmp_entity=$(mktemp proxy-acct-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/acct/query ['data']=$data)
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"

    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/services/acct/query ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .domainId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


###
### vlan pooling
###

query_all_vlan_pooling_by_domain_id() {
  local domain_id=$1
  local tmp_entity=$(mktemp vlanpooling-$SZ_IP-XXXXXXXXXX --tmpdir=/tmp)

  local data="{
  \"attributes\": [\"*\"],
  \"filters\": [{\"type\": \"DOMAIN\",
                 \"value\": \"${domain_id}\"
  }]
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/vlanpoolings/query ['data']=$data)
  local total_count=`pubapi_post "$(declare -p api_data)" | sed -n 's/Response body: //p' | jq '.totalCount'`

  local list_size=1000
  local page=1
  for index in $(seq 1 $list_size $total_count); do
  local data="{
    \"attributes\": [\"*\"],
    \"filters\": [{\"type\": \"DOMAIN\",
                   \"value\": \"${domain_id}\"
    }],
    \"page\": ${page},
    \"limit\": ${list_size}
}"

    page=$(($page + 1))

    declare -A api_data=(['url']=${PUBAPI_BASE_URL}/vlanpoolings/query ['data']=$data)
    pubapi_post "$(declare -p api_data)" \
    | sed -n 's/Response body: //p' \
    | jq --raw-output '.list[] | .id, .domainId' \
    | tr -d \" \
    | paste - - -d '|' \
    | tee -a "${tmp_entity}"
  done
}


###
### export function
###

#export -f query_all_l2acl_by_domain_id
#export -f get_l2acl_by_id
#export -f put_l2acl
#
#export -f query_all_l3acp_by_domain_id
#export -f get_l3acp_by_id
#export -f put_l3acp
#
#export -f query_all_lbs_by_domain_id
