String basePath = 'JOBS'
//String repo = 'sheehan/gradle-example'

folder(basePath) {
    description 'This example shows how to create a set of jobs for each github branch, each in its own folder.'
}
multibranchPipelineJob("$basePath/build-dealworks-app") {
    branchSources {
        github {
            // Sets the GitHub API URI.
//        apiUri(String apiUri)
// Build origin branches.
            buildOriginBranch()
// Build origin branches also filed as PRs.
            buildOriginBranchWithPR()
// Build origin PRs (unmerged head).
//        buildOriginPRHead(boolean buildOriginPRHead = true)
// Build origin PRs (merged with base branch).
            buildOriginPRMerge()
// Sets checkout credentials for authentication with GitHub.
            checkoutCredentialsId("github-user")
// Sets a pattern for branches to exclude.
//        excludes(String excludes)

// Sets a pattern for branches to include.
            includes("develop,master,stage")
// Sets the name of the GitHub Organization or GitHub User Account.
            repoOwner("MerrillCorporation")
// Sets the name of the GitHub repository.
            repository("dealworks-app")
// Sets scan credentials for authentication with GitHub.
            scanCredentialsId("github-user")
        }
    }
    orphanedItemStrategy {
        // Trims dead items by the number of days or the number of items.
        discardOldItems {
// Sets the number of days to keep old items.
            daysToKeep(1)
// Sets the number of old items to keep.
            numToKeep(5)
        }
    }

}
//pipelineJob("$basePath/build-dealworks-app") {
//    definition {
////        cps {
////            script(readFileFromWorkspace('src/jobs/src/dealworks-app.groovy'))
////            sandbox()
////        }
//        cpsScm {
//            scm {
//                git {
//                    branch('master')
//                    remote {
//                        github('wildbuffalo/job-dsl-gradle-example')
//                        credentials('github-user')
//                    }
////                    extensions {
////                        cleanAfterCheckout()
////                        relativeTargetDirectory('repo1')
////                    }
//                    scriptPath('src/jobs/src/dealworks-app.groovy')
//                }
//            }
//        }
//    }
//    triggers {
//        githubPush()
//    }
//}

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