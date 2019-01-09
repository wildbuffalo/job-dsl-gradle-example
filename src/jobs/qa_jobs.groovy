String basePath = 'QA'
//String repo = 'sheehan/gradle-example'

folder(basePath) {
    description 'This example shows how to create a set of jobs for each github branch, each in its own folder.'
}

job("$basePath/QA-acgg") {
    logRotator(-1, 10)
    scm {
        git {
            remote {
                branches('origin/master','origin/abvv')
                github('wildbuffalo/getting-stated-nodejs')
                credentials('github-user')
            }
        }
    }
    triggers {
        githubPush()
    }
//    steps {
//        gradle('clean build')
//    }
}