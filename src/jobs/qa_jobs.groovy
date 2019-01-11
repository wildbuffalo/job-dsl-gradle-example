String basePath = 'QA'
//String repo = 'sheehan/gradle-example'

folder(basePath) {
    description 'This example shows how to create a set of jobs for each github branch, each in its own folder.'
}

job("$basePath/QA-acgg") {
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
    steps {
        shell('ls')
        shell('pwd')
    }

//    environmentVariables {
//        scriptFile('Jenkinsfile')
//    }
    triggers {
        githubPush()
    }
//    steps {
//        gradle('clean build')
//    }
}

pipelineJob("$basePath/QA-dealworks-app") {
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