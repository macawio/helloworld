def dockerImageTag
pipeline {
    agent any
    triggers {
        // poll the SCM every 5 minutes and trigger a build
        // if any new changes are present
        pollSCM('H/5 * * * *')
    }
    parameters {
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: 'Should the tests be run?')
        booleanParam(name: 'PUBLISH_DOCKER_IMAGE', defaultValue: true, description: 'Should a docker image with a new tag be created and published to a docker repo?')
        booleanParam(name: 'PROVISION_SERVICE', defaultValue: true, description: 'Should the service be provisioned to a Macaw platform runtime?')
        string(name: 'SERVICE_BLUEPRINT_ID', defaultValue:'98512989-095c-5f3f-9aaa-cb2142639236', description: 'The blueprint id to use for provisioning the service. This will be used if service provisioning is enabled')
        booleanParam(name: 'ROLLING_UPDATE', defaultValue: true, description: 'Will this be a rolling update or a new cluster needs to be provisioned')
        booleanParam(name: 'SKIP_CERT_VERIFICATION', defaultValue: true, description: 'If set to true then any SSL certificate errors will be ignored. Typically, for self-signed certificates this param can be set to true')
        string(name: 'SERVICE_ID', defaultValue:'', description: 'The service id of the service in blueprint that needs to be provisioned')
    }
    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                dir('api') {
                    sh "ant clean deploy"
                }
                dir('impl') {
                    sh "ant clean deploy"
                }
                dir('webapp') {
                    sh "ant clean war"
                }
            }
        }
        stage('Test') {
            when {
                expression {
                    return params.RUN_TESTS
                }
            }
            steps {
                dir('impl') {
                    sh "ant test"
                }
            }
        }
        stage('Deploy') {
            when {
                expression {
                    return params.PUBLISH_DOCKER_IMAGE
                }
            }
            steps {
                script {
                    def jobName = env.JOB_NAME
                    def date = new Date().format('yyyy_MM_dd_HH_mm_ss')
                    dockerImageTag = "${jobName}_${date}"
                    def serviceHome = env.WORKSPACE
                    def macawPublishCmd = "${env.MACAW_SDK_HOME}/tools/macaw-publish-tools/bin/macawpublish"
                    echo "Generating a new docker image tag ${dockerImageTag}"
                    sh "export PYTHONIOENCODING=UTF-8; ${macawPublishCmd} service --tag ${dockerImageTag} ${serviceHome}"
                }
            }
        }
        stage('Provision Service') {
            when {
                expression {
                    return params.PROVISION_SERVICE
                }
            }
            environment {
                MACAW_USER_ACCESS_KEY = credentials('macaw-user-access-key')
                MACAW_USER_SECRET_KEY = credentials('macaw-user-secret-key')
            }
            steps {
                script {
                    def macawPublishCmd = "${env.MACAW_SDK_HOME}/tools/macaw-publish-tools/bin/macawpublish"
                    echo "Provisioning service to Macaw platform - ${env.MACAW_PLATFORM_URL}"

                    def provisionCommand = "${macawPublishCmd} provision-blueprint --tag ${dockerImageTag} --macaw-platform ${env.MACAW_PLATFORM_URL} --provisioner-docker-repo-name ${env.PROVISIONER_DOCKER_REPO} --provisioning-environment ${env.PROVISIONER_ENV} --auth-access-key $MACAW_USER_ACCESS_KEY --auth-secret-key-env-var MACAW_USER_SECRET_KEY ${params.SERVICE_BLUEPRINT_ID} --service ${params.SERVICE_ID}"

                    if (!params.ROLLING_UPDATE) {
                        provisionCommand = "${provisionCommand} --no-rolling-update"
                    }
                    if (params.SKIP_CERT_VERIFICATION) {
                        provisionCommand = "${provisionCommand} --skip-cert-verification"
                    }
                    sh "export PYTHONIOENCODING=UTF-8; ${provisionCommand}"
                }
            }
        }
    }
}
