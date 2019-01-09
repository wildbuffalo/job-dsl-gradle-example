String basePath = 'abc'
//String repo = 'sheehan/gradle-example'

job("QA-avb") {
    authenticationToken("mytoken")
    multiscm {
        git {
            remote {
                github('wildbuffalo/job-dsl-gradle-example')
            }
            extensions {
                relativeTargetDirectory('jenkins')
            }
        }
        git {
            remote {
                github('wildbuffalo/getting-stated-nodejs')
            }
            extensions {
                relativeTargetDirectory('apps')
            }
        }
    }
    triggers {
        githubPush()
    }
//    steps {
//        gradle 'assemble'
//    }
}
//
//job("$basePath/gradle-example-deploy") {
//    parameters {
//        stringParam 'host'
//    }
//    steps {
//        shell 'scp war file; restart...'
//    }
//}

pipelineJob('QA-dealworks-app') {
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
                        url('https://github.com/wildbuffalo/job-dsl-gradle-example.git')
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