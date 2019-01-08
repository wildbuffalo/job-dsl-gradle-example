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
nestedView(basePath) {
    description 'This example shows basic folder/job creation.'
    views {
        listView('overview') {
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
        }
//        buildPipelineView('pipeline') {
//            selectedJob('foo')
//        }
        listView("Build Jobs"){
            jobs{

            }
            columns{
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }
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
//sectionedView('project-summary') {
//    filterBuildQueue()
//    filterExecutors()
//    sections {
//        listView {
//            name('Project A')
//            jobs {
//                regex(/project-A-.*/)
//            }
//            columns {
//                status()
//                weather()
//                name()
//                lastSuccess()
//                lastFailure()
//            }
//        }
//        listView {
//            name('Project B')
//            jobs {
//                regex(/project-B-.*/)
//            }
//            jobFilters {
//                regex {
//                    matchValue(RegexMatchValue.DESCRIPTION)
//                    regex(/.*-project-B-.*/)
//                }
//            }
//            columns {
//                status()
//                weather()
//                name()
//                lastSuccess()
//                lastFailure()
//            }
//        }
//    }
//}
listView('project-A') {
    description('All unstable jobs for project A')
    filterBuildQueue()
    filterExecutors()
    jobs {
        name('release-projectA')
        regex(/project-A-.+/)
    }
    jobFilters {
        status {
            status(Status.UNSTABLE)
        }
    }
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

dashboardView('example') {
    jobs {
        regex(/acme-.*/)
    }
    columns {
        status()
        weather()
        buildButton()
    }
    topPortlets {
        jenkinsJobsList {
            displayName('acme jobs')
        }
    }
    leftPortlets {
        testStatisticsChart()
    }
    rightPortlets {
        testTrendChart()
    }
    bottomPortlets {
        iframe {
            effectiveUrl('http://example.com')
        }
        testStatisticsGrid()
        buildStatistics()
    }
}
categorizedJobsView('example') {
    jobs {
        regex(/configuration_.*/)
    }
    categorizationCriteria {
        regexGroupingRule(/^configuration_([^_]+).*$/)
    }
    columns {
        status()
        categorizedJob()
        buildButton()
    }
}
buildPipelineView('project-A') {
    filterBuildQueue()
    filterExecutors()
    title('Project A CI Pipeline')
    displayedBuilds(5)
    selectedJob('project-A-compile')
    alwaysAllowManualTrigger()
    showPipelineParameters()
    refreshFrequency(60)
}
buildMonitorView('project-A') {
    description('All jobs for project A')
    jobs {
        name('release-projectA')
        regex(/project-A-.+/)
    }
}