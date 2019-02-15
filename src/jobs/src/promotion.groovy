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
               scmPromote()
            }
        }
        stage('Build') {
            steps {
                sh 'ls'
            }
        }
    }
}
def scmPromote(){
    [
//            [repo: 'dwgraphql-app', email: 'me@example.com'],
//            [repo: 'dealworks-app', email: 'you@example.com'],
            [repo: 'getting-started-nodejs'],
    ].each { Map config ->
        checkout([$class: 'GitSCM',
                  branches: [[name: '*/develop']],
                  doGenerateSubmoduleConfigurations: false,
                  extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${config.repo}"]],
                  submoduleCfg: [],
//                  userRemoteConfigs: [[credentialsId: '6331db84-0ca0-4396-a946-afa1e804158f', url: "https://github.com/MerrillCorporation/${config.repo}.git"]]
                  userRemoteConfigs: [[credentialsId: '6331db84-0ca0-4396-a946-afa1e804158f', url: "https://github.com/wildbuffalo/${config.repo}.git"]]

        ])
        withCredentials([usernamePassword(credentialsId: '6331db84-0ca0-4396-a946-afa1e804158f', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
//            sh("git tag -a some_tag -m 'Jenkins'")
//            sh('git push https://${GIT_USERNAME}:${GIT_PASSWORD}@<REPO> --tags')
            dir("${config.repo}"){
                sh 'git status'
                sh 'commit -m "promote to stage"'
                sh "git push -f https://${GIT_USERNAME}:${GIT_PASSWORD}@${config.repo} master"
            }
        }

//        sshagent (credentials: ['6331db84-0ca0-4396-a946-afa1e804158f']) {
//            dir("${config.repo}"){
//                sh 'git status'
//                sh 'git push -f origin/develop:master'
//            }
//        }


//    job("$basePath/ci-${config.repo}") {
//        description "deployment for ${config.repo}"
//
//        logRotator {
//            numToKeep 5
//        }
//        triggers {
//            scm 'H/5 * * * *'
//        }
//        steps {
//            gradle 'assemble'
//        }
//        publishers {
//            if (config.email) {
//                extendedEmail {
//                    recipientList config.email
//                }
//            }
//        }
//    }
    }


}
