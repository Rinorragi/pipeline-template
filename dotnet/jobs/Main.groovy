def applicationName = 'Grade-Calculator'
def gitRepository = 'https://github.com/Rinorragi/Grade-Calculator.git'
def solutionFile = 'GradeCalculator.sln'

deliveryPipelineView('Pipeline') {
    enableManualTriggers(true)
    showAggregatedPipeline(true)
    pipelines() {
        component(applicationName, applicationName + ' Build')
    }
}

job(applicationName + ' Build') {
    deliveryPipelineConfiguration("Build", "Build")
    wrappers {
        deliveryPipelineVersion('build \$BUILD_NUMBER', true)
    }
    scm {
        git {
            remote {
                url(gitRepository)
            }
            branch('master')
            clean()
        }
    }
    triggers {
        scm('*/15 * * * *')
    }
    steps {
        batchFile('Nuget.exe restore ' + solutionFile + ' -ConfigFile .nuget\\NuGet.Config -NoCache')
	}
    publishers {
		downstream(applicationName + ' Unit-Tests', 'SUCCESS')
    }
}

job(applicationName + ' Unit-Tests') {
    deliveryPipelineConfiguration("Build", "Unit-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        
	}
    publishers {
		downstream(applicationName + ' Dev-Deploy', 'SUCCESS')
		buildPipelineTrigger(applicationName + ' Sonar-Tests') {
		}
    }
}

job(applicationName + ' Sonar-Tests') {
    deliveryPipelineConfiguration("Build", "Sonar-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        
	}
    publishers {
    }
}

job(applicationName + ' Dev-Deploy') {
    deliveryPipelineConfiguration("Dev", "Deploy")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        
	}
    publishers {
		buildPipelineTrigger(applicationName + ' Dev-Integration-Tests') {
		}
    }
}

job(applicationName + ' Dev-Integration-Tests') {
    deliveryPipelineConfiguration("Dev", "Integration-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
        buildPipelineTrigger(applicationName + ' Staging-Deploy') {
        }
    }
}

job(applicationName + ' Staging-Deploy') {
    deliveryPipelineConfiguration("Staging", "Deploy")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        
	}
    publishers {
		buildPipelineTrigger(applicationName + ' Staging-Integration-Tests') {
		}
		buildPipelineTrigger(applicationName + ' Staging-Performance-Tests') {
		}
		buildPipelineTrigger(applicationName + ' Staging-Security-Tests') {
		}
    }
}

job(applicationName + ' Staging-Performance-Tests') {
    deliveryPipelineConfiguration("Staging", "Performance-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
    }
}

job(applicationName + ' Staging-Security-Tests') {
    deliveryPipelineConfiguration("Staging", "Security-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
    }
}

job(applicationName + ' Staging-Integration-Tests') {
    deliveryPipelineConfiguration("Staging", "Integration-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
        buildPipelineTrigger(applicationName + ' Prod-Deploy') {
        }
    }
}

job(applicationName + ' Prod-Deploy') {
    deliveryPipelineConfiguration("Prod", "Deploy")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        
	}
    publishers {
		buildPipelineTrigger(applicationName + ' Prod-Integration-Tests') {
		}
    }
}

job(applicationName + ' Prod-Integration-Tests') {
    deliveryPipelineConfiguration("Prod", "Integration-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
    }
}
