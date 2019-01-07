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

def pipelines = [
    [name: 'foo', startJob: 'foo_start'],
    [name: 'bar', startJob: 'bar_start'],
]

nestedView('Pipelines') {
    views {
        pipelines.each { def pipeline ->
            // call delegate.buildPipelineView to create a nested view
            delegate.buildPipelineView("${pipeline.name} Pipeline") {
                selectedJob(pipeline.startJob)
            }
        }
    }
}

pipelineJob('foo') {
    definition {
        cps {
            script(readFileFromWorkspace('src/jobs/project-a-workflow.groovy'))
            sandbox()
        }
    }
}
