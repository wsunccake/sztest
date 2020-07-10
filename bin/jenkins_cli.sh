#!/bin/bash

ACTION=$1

###
### import util
###

SCRIPT_DIR="$(dirname `realpath $0`)"
LIB_DIR=$SCRIPT_DIR/../util
source $LIB_DIR/jenkins_util.sh


###
### define variable
###

JENKINS_IP=${JENKINS_IP:=127.0.0.1}
JENKINS_PORT=${JENKINS_PORT:=8080}
JENKINS_JAR=${JENKINS_JAR:=jenkins-cli.jar}
GIT_REPO=${GIT_REPO:-https://github.com/wsunccake/sztest-pipeline}
GIT_BRANCH=${GIT_BRANCH:-master}

export JENKINS_USER_ID=${JENKINS_USER_ID:-"username"}
export JENKINS_API_TOKEN="${JENKINS_API_TOKEN:-"password"}"

JENKINS_URL=http://${JENKINS_IP}:${JENKINS_PORT}/


###
### function
###

usage() {
  echo """
  $0 <arg>

argument
  help
  about

  list-view <view_name>
  create-view <view_name>
  delete-view <view_name>
  add-job-to-view <view_name> <job_name>
  remove-job-to-view <view_name> <job_name>

  list-job
  create-job <job_name> <pipeline_file>
  delete-job <job_name>

environment variable
  JENKINS_IP:         jenkins server ip, default 127.0.0.1
  JENKINS_PORT:       jenkins server port, default 8080
  JENKINS_JAR:        jenkins jar file, default jenkins-cli.jar
  JENKINS_USER_ID:    username for jenkins server
  JENKINS_API_TOKEN:  password for jenkins server
"""
}


about() {
  echo "JENKINS_IP: $JENKINS_IP"
  echo "JENKINS_PORT: $JENKINS_PORT"
  echo "JENKINS_JAR: $JENKINS_JAR"
  echo "GIT_REPO: $GIT_REPO"
  echo "GIT_BRANCH: $GIT_BRANCH"

  echo "JENKINS_USER_ID: $JENKINS_USER_ID"
  echo "JENKINS_API_TOKEN: $JENKINS_API_TOKEN"

  echo "JENKINS_URL: $JENKINS_URL"
}


download_jenkin_jar() {
  curl -o $JENKINS_JAR http://${JENKINS_IP}:${JENKINS_PORT}/jnlpJars/jenkins-cli.jar
}

make_temp() {
    TEMP_DIR=`mktemp -d`
    UTIL_PATH=`dirname $0`
    echo "create temporary dir: ${TEMP_DIR}"
}


clean_temp() {
    rm -rf ${TEMP_DIR}
    echo "remove temporary dir: ${TEMP_DIR}"
}



###
### main
###

[ ! -f $JENKINS_JAR ] && download_jenkin_jar

case $ACTION in
  "help")
    usage
  ;;

  "about")
    about
  ;;

  "list-view")
    VIEW_NAME=$2
    list-job $VIEW_NAME
  ;;

  "create-view")
    VIEW_NAME=$2
    make_temp
    create-view-xml $VIEW_NAME $TEMP_DIR/${VIEW_NAME}.xml
    create-view $TEMP_DIR/${VIEW_NAME}.xml
    clean_temp
  ;;

  "delete-view")
    VIEW_NAME=$2
    delete-view $VIEW_NAME
  ;;

  "add-job-to-view")
    VIEW_NAME=$2
    JOB_NAME=$3
    add-job-to-view $VIEW_NAME $JOB_NAME
  ;;

  "remove-job-to-view")
    VIEW_NAME=$2
    JOB_NAME=$3
    remove-job-to-view $VIEW_NAME $JOB_NAME
  ;;

  "list-job")
    list-job
  ;;

  "create-job")
    JOB_NAME=$2
    PIPELINE_FILE=$3
    make_temp
    create-job-xml $JOB_NAME $PIPELINE_FILE $TEMP_DIR/${JOB_NAME}.xml
    create-job $JOB_NAME $TEMP_DIR/${JOB_NAME}.xml
    clean_temp
  ;;

  "delete-job")
    JOB_NAME=$2
    delete-job $JOB_NAME
  ;;

  *)
    usage
  ;;
esac

