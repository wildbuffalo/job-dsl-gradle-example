package src

import groovy.json.*

@Library('ds1-marketing-jenkins-library@master') _
pipeline {
    agent any
    options {
        disableConcurrentBuilds()
        skipDefaultCheckout true
        sauce('saucelabs')
    }
    post {
        cleanup {
            cleanWs()
            dir("${env.WORKSPACE}@tmp") {
                cleanWs()
            }
            node('master') {
                dir("${env.WORKSPACE}@libs") {
                    cleanWs()
                }
                dir("${env.WORKSPACE}@script") {
                    cleanWs()
                }
            }
        }
    }
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '6331db84-0ca0-4396-a946-afa1e804158f', url: 'https://github.com/MerrillCorporation/dealworks-ui-tests.git']]])
            }
        }
        stage('Build') {
            steps {
                script {
                    getDockerfile()
                    docker.withRegistry('https://merrillcorp-dealworks.jfrog.io', 'mrll-artifactory') {
                        def dockerfile = './qa.Dockerfile'
                        tools_image = docker.build("dealworks-app/qa:latest", "--pull --rm -f ${dockerfile} .")
                        tools_image.inside() {
                            sh "bundle exec parallel_cucumber features/ -n $params.threads -o \"-t @$params.tag env=$params.env sys=$params.system jobExecutionPlatform=jenkins -f json -o cucumber.json -f html -o index.html -f json_pretty -o prettycucumber.json -f junit -o junit -f pretty --retry 1 \" | ruby ./generateReport.rb"
                        }
                    }
                }
            }
            post {
                always {

                    sh 'cat testResults.json'
                    junit testDataPublishers: [[$class: 'SauceOnDemandReportPublisher']], testResults: 'junit/*.xml'
//                        xunit testDataPublishers: [[$class: 'SauceOnDemandReportPublisher']], tools: [JUnit(deleteOutputFiles: true, failIfNotNew: true, pattern: 'junit/*.xml', skipNoTestFiles: false, stopProcessingIfError: true)]
//                        xunit testDataPublishers: tools: [JUnit(deleteOutputFiles: true, failIfNotNew: true, pattern: 'junit/*.xml', skipNoTestFiles: false, stopProcessingIfError: true)]
//                        xunit([JUnit(deleteOutputFiles: true, failIfNotNew: true, pattern: 'junit/*.xml', skipNoTestFiles: false, stopProcessingIfError: true)])
//                          step([$class: 'JUnitResultArchiver', testDataPublishers: [[$class: 'SauceOnDemandReportPublisher']], testResults: 'junit/*.xml'])
//                        sh 'cat cucumber.json'
//                        cucumber fileIncludePattern: 'cucumber.json', sortingMethod: 'ALPHABETICAL'
//                        cucumberSlackSend channel: 'alrt-ds1-marketing', json: 'cucumber.json'
                }
                success {
                    slackMessage("good")
                }
                unstable {
                    slackMessage("danger")
                }
                failure {
                    slackMessage("danger")
                }
            }
        }
    }
}

def getDockerfile() {
    writeFile file: 'qa.Dockerfile', text: '''FROM ruby:alpine3.8
# Create a group and user
# RUN echo "jenkins ALL=NOPASSWD: ALL" >> /etc/sudoers
RUN addgroup -g 1000 -S jenkins &&\
    adduser -u 1000 -S jenkins -G jenkins
RUN apk add --no-cache make gcc g++ vim
# throw errors if Gemfile has been modified since Gemfile.lock
# RUN bundle config --global frozen 1

WORKDIR /home/jenkins/app

COPY Gemfile /home/jenkins/app/
# COPY Gemfile.lock /home/jenkins/app/
RUN bundle install --path /home/jenkins/bundle

COPY . .'''

}

def slackMessage(colorCode) {
    def props = readJSON file: 'testResults.json'
    attachmenPayload = [[
                                fallback  : "${env.JOB_NAME} execution #${env.BUILD_NUMBER}",
                                color     : colorCode,
                                title     : "${env.JOB_NAME} with tag ${params.tag}",
                                title_link: "${env.BUILD_URL}",
                                text      : "",
                                fields    :
                                        [
                                                [
                                                        title: "Scenarios",
                                                        value: props.report.totalScenarios,
                                                        short: true
                                                ], [
                                                        title: "Failed",
                                                        value: props.report.totalFailed,
                                                        short: true

                                                ], [
                                                        title: "Success",
                                                        value: props.report.totalSuccess,
                                                        short: true
                                                ], [
                                                        title: "Skipped",
                                                        value: props.report.totalSkipped,
                                                        short: true
                                                ], [
                                                        title: "Undefined",
                                                        value: props.report.totalUndefined,
                                                        short: true
                                                ], [
                                                        title: "Duration",
                                                        value: props.report.totalDuration,
                                                        short: true
                                                ]
                                        ]
                        ]]
    slackSend(channel: '#ds1-marketing-qa', color: colorCode, attachments: new JsonBuilder(attachmenPayload).toPrettyString())

}
