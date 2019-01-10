package src
pipeline {
    agent any

    environment {
        JFROG = credentials("mrll-artifactory")
        CF_DOCKER_PASSWORD = "$JFROG_PSW"
        PCF = credentials("svc-inf-jenkins")
    }
    options {
        skipDefaultCheckout()
        ansiColor('xterm')
    }
    parameters {
        string(name: 'SRC_PATH', defaultValue: 'mrll-npm/@mrll/dealworks-app/-/@mrll/dealworks-app-1.0.294.tgz')
        choice(name: 'Space', choices: ['devg', 'stageg', 'prod'], description: 'PCF spaces')
        choice(name: 'Manifest', choices: ['manifest-dev', 'manifest-stage', 'manifest-prod'], description: 'PCF manifest file')
    }
    post {
        cleanup {
            cleanWs() // clean the current workspace
            // clean the @tmp workspace
            dir("${env.WORKSPACE}@tmp") {
                cleanWs()
            }
            script {
                node('master') {
                    // clean the master @libs workspace
                    dir("${env.WORKSPACE}@libs") {
                        cleanWs()
                    }
                    // clean the master @script workspace
                    dir("${env.WORKSPACE}@script") {
                        cleanWs()
                    }
                }
            }
        }
    }
    stages {
        stage('Checkout') {

            //  agent any
            steps {
                git url: "https://github.com/wildbuffalo/getting-started-nodejs.git"
                script {
                    getrepo = sh(returnStdout: true, script: "basename -s .git `git config --get remote.origin.url`").trim()
//getrepo = sh "basename `git rev-parse --show-toplevel`"
                }

                echo "$getrepo"
                sh "curl -u $JFROG_USR:$JFROG_PSW -o ./archive.tgz -L https://merrillcorp.jfrog.io/merrillcorp/${params.SRC_PATH} --fail -O"
                sh 'mkdir archive'
                sh "tar -xvf archive.tgz -C archive"
                sh "ls"
                sh 'ls archive'
                stash includes: 'archive/package/*', name: 'app'
            }
        }
        stage('Push to PCF') {
            steps {
                script {
                    //  node {

                    docker.withRegistry('https://merrillcorp-dealworks.jfrog.io', 'mrll-artifactory') {
                        def dockerfile = './Docker/pcf.Dockerfile'
                        docker_pcf_src = docker.build("docker_pcf_src", "-f ${dockerfile} .")
                        docker_pcf_src.inside() {
                            dir("first-stash") {
                                unstash 'app'
                            }
                            //   unstash 'app'

                            sh 'ls'
                            sh 'pwd'
                            sh 'printenv'
                            sh 'cf -v'
//                            sh "cd ${pwd()}/archive/package/ &&\
//                                        ls &&\
//                                        cf login -a https://api.sys.us2.devg.foundry.mrll.com -u $PCF_USR -p $PCF_PSW -s ${params.Space} &&\
//                                        cf blue-green-deploy $getrepo -f ${pwd()}/pcf/${params.Manifest}.yml --delete-old-apps"
//                            if ( ${params.Space} == 'prod') {
//                                echo 'I only execute on the master branch'
//                            sh "cd ${pwd()}/archive/package/ &&\
//                                        ls &&\
//                                        cf login -a https://api.sys.us2.prodg.foundry.mrll.com -u $PCF_USR -p $PCF_PSW &&\
//                                        cf blue-green-deploy $getrepo -f ${pwd()}/pcf/${params.Manifest}.yml --delete-old-apps"
//                            } else {
//                                echo 'I execute elsewhere'
                            sh "cd ${pwd()}/archive/package/ &&\
                                        ls &&\
                                        cf login -a https://api.sys.us2.devg.foundry.mrll.com -u $PCF_USR -p $PCF_PSW -s ${params.Space} -o us2-datasiteone &&\
                                        cf blue-green-deploy $getrepo -f ${pwd()}/pcf/${params.Manifest}.yml --delete-old-apps"

                        }
                    }
                }
            }
        }
    }
}



