String basePath = 'abc'
//String repo = 'sheehan/gradle-example'


//job("$basePath/gradle-example-build") {
//    scm {
//        github repo
//    }
//    triggers {
//        scm 'H/5 * * * *'
//    }
//    steps {
//        gradle 'assemble'
//    }
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

pipelineJob('QA-dealworks-app') {
    definition {
        cps {
            script(readFileFromWorkspace('src/jobs/src/project-a-workflow.groovy'))
            sandbox()
        }
    }
}
folder(basePath) {
    description 'This example shows basic folder/job creation.'
    views {
//        listView('overview') {
        jobs {
//                name('foo')
            regex(/QA-.*/)
        }
        columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
        }
//        }
//        buildPipelineView('pipeline') {
//            selectedJob('foo')
//        }
    }
}

//nestedView('QA') {
//    views {
////        listView('overview') {
//            jobs {
////                name('foo')
//                regex(/QA-.*/)
//            }
//            columns {
//                status()
//                weather()
//                name()
//                lastSuccess()
//                lastFailure()
//            }
////        }
////        buildPipelineView('pipeline') {
////            selectedJob('foo')
////        }
//    }
//}
