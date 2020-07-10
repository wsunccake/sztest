#!/bin/bash


###
### jenkins job
###

create-job-xml() {
  local jenkins_job=$1
  local jenkins_file=$2
  local jenkins_xml=$3

  cat << EOF > $jenkins_xml
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
          <name>*/${GIT_BRANCH}</name>
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
}


list-job() {
  local name=$1

  echo "java -jar $JENKINS_JAR -s $JENKINS_URL list-jobs $name"
  java -jar $JENKINS_JAR -s $JENKINS_URL list-jobs $name
}


create-job() {
  local name=$1
  local job_xml=$2

  echo "cat $job_xml | java -jar $JENKINS_JAR -s $JENKINS_URL create-job $name"
  cat $job_xml | java -jar $JENKINS_JAR -s $JENKINS_URL create-job $name
}


delete-job() {
  local name=$1

  echo "java -jar $JENKINS_JAR -s $JENKINS_URL delete-job $name"
  java -jar $JENKINS_JAR -s $JENKINS_URL delete-job $name
}


build_job() {
    for v in ${VIEWS[*]}; do
        for j in `find ${UTIL_PATH}/../jenkins_job/${v} -name *.groovy -exec basename {} \;`; do
            echo "java -jar jenkins-cli.jar -s ${JENKINS_URL} build ${j%%.*}"
            java -jar jenkins-cli.jar -s ${JENKINS_URL} build ${j%%.*}
        done
    done
}


###
### jenkins view
###

get-view() {
  local name=$1

  echo "java -jar $JENKINS_JAR -s $JENKINS_URL get-view $name"
  java -jar $JENKINS_JAR -s $JENKINS_URL get-view $name
}


create-view-xml() {
    local jenkins_view=$1
    local jenkins_xml=$2

    cat << EOF > $jenkins_xml
<?xml version="1.1" encoding="UTF-8"?>
<hudson.model.ListView>
  <name>${jenkins_view}</name>
  <filterExecutors>false</filterExecutors>
  <filterQueue>false</filterQueue>
  <properties class="hudson.model.View\$PropertyList"/>
  <jobNames>
    <comparator class="hudson.util.CaseInsensitiveComparator"/>
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
}


create-view() {
  local view_xml=$1

  echo "cat $view_xml |java -jar $JENKINS_JAR -s $JENKINS_URL create-view"
  cat $view_xml |java -jar $JENKINS_JAR -s $JENKINS_URL create-view
}


delete-view() {
  local view=$1

  echo "java -jar jenkins-cli.jar -s ${JENKINS_URL} delete-view $view"
  java -jar jenkins-cli.jar -s ${JENKINS_URL} delete-view $view
}


add-job-to-view() {
  local view=$1
  local job=$2

  echo "java -jar jenkins-cli.jar -s ${JENKINS_URL} add-job-to-view $view $job"
  java -jar jenkins-cli.jar -s ${JENKINS_URL} add-job-to-view $view $job
}


remove-job-to-view() {
  local view=$1
  local job=$2

  echo "java -jar jenkins-cli.jar -s ${JENKINS_URL} remove-job-to-view $view $job"
  java -jar jenkins-cli.jar -s ${JENKINS_URL} remove-job-to-view $view $job
}

