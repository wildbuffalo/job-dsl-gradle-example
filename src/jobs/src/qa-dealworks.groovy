package src
@Library('ds1-marketing-jenkins-library@master') _
pipeline {
    agent any
    environment {
        SAUCE = credentials('saucelabs')
        SAUCE_USERNAME = $SAUCE_USR
        SAUCE_ACCESS_KEY = $SAUCE_PSW
    }

    options {
        disableConcurrentBuilds()
        skipDefaultCheckout true
        sauce('saucelabs')
//        sauceconnect(options: '', sauceConnectPath: '', verboseLogging: true)
        sauceconnect(options: '', sauceConnectPath: '', useGeneratedTunnelIdentifier: false ,verboseLogging: true)
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
                    sh 'printenv'
                    sh 'pwd'
                    sh 'ls'
                    getDockerfile()
                    docker.withRegistry('https://merrillcorp-dealworks.jfrog.io', 'mrll-artifactory') {
                        def dockerfile = './qa.Dockerfile'
                        tools_image = docker.build("dealworks-app/qa:latest", "--pull --rm -f ${dockerfile} .")
                        tools_image.inside() {
                            sh "cd /home/jenkins/app/ &&\
                                        ls"

//                            sh "bundle exec parallel_cucumber features/ -n $params.threads -o \"-t @$params.tag env=$params.env sys=$params.system jobExecutionPlatform=jenkins -f json --out cucumber.json --retry 1\" "
                            sh "bundle exec parallel_cucumber features/ -n $params.threads -o \"-t @$params.tag env=$params.env sys=$params.system jobExecutionPlatform=jenkins -f json -o cucumber.json -f html -o index.html -f json_pretty -o prettycucumber.json -f junit -o junit --retry 1\" "
//                            saucePublisher()
//                            sh "ruby ./parse_console_log.rb"
//                            sh "cat FailedTCDeatils.html"
                        }
                    }
                }
            }
            post {
                always {
                    junit 'junit/**'
                    step([$class: 'SauceOnDemandTestPublisher'])
                    script {
                        sh  'ls'
//                        step([$class: 'XUnitBuilder',
//                              thresholds: [
//                                      [$class: 'SkippedThreshold', failureThreshold: '0'],
//                                      // Allow for a significant number of failures
//                                      // Keeping this threshold so that overwhelming failures are guaranteed
//                                      //     to still fail the build
//                                      [$class: 'FailedThreshold', failureThreshold: '10']],
//                              tools: [[$class: 'JUnitType', pattern: 'junit/**']]])
//                        xunit testDataPublishers: tools: [JUnit(deleteOutputFiles: true, failIfNotNew: true, pattern: 'junit/*.xml', skipNoTestFiles: false, stopProcessingIfError: true)]
//                        xunit([JUnit(deleteOutputFiles: true, failIfNotNew: true, pattern: 'junit/*.xml', skipNoTestFiles: false, stopProcessingIfError: true)])
//                          step([$class: 'JUnitResultArchiver', testDataPublishers: [[$class: 'SauceOnDemandReportPublisher', jobVisibility: 'public']], testResults: 'junit/*.xml'])
//                        saucePublisher()
//                        cucumber 'cucumber.json'
                        sh 'cat cucumber.json'
//                        cucumber fileIncludePattern: 'cucumber.json', sortingMethod: 'ALPHABETICAL'
//                        cucumberSlackSend channel: 'alrt-ds1-marketing', json: 'cucumber.json'

//                        publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: '', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: 'QA'])
                        sh 'printenv'
                    }

                }
//                success {
//
//                    slackSend color: "good", message: "Job: <${env.RUN_DISPLAY_URL}/cucumber-html-reports|${env.JOB_NAME}> with build number ${env.BUILD_NUMBER} was successful"
//                }
//                unstable {
//                    slackSend color: "danger", message: "Job: <${env.RUN_DISPLAY_URL}/cucumber-html-reports|${env.JOB_NAME}> with build number ${env.BUILD_NUMBER} was unstable"
//                }
//                failure {
//                    slackSend color: "danger", message: "Job: <${env.RUN_DISPLAY_URL}/cucumber-html-reports|${env.JOB_NAME}> with build number ${env.BUILD_NUMBER} was failed"
//                }
            }
        }
    }
}
def getDockerfile() {
writeFile file: 'qa.Dockerfile', text: '''FROM ruby:alpine3.8
# Create a group and user
RUN echo "jenkins ALL=NOPASSWD: ALL" >> /etc/sudoers
RUN addgroup -g 1000 -S jenkins && \
    adduser -u 1000 -S jenkins -G jenkins
RUN apk add --no-cache make gcc g++ vim
# throw errors if Gemfile has been modified since Gemfile.lock
RUN bundle config --global frozen 1

WORKDIR /home/jenkins/app

COPY Gemfile /home/jenkins/app/
COPY Gemfile.lock /home/jenkins/app/
RUN bundle install --path /home/jenkins/bundle

COPY . .'''

}