@Library('ds1-marketing-jenkins-library@master') _
pipeline {
    agent any
    options {
        sauce('saucelabs')
        sauceconnect(options: '', sauceConnectPath: '', useGeneratedTunnelIdentifier: true, useLatestSauceConnect: true, verboseLogging: true)
        disableConcurrentBuilds()
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '1', numToKeepStr: '5')
        skipDefaultCheckout true
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
//                                    junit 'reports/**'
//                                    step([$class: 'SauceOnDemandTestPublisher'])
                                    sh "bundle exec parallel_cucumber features -n $params.threads -o \"-t @dealworksProjectFromTheGLOPenv=$params.env sys=$params.system jobExecutionPlatform=jenkins --retry 1\" "
//                                    | tee test-output.log
// @$params.tag

//                                }
//                            }
                        }
                    }
                }
            }
            post{
                script {
                    saucePublisher()
                }

            }
        }
    }
}
def getDockerfile() {
    writeFile file: 'qa.Dockerfile', text: '''FROM ruby:alpine3.8
RUN apk add --no-cache make gcc g++ vim
# throw errors if Gemfile has been modified since Gemfile.lock
RUN bundle config --global frozen 1
WORKDIR /usr/app

COPY Gemfile /usr/app/
COPY Gemfile.lock /usr/app/
RUN bundle install

COPY . .'''

}