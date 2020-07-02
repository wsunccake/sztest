#!/bin/bash


create_domain() {
  local name=$1
  local data="{
    \"name\": $name
}"

  declare -A api_data=(['url']=${PUBAPI_BASE_URL}/domains ['data']=$data)
  pubapi_post "$(declare -p api_data)"
}
