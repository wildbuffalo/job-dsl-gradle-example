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
    }
    post {
        success {
            slackSend color: "good", message: "Job: <${env.BUILD_URL}|${env.JOB_NAME}> with build number ${env.BUILD_NUMBER} was successful"
        }
        unstable {
            slackSend color: "danger", message: "Job: <${env.BUILD_URL}|${env.JOB_NAME}> with build number ${env.BUILD_NUMBER} was unstable"
        }
        failure {
            slackSend color: "danger", message: "Job: <${env.BUILD_URL}|${env.JOB_NAME}> with build number ${env.BUILD_NUMBER} was failed"
        }
        cleanup {
            // clean the current workspace
            cleanWs()
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
        stage('Get Image') {
            steps {
                getDockerfile()
            }
        }
//        stage('Archive to Artifactory') {
//            steps {
//                script {
//                    docker.withRegistry('https://merrillcorp-dealworks.jfrog.io', 'mrll-artifactory') {
//                        docker_image.push('latest')
//                        docker_image.push()
//                    }
//                }
//            }
//        }
        stage('Push to PCF') {
            steps {
                runDockerfile()
                script {

                }
            }
        }
    }
}

def getDockerfile() {
    writeFile file: 'deploy.Dockerfile', text: "FROM merrillcorp-dealworks.jfrog.io/$REPO/$BRANCH_NAME:$VERSION as source\n" +
            "WORKDIR /usr/src/app\n" +
            "RUN npm install\n" +
            "RUN npm run build\n" +
            "FROM merrillcorp-dealworks.jfrog.io/tools:latest\n" +
//            "COPY --from=source /usr/src/app /home/jenkins/src/"
            "COPY --from=source /usr/src/app/dist /home/jenkins/src/\n" +
            "COPY --from=source /usr/src/app/devops /home/jenkins/src/devops/"
}
def runDockerfile() {
    docker.withRegistry('https://merrillcorp-dealworks.jfrog.io', 'mrll-artifactory') {
        def dockerfile = "./deploy.Dockerfile"
        docker_pcf_src = docker.build("docker_pcf_src", "--pull --rm -f ${dockerfile} .")
        docker_pcf_src.inside() {
            if (BRANCH_NAME == 'master') {
                sh "cd /usr/src/app &&\
                    ls &&\
                    pwd &&\
                    cf login -a https://api.sys.us2.prodg.foundry.mrll.com -u $PCF_USR -p $PCF_PSW -s prodg -o us2-datasiteone &&\
                    cf zero-downtime-push $REPO-prod -f ./devops/manifest-prod.yml"
            } else if (BRANCH_NAME == 'stage') {
                sh "cd /usr/src/app &&\
                    ls &&\
                    pwd &&\
                    cf login -a https://api.sys.us2.devg.foundry.mrll.com -u $PCF_USR -p $PCF_PSW -s stageg -o us2-datasiteone &&\
                    cf zero-downtime-push $REPO-stage -f ./devops/manifest-stage.yml"
            } else {
                sh "cd /usr/src/app &&\
                    ls &&\
                    pwd &&\
                    cf login -a https://api.sys.us2.devg.foundry.mrll.com -u $PCF_USR -p $PCF_PSW -s devg -o us2-datasiteone &&\
                    cf zero-downtime-push $REPO -f ./devops/manifest-dev.yml"
            }
        }
    }
}
