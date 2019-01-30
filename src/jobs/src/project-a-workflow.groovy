@Library('ds1-marketing-jenkins-library@master') _
pipeline {
    agent any
    options {
        disableConcurrentBuilds()
        skipDefaultCheckout true
//        sauce('saucelabs')
//        sauceconnect(options: '', sauceConnectPath: '', useGeneratedTunnelIdentifier: true, useLatestSauceConnect: true, verboseLogging: true)
    }

//    parameters {
//        string(name: 'REPO', description: 'repository name')
//        choice(name: 'STAGE', choices: ['develop', 'stage', 'master'], description: 'The branch is respect to the environment accordingly dev to dev env, stage to stage env, master to prod env')
//        string(name: 'VERSION', defaultValue: 'latest', description: 'pick your version from the artifactory')
//    }
//    environment {
//        JFROG = credentials("mrll-artifactory")
//        CF_DOCKER_PASSWORD = "$JFROG_PSW"
//        PCF = credentials("svc-inf-jenkins")
//        REPO = "$params.REPO"
//        STAGE = "$params.STAGE"
//        VERSION = "$params.VERSION"
//
//    }
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
            //  agent any
            steps {
//                git url: "https://github.com/MerrillCorporation/dealworks-app.git"
                checkout([$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '6331db84-0ca0-4396-a946-afa1e804158f', url: 'https://github.com/MerrillCorporation/dealworks-ui-tests.git']]])
                script {
                    env.gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    env.getRepo = sh(returnStdout: true, script: "basename -s .git `git config --get remote.origin.url`").trim()
                    sh 'printenv'

                }
            }
        }
        stage('Build') {
//            agent {
//                dockerfile {
//
//                    additionalBuildArgs '-t dealworks-app/qa:latest'
//                    additionalBuildArgs '--pull'
//                    additionalBuildArgs '--rm'
//                    filename 'qa.Dockerfile'
//                    registryCredentialsId 'mrll-artifactory'
//                    registryUrl 'https://merrillcorp-dealworks.jfrog.io'
//                }
//            }
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

//                            step([$class: 'DockerBuilderPublisher', cleanImages: true, cleanupWithJenkinsJobDelete: true, cloud: '', dockerFileDirectory: 'qa.Dockerfile', fromRegistry: [], pushCredentialsId: 'mrll-artifactory', pushOnSuccess: true, tagsString: 'dealworks-app/qa:latest'])
//                            sauce('saucelabs') {
//                                sauceconnect(options: '', sauceConnectPath: '', useGeneratedTunnelIdentifier: true, useLatestSauceConnect: true, verboseLogging: true) {
//                                    sh './node_modules/.bin/nightwatch -e chrome --test tests/guineaPig.js || true'
//
//                                    sh "bundle exec parallel_cucumber features -n $params.threads -f json_pretty --out cucumber.json -o \"-t @buyerTableAddBuyerStatus env=$params.env sys=$params.system jobExecutionPlatform=jenkins -f json --retry 1\"  "
                            sh "cd /home/jenkins/app/ &&\
                                        ls"
                            sh "cd /home/jenkins/app/ &&" +
                                    "bundle exec parallel_cucumber features/ -n $params.threads -o \"-t @buyerTableAddBuyerStatus env=$params.env sys=$params.system jobExecutionPlatform=jenkins -f json --out cucumber.json --retry 1\"  "
// | tee test-output.log
// @dealworksProjectFromTheGLOP  fail @buyerTableAddBuyerStatus @$params.tag
                                    sh "ls"
                            sh "/home/jenkins/app/ && ls"
//                            sh 'cat cucumber.json'
                            cucumber fileIncludePattern: '/home/jenkins/app/cucumber.json', sortingMethod: 'ALPHABETICAL'
//                                }
//                            }
                        }
                    }
                }
            }
//            post {
//                always {
//                    step([$class: 'XUnitBuilder',
//                          thresholds: [
//                                  [$class: 'SkippedThreshold', failureThreshold: '0'],
//                                  // Allow for a significant number of failures
//                                  // Keeping this threshold so that overwhelming failures are guaranteed
//                                  //     to still fail the build
//                                  [$class: 'FailedThreshold', failureThreshold: '10']],
//                          tools: [[$class: 'JUnitType', pattern: 'reports/**']]])
//
//                    saucePublisher()
//                }
//            }
        }
    }
}
def getDockerfile() {
    writeFile file: 'qa.Dockerfile', text: '''FROM ubuntu:xenial
ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000
ENV JENKINS_HOME=/home/jenkins

RUN addgroup --gid ${gid} ${group} \
    && adduser --home ${JENKINS_HOME} --uid ${uid} --gid ${gid} --shell /bin/bash --disabled-password --gecos "" ${user}


RUN apt-get update && apt-get install -y ruby && \
    apt-get clean
RUN gem install bundle
RUN bundle config --global frozen 1
USER jenkins
WORKDIR /home/jenkins/app

COPY Gemfile /home/jenkins/app/
COPY Gemfile.lock /home/jenkins/app/
RUN bundle install 

COPY . .'''

}
//writeFile file: 'qa.Dockerfile', text: '''FROM ruby:alpine3.8
//# Create a group and user
//RUN echo "jenkins ALL=NOPASSWD: ALL" >> /etc/sudoers
//RUN addgroup -g 1000 -S jenkins && \
//    adduser -u 1000 -S jenkins -G jenkins
//RUN apk add --no-cache make gcc g++ vim
//# throw errors if Gemfile has been modified since Gemfile.lock
//RUN bundle config --global frozen 1
//
//WORKDIR /home/jenkins/app
//
//COPY Gemfile /home/jenkins/app/
//COPY Gemfile.lock /home/jenkins/app/
//RUN bundle install --path /home/jenkins/bundle
//
//COPY . .'''