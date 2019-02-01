String basePath = 'QA'
//String repo = 'sheehan/gradle-example'

folder(basePath) {
    description 'QA jobs'
}

job("$basePath/QA-acgg") {
    concurrentBuild(true)
    logRotator(-1, 1)
    scm {
        git {
            branch('master')
            remote {

                url("https://github.com/wildbuffalo/getting-started-nodejs.git")
                credentials('github-user')
            }

            extensions {
                // Cleans up the workspace after every checkout by deleting all untracked files and directories, including those which are specified in .gitignore.
                cleanAfterCheckout()
                // Clean up the workspace before every checkout by deleting all untracked files and directories, including those which are specified in .gitignore.
                cleanBeforeCheckout()
            }
        }
    }
//    environmentVariables {
//        scriptFile('Jenkinsfile')
//    }
//    triggers {
//        githubPush()
//    }
//    steps {
//        gradle('clean build')
//    }
}
pipelineJob("$basePath/QA-dealworks-app-STAGE") {
    definition {
        cps {
// Enables the Groovy sandbox for the script.
            sandbox()
// Sets the workflow DSL script.
            script('src/jobs/src/project-a-workflow.groovy')
        }
//            scriptPath('src/jobs/src/project-a-workflow.groovy')
        }

    triggers {
        githubPush()
    }
    parameters {
        stringParam("env", "stage", "Environment Variable")
        stringParam("tag", "smoke","")
        stringParam("threads", "35","")
        stringParam("system", "windows","")
    }
}

pipelineJob("$basePath/QA-dealworks-app-DEV") {
    definition {
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
    parameters {
        stringParam("env", "dev", "Environment Variable")
        stringParam("tag", "smoke","")
        stringParam("threads", "35","")
        stringParam("system", "windows","")
    }
}