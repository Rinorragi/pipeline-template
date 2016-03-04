def applicationName = 'Grade-Calculator'
def gitRepository = 'https://github.com/Rinorragi/Grade-Calculator.git'
def solutionFile = 'GradeCalculator.sln'

def developmentEnvironmentName = 'Dev'
def customerTestEnvironmentName = 'QA'
def productionEnvironmentName = 'Prod'

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
		buildName('\$PIPELINE_VERSION')
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
	configure { project ->
			def msbuild = project / builders / 'hudson.plugins.msbuild.MsBuildBuilder'
			(msbuild / msBuildName).value = '(Default)'
			(msbuild / msBuildFile).value = solutionFile
			(msbuild / cmdLineArgs).value = '/p:Configuration=Release /p:DeployOnBuild=True /p:PublishProfile=&quot;CI&quot;'
			(msbuild / buildVariablesAsProperties).value = 'true'
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
		downstream(applicationName + ' ' + developmentEnvironmentName + '-Deploy', 'SUCCESS')
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

job(applicationName + ' ' + developmentEnvironmentName + '-Deploy') {
    deliveryPipelineConfiguration("Dev", "Deploy")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        
	}
    publishers {
		buildPipelineTrigger(applicationName + ' ' + developmentEnvironmentName + '-EndToEnd-Tests') {
		}
    }
}

job(applicationName + ' ' + developmentEnvironmentName + '-EndToEnd-Tests') {
    deliveryPipelineConfiguration("Dev", "EndToEnd-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
        buildPipelineTrigger(applicationName + ' ' + customerTestEnvironmentName + '-Deploy') {
        }
    }
}

job(applicationName + ' ' + customerTestEnvironmentName + '-Deploy') {
    deliveryPipelineConfiguration("Staging", "Deploy")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        
	}
    publishers {
		buildPipelineTrigger(applicationName + ' ' + customerTestEnvironmentName + '-Smoke-Tests') {
		}
		buildPipelineTrigger(applicationName + ' ' + customerTestEnvironmentName + '-Performance-Tests') {
		}
		buildPipelineTrigger(applicationName + ' ' + customerTestEnvironmentName + '-Security-Tests') {
		}
    }
}

job(applicationName + ' ' + customerTestEnvironmentName + '-Performance-Tests') {
    deliveryPipelineConfiguration("Staging", "Performance-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
    }
}

job(applicationName + ' ' + customerTestEnvironmentName + '-Security-Tests') {
    deliveryPipelineConfiguration("Staging", "Security-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
    }
}

job(applicationName + ' ' + customerTestEnvironmentName + '-Smoke-Tests') {
    deliveryPipelineConfiguration("Staging", "Smoke-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
        buildPipelineTrigger(applicationName + ' ' + productionEnvironmentName + '-Deploy') {
        }
    }
}

job(applicationName + ' ' + productionEnvironmentName + '-Deploy') {
    deliveryPipelineConfiguration("Prod", "Deploy")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        
	}
    publishers {
		buildPipelineTrigger(applicationName + ' ' + productionEnvironmentName + '-Smoke-Tests') {
		}
    }
}

job(applicationName + ' ' + productionEnvironmentName + '-Smoke-Tests') {
    deliveryPipelineConfiguration("Prod", "Smoke-Tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
    }
    publishers {
    }
}
