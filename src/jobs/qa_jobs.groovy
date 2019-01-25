import org.omg.CORBA.Environment

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
    parameters {
        stringParam("env", "dev", "Environment Variable")
        stringParam("tag", "smoke","")
        stringParam("threads", "35","")
        stringParam("system", "windows","")
//// Defines a parameter to select a label used to identify/restrict the node where this job should run on.
//        labelParam(String parameterName) {}
//// Defines a parameter that allows to select a Subversion tag from which to create the working copy for the project.
//        listTagsParam(String parameterName, String scmUrl, String tagFilterRegex, boolean sortNewestFirst = false, boolean sortZtoA = false, String maxTagsToDisplay = 'all', String defaultValue = null, String description = null)
//// Defines a parameter that allows to select a Subversion tag from which to create the working copy for the project.
//        listTagsParam(String parameterName, String scmUrl) {}
//// Defines a parameter that allows to choose which matrix combinations to run.
//        matrixCombinationsParam(String parameterName, String defaultValue = null, String description = null)
//// Defines a parameter to select a list of nodes where the job could potentially be executed on.
//        nodeParam(String parameterName) {}
//// Defines a parameter that allows to take in a user's password.
//        nonStoredPasswordParam(String parameterName, String description = null)
//// Defines a run parameter, where users can pick a single run of a certain project.
//        runParam(String parameterName, String jobToRun, String description = null, String filter = null)
//// Defines a simple text parameter, where users can enter a string value.
//        stringParam(String parameterName, String defaultValue = null, String description = null)
//// Defines a simple text parameter, where users can enter a multi-line string value.
//        textParam(String parameterName, String defaultValue = null, String description = null)
    }
}