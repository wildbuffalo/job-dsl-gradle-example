String basePath = 'QA'
//String repo = 'sheehan/gradle-example'

folder(basePath) {
    description 'This example shows how to create a set of jobs for each github branch, each in its own folder.'
}

job('QA-acgg') {
    logRotator(-1, 10)
    scm {
        github('wildbuffalo/getting-stated-nodejs','master')
    }
    triggers {
        githubPush()
    }
//    steps {
//        gradle('clean build')
//    }
}