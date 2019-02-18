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
            when {
                anyOf {
                    changeset "*"
                }
            }
        }
        stage('Build') {
            when {
                anyOf {
                    changeset "*"
                }
            }
            steps {
                sh 'ls'
            }
        }

    }
}
def scmPromote(){
    [
//            [repo: 'dwgraphql-app', email: 'me@example.com'],
            [repo: 'dealworks-app', email: 'you@example.com'],
//            [repo: 'getting-started-nodejs'],
    ].each { Map config ->
        dir("${config.repo}"){
        withCredentials([usernamePassword(credentialsId: '6331db84-0ca0-4396-a946-afa1e804158f', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
            git branch: 'develop', credentialsId: '6331db84-0ca0-4396-a946-afa1e804158f', url: "https://github.com/wildbuffalo/${config.repo}.git"
                sh "git remote set-url origin https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/${GIT_USERNAME}/${config.repo}.git"
                sh 'git status'
                sh 'git branch'
                sh 'git remote -v'
                sh "git push -f origin develop:master"
            }
        }
    }
}
