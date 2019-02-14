import com.dslexample.util.StepsUtil

String basePath = 'UTILS'

folder(basePath) {
    description 'This example shows how to pull out common components into static methods.'
}


pipelineJob("$basePath/promotion-Stage") {
    parameters {
        stringParam('SRC_PATH', 'mrll-npm/@mrll/dealworks-app/-/@mrll/dealworks-app-1.0.294.tgz')
//        stringParam('getrepo', 'dealworks-app')
//        choiceParam('Space', ['devg', 'stageg', 'prod'], 'PCF spaces')
//        choiceParam('Manifest', ['manifest-dev', 'manifest-stage', 'manifest-prod'], 'PCF manifest file')

    }
    definition {
        cps {
            script(readFileFromWorkspace('src/jobs/src/promotion.groovy'))
            sandbox()
        }
    }
//    triggers {
//        githubPush()
//    }
}

pipelineJob("$basePath/deploy") {
    parameters {
        choiceParam('BRANCH_NAME', ['develop', 'stage', 'master'], 'develop = dev, stage = stage, master = prod')
        stringParam('REPO', 'dealworks-app')
        stringParam('VERSION', '')
//        choiceParam('Space', ['devg', 'stageg', 'prod'], 'PCF spaces')
//        choiceParam('Manifest', ['manifest-dev', 'manifest-stage', 'manifest-prod'], 'PCF manifest file')

    }
    definition {
        cps {
            script(readFileFromWorkspace('src/jobs/src/node_FE_deployment.groovy'))
            sandbox()
        }
    }
}
