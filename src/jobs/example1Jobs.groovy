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
    recurse()
}