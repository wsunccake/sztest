#!/bin/bash

###
### curl
###

sz_curl_cmd() {
  local method=$1
  local url=$2
  local data=$3

  case $method in
  "GET")
    curl --insecure \
         --silent \
         --max-time "${CURL_TIMEOUT}" \
         --cookie "${SZ_COOKIE}" \
         --write-out "\nResponse code: %{http_code}\nResponse time: %{time_total}\n" \
         --request "${method}"\
         --header "content-type: application/json" \
         "${url}"
    ;;
  "POST"|"PUT")
    curl --insecure \
         --silent \
         --max-time "${CURL_TIMEOUT}" \
         --cookie "${SZ_COOKIE}" \
         --write-out "\nResponse code: %{http_code}\nResponse time: %{time_total}\n" \
         --request "${method}"\
         --header "content-type: application/json" \
         --data "${data}" \
         "${url}"
    ;;
  esac
}


###
### pubapi
###

pubapi() {
  eval "declare -A api_data="${1#*=}

  local method=${api_data['method']}
  local url=${api_data['url']}
  local data=${api_data['data']}
  local file=${api_data['file']}

  echo "Request method: ${method}"
  echo "Request URL: ${url}"

  if [ -z "$data" ]; then
    if [ -n "$file" ]; then
      data="@${file}"
      echo "Request body: `jq '.' ${file}`"
    fi
  else
    echo "Request body: ${data}"
  fi

  echo -n 'Response body: '
  sz_curl_cmd ${method} ${url} "${data}"
}


pubapi_get() {
  eval "declare -A api_data="${1#*=}
  api_data['method']=GET
  pubapi "$(declare -p api_data)"
}


pubapi_post() {
  eval "declare -A api_data="${1#*=}
  api_data['method']=POST
  pubapi "$(declare -p api_data)"
}


pubapi_put() {
  eval "declare -A api_data="${1#*=}
  api_data['method']=PUT
  pubapi "$(declare -p api_data)"
}


###
### pubapi login and logout
###

pubapi_login() {
  local username=$1
  local password=$2

  local method="POST"
  local url="${PUBAPI_BASE_URL}/session"
  local data="{
    \"username\": \"${username}\",
    \"password\": \"${password}\"
}"

  echo "Request method: ${method}"
  echo "Request URL: ${url}"
  echo "Request body: ${data}"

  echo -n 'Response body: '
  curl --insecure \
       --silent \
       --max-time "${CURL_TIMEOUT}" \
       --cookie-jar "${SZ_COOKIE}" \
       --write-out "\nResponse code: %{http_code}\nResponse time: %{time_total}\n" \
       --request "${method}"\
       --header "content-type: application/json" \
       --data "${data}" \
       "${url}"
}


pubapi_logout() {
  local method="DELETE"
  local url="${PUBAPI_BASE_URL}/session"

  echo "Request method: ${method}"
  echo "Request URL: ${url}"

  echo -n 'Response body: '
  curl --insecure \
       --silent \
       --max-time "${CURL_TIMEOUT}" \
       --cookie "${COOKIE}" \
       --write-out "\nResponse code: %{http_code}\nResponse time: %{time_total}\n" \
       --request "${method}"\
       --header "content-type: application/json" \
       "${url}"
}


###
### export function
###

export -f sz_curl_cmd
export -f pubapi
export -f pubapi_get
export -f pubapi_post
export -f pubapi_put
export -f pubapi_login
export -f pubapi_logout
