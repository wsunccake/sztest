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
### export function
###

export -f query_all_l2acl_by_domain_id
export -f get_l2acl_by_id
export -f put_l2acl

export -f query_all_l3acp_by_domain_id
export -f get_l3acp_by_id
export -f put_l3acp

export -f query_all_lbs_by_domain_id
