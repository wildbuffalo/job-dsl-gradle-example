String basePath = 'abc'
//String repo = 'sheehan/gradle-example'

folder(basePath) {
    description 'This example shows how to create a set of jobs for each github branch, each in its own folder.'
}

//job("$basePath/QA-avb") {
////    authenticationToken("mytoken")
//
//    triggers {
//        githubPush()
//    }
////    steps {
////        gradle 'assemble'
////    }
//}
//
//job("$basePath/gradle-example-deploy") {
//    parameters {
//        stringParam 'host'
//    }
//    steps {
//        shell 'scp war file; restart...'
//    }
//}

pipelineJob("$basePath/QA-dealworks-app") {
    definition {
//        cps {
//            script(readFileFromWorkspace('src/jobs/src/project-a-workflow.groovy'))
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
//                    scriptPath('src/jobs/src/project-a-workflow.groovy')
                }
//                github('wildbuffalo/getting-stated-nodejs')
            }
            scriptPath('src/jobs/src/project-a-workflow.groovy')
        }
    }
    triggers {
        githubPush()
    }
}

pipelineJob("$basePath/deployment") {
    parameters {
        stringParam(name: 'SRC_PATH', defaultValue: 'mrll-npm/@mrll/dealworks-app/-/@mrll/dealworks-app-1.0.294.tgz')
        activeChoiceParam('CHOICE-1') {
            description('Allows user choose from multiple choices')
            filterable()
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('["choice1", "choice2"]')
                fallbackScript('"fallback choice"')
            }
        }
//        choice(name: 'Space', choices: ['devg', 'stageg', 'prod'], description: 'PCF spaces')
//        choice(name: 'Manifest', choices: ['manifest-dev', 'manifest-stage', 'manifest-prod'], description: 'PCF manifest file')

    }
    definition {
//        cps {
//            script(readFileFromWorkspace('src/jobs/src/project-a-workflow.groovy'))
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
//                    scriptPath('src/jobs/src/project-a-workflow.groovy')
                }
//                github('wildbuffalo/getting-stated-nodejs')
            }
            scriptPath('src/jobs/src/deploy.groovy')
        }
    }
    triggers {
        githubPush()
    }
}
//pipelineJob('build-dealworks-app') {
//    definition {
////        cps {
////            script(readFileFromWorkspace('src/jobs/src/dealworks-app.groovy'))
////            sandbox()
////        }
//        cpsScm {
//            scm {
//                git {
//                    remote {
//                        branch('develop')
//                        url('https://github.com/wildbuffalo/dealworks-app.git')
//                        credentials('github-user')
//                    }
////                    extensions {
////                        cleanAfterCheckout()
////                        relativeTargetDirectory('repo1')
////                    }
//                    scriptPath('src/jobs/src/dealworks-app.groovy')
//                }
//            }
//        }
//    }
//    triggers {
//        githubPush()
//    }
//}

listView('QA') {
    description('All unstable jobs for project A')
    filterBuildQueue()
    filterExecutors()
    jobs {
//        name('release-projectA')
        regex(/QA-.*/)
    }
//    jobFilters {
//        status {
//            status(Status.UNSTABLE)
//        }
//    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}
buildMonitorView('project-A') {
    description('All jobs for project A')
    jobs {
//        name('release-projectA')
        regex(/.*/)
    }
    statusFilter(StatusFilter.ENABLED)
    filterBuildQueue()
    recurse()
}