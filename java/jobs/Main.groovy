APP_ARTIFACTS = ['target/hello.jar', 'start', 'stop']

deliveryPipelineView('Pipeline') {
    enableManualTriggers(true)
    showAggregatedPipeline(true)
    pipelines() {
        component('Hello', 'build')
    }
}

job('build') {
    deliveryPipelineConfiguration("Build", "Build")
    wrappers {
        deliveryPipelineVersion('build \$BUILD_NUMBER', true)
    }
    scm {
        git {
            remote {
                url('https://github.com/noidi/hello-java.git')
            }
            branch('master')
            clean()
        }
    }
    triggers {
        scm('*/15 * * * *')
    }
    steps {
        shell('echo $BUILD_NUMBER > src/main/resources/build.txt')
        shell('mvn package')
        shell('mvn verify')
    }
    publishers {
        archiveJunit('target/failsafe-reports/*.xml')
        archiveArtifacts {
            APP_ARTIFACTS.collect { pattern(it) }
            onlyIfSuccessful()
        }
        downstream('deploy-dev', 'SUCCESS')
    }
}

job('deploy-dev') {
    deliveryPipelineConfiguration("Dev", "Deploy")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        copyArtifacts('build') {
            buildSelector() {
                upstreamBuild(fallback=true)
            }
            includePatterns(*APP_ARTIFACTS)
            flatten()
        }
        // Don't fail the build even if rsync fails (it's probably because some
        // files are missing the o+r permission).
        shell('rsync -av /project/* . || true')
        shell('ansible-playbook -l dev deploy.yml')
    }
}
