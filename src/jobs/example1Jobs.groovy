String basePath = 'example1'
String repo = 'sheehan/gradle-example'

folder(basePath) {
    description 'This example shows basic folder/job creation.'
}

job("$basePath/gradle-example-build") {
    scm {
        github repo
    }
    triggers {
        scm 'H/5 * * * *'
    }
    steps {
        gradle 'assemble'
    }
}

job("$basePath/gradle-example-deploy") {
    parameters {
        stringParam 'host'
    }
    steps {
        shell 'scp war file; restart...'
    }
}

nestedView('project-a') {
    views {
        listView('overview') {
            jobs {
                regex(/project-A-.*/)
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }
        buildPipelineView('pipeline') {
            selectedJob('foo')
        }
    }
}
pipelineJob('foo') {
    definition {
        cps {
            script(readFileFromWorkspace('src/jobs/src/project-a-workflow.groovy'))
            sandbox()
        }
    }
}
