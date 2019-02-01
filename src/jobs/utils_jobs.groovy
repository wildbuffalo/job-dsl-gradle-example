import com.dslexample.util.StepsUtil

String basePath = 'UTILS'

folder(basePath) {
    description 'This example shows how to pull out common components into static methods.'
}


pipelineJob("$basePath/deployment") {
    parameters {
        stringParam('SRC_PATH', 'mrll-npm/@mrll/dealworks-app/-/@mrll/dealworks-app-1.0.294.tgz')
        stringParam('getrepo', 'dealworks-app')
        choiceParam('Space', ['devg', 'stageg', 'prod'], 'PCF spaces')
        choiceParam('Manifest', ['manifest-dev', 'manifest-stage', 'manifest-prod'], 'PCF manifest file')

    }
    definition {
//        cps {
//            script(readFileFromWorkspace('src/jobs/src/qa-dealworks.groovy'))
//            sandbox()
//        }
        cpsScm {
            scm {
                git {
                    remote {
                        branch('master')
                        github('wildbuffalo/job-dsl-gradle-example')
                        credentials('github-user')
                    }
                    extensions {
                        cleanAfterCheckout()
//                        relativeTargetDirectory('repo1')
                    }
//                    scriptPath('src/jobs/src/qa-dealworks.groovy')
                }
//                github('wildbuffalo/getting-stated-nodejs')
            }
            scriptPath('src/jobs/src/deploy.groovy')
        }
    }
//    triggers {
//        githubPush()
//    }
}

