String basePath = 'JOBS'
//String repo = 'sheehan/gradle-example'

folder(basePath) {
    description 'This example shows how to create a set of jobs for each github branch, each in its own folder.'
}

pipelineJob('build-dealworks-app') {
    definition {
//        cps {
//            script(readFileFromWorkspace('src/jobs/src/dealworks-app.groovy'))
//            sandbox()
//        }
        cpsScm {
            scm {
                git {
                    branch('master')
                    remote {
                        github('wildbuffalo/job-dsl-gradle-example')
                        credentials('github-user')
                    }
//                    extensions {
//                        cleanAfterCheckout()
//                        relativeTargetDirectory('repo1')
//                    }
                    scriptPath('src/jobs/src/dealworks-app.groovy')
                }
            }
        }
    }
    triggers {
        githubPush()
    }
}

//listView('QA') {
//    description('All unstable jobs for project A')
//    filterBuildQueue()
//    filterExecutors()
//    jobs {
////        name('release-projectA')
//        regex(/QA-.*/)
//    }
////    jobFilters {
////        status {
////            status(Status.UNSTABLE)
////        }
////    }
//    columns {
//        status()
//        weather()
//        name()
//        lastSuccess()
//        lastFailure()
//        lastDuration()
//        buildButton()
//    }
//}

//buildMonitorView('project-A') {
//    description('All jobs for project A')
//    jobs {
////        name('release-projectA')
//        regex(/.*/)
//    }
//    statusFilter(StatusFilter.ENABLED)
//    filterBuildQueue()
//    recurse()
//}