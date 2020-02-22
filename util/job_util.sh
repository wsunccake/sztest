#!/bin/bash


JENKINS_IP=${JENKINS_IP:="127.0.0.1"}
JENKINS_PORT=${JENKINS_PORT:="8080"}
JENKINS_JAR=${JENKINS_JAR:="jenkins-cli.jar"}
GIT_REPO=${GIT_REPO:-"https://github.com/wsunccake/sztest"}

export JENKINS_USER_ID=${USERNAME:-"username"}
export JENKINS_API_TOKEN="${PASSWORD:-"password"}"

JENKINS_URL="http://${JENKINS_IP}:${JENKINS_PORT}/"
VIEWS=("api_action" "expect_action" "sh_action" "suite_action")


###
### function
###


usage() {
    echo """
  $0 <arg>

argument
  job:           create jobs
  view:          create views
  all:           create jobs and views

environment variable
  JENKINS_IP:    jenkins server ip, default 127.0.0.1
  JENKINS_PORT:  jenkins server port, default 8080
  JENKINS_JAR:   jenkins jar file, default jenkins-cli.jar
  USERNAME:      username for jenkins server
  PASSWORD:      password for jenkins server
"""
}


create_job_xml() {
    local jenkins_job=$1
    local jenkins_file=$2

    cat << EOF > "${TEMP_DIR}/${jenkins_job}.xml"
<?xml version='1.1' encoding='UTF-8'?>
<flow-definition plugin="workflow-job@2.25">
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <com.sonyericsson.rebuild.RebuildSettings plugin="rebuild@1.31">
      <autoRebuild>false</autoRebuild>
      <rebuildDisabled>false</rebuildDisabled>
    </com.sonyericsson.rebuild.RebuildSettings>
  </properties>
  <definition class="org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition" plugin="workflow-cps@2.57.3">
    <scm class="hudson.plugins.git.GitSCM" plugin="git@3.9.0">
      <configVersion>2</configVersion>
      <userRemoteConfigs>
        <hudson.plugins.git.UserRemoteConfig>
          <url>${GIT_REPO}</url>
        </hudson.plugins.git.UserRemoteConfig>
      </userRemoteConfigs>
      <branches>
        <hudson.plugins.git.BranchSpec>
          <name>*/master</name>
        </hudson.plugins.git.BranchSpec>
      </branches>
      <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
      <submoduleCfg class="list"/>
      <extensions/>
    </scm>
    <scriptPath>${jenkins_file}</scriptPath>
    <lightweight>true</lightweight>
  </definition>
  <triggers/>
  <disabled>false</disabled>
</flow-definition>
EOF

    echo ${jenkins_job}.xml
}


create_job() {
    for v in ${VIEWS[*]}; do
        for j in `find ${UTIL_PATH}/../jenkins_job/${v} -name *.groovy -exec basename {} \;`; do
            job_xml=`create_job_xml ${j%%.*} jenkins_job/${v}/${j}`
            echo "cat ${TEMP_DIR}/${job_xml} | java -jar jenkins-cli.jar -s ${JENKINS_URL} create-job ${j%%.*}"
            cat ${TEMP_DIR}/${job_xml} | java -jar jenkins-cli.jar -s ${JENKINS_URL} create-job ${j%%.*}
        done
    done
}


remove_job() {
    for v in ${VIEWS[*]}; do
        for j in `find ${UTIL_PATH}/../jenkins_job/${v} -name *.groovy -exec basename {} \;`; do
            echo "java -jar jenkins-cli.jar -s ${JENKINS_URL} delete-job ${j%%.*}"
            java -jar jenkins-cli.jar -s ${JENKINS_URL} delete-job ${j%%.*}
        done
    done
}


build_job() {
    for v in ${VIEWS[*]}; do
        for j in `find ${UTIL_PATH}/../jenkins_job/${v} -name *.groovy -exec basename {} \;`; do
            echo "java -jar jenkins-cli.jar -s ${JENKINS_URL} build ${j%%.*}"
            java -jar jenkins-cli.jar -s ${JENKINS_URL} build ${j%%.*}
        done
    done
}


create_view_xml() {
    local jenkins_view=$1
    local jenkins_jobs=$2
    local tmp
    tmp=""

    for j in ${jenkins_jobs[*]}; do
        tmp="${tmp}<string>${j}</string>"
    done

    cat << EOF > "${TEMP_DIR}/${jenkins_view}.xml"
<?xml version="1.1" encoding="UTF-8"?>
<hudson.model.ListView>
  <name>${jenkins_view}</name>
  <filterExecutors>false</filterExecutors>
  <filterQueue>false</filterQueue>
  <properties class="hudson.model.View\$PropertyList"/>
  <jobNames>
    <comparator class="hudson.util.CaseInsensitiveComparator"/>
    ${tmp}
  </jobNames>
  <jobFilters/>
  <columns>
    <hudson.views.StatusColumn/>
    <hudson.views.WeatherColumn/>
    <hudson.views.JobColumn/>
    <hudson.views.LastSuccessColumn/>
    <hudson.views.LastFailureColumn/>
    <hudson.views.LastDurationColumn/>
    <hudson.views.BuildButtonColumn/>
    <org.jenkins.plugins.builton.BuiltOnColumn plugin="built-on-column@1.1"/>
    <hudson.plugins.favorite.column.FavoriteColumn plugin="favorite@2.3.2"/>
  </columns>
  <recurse>false</recurse>
</hudson.model.ListView>
EOF

    echo ${jenkins_view}.xml
}

create_view() {
    local job_array

    for v in ${VIEWS[@]}; do
        job_array=()
        for j in `find ${UTIL_PATH}/../jenkins_job/${v} -name *.groovy -exec basename {} \; | sort`; do
            job_array+=("${j%%.*}")
        done

        view_xml=`create_view_xml ${v} "${job_array[*]}"`
        echo "cat ${TEMP_DIR}/${view_xml} | java -jar jenkins-cli.jar -s ${JENKINS_URL} create-view ${v}"
        cat ${TEMP_DIR}/${view_xml} | java -jar jenkins-cli.jar -s ${JENKINS_URL} create-view ${v}
    done
}


remove_view() {
    for v in ${VIEWS[*]}; do
        echo "java -jar jenkins-cli.jar -s ${JENKINS_URL} delete-view ${v}"
        java -jar jenkins-cli.jar -s ${JENKINS_URL} delete-view ${v}
    done
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


if [ "$#" -lt 1 ]; then
    usage
    exit 0
fi

# prepare
make_temp


# job
if [ "$1" == "job" ] || [ "$1" == "all" ]; then
    remove_job
    create_job
    build_job
fi


# view
if [[ "$1" == "view" ]] || [[ "$1" == "all" ]]; then
    remove_view
    create_view
fi


# clean
clean_temp
