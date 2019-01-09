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
//        groovyScriptFile('generateReports.groovy')
        xShell {
            commandLine('ls')
        }
        xShell {
            commandLine('pwd')
            executableInWorkspaceDir()
        }
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

//job("$basePath/example-5") {
//    scm {
//        git {
//            remote {
//                github('wildbuffalo/getting-stated-nodejs')
//                credentials('github-user')
//            }
//            branches('master','abvv')
//            extensions {
//                choosingStrategy {
//                    alternative()
//                }
//            }
//        }
//    }
//}