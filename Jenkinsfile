pipeline {
    agent any

    tools {
        jdk ("JDK 21")
    }

    environment {
        GRADLE_USER_HOME = "/var/jenkins_home/workspace/.gradle"
        IMAGE_NAME = 'toastit/v1'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${env.BRANCH_NAME}", url: 'https://github.com/ToastitProject/toastit_v1.git'
            }
        }

        stage('Env Config Setup') {
            parallel {
                stage('Production Environment Setup') {
                    steps {
                        withCredentials([file(credentialsId: 'prod-env-file', variable: 'dbConfigFile')]) {
                            sh '''
                                install -D -m 644 $dbConfigFile ./src/main/resources/properties/env.properties
                            '''
                        }
                    }
                }
                stage('Test Environment Setup') {
                    steps {
                        withCredentials([file(credentialsId: 'test-env-file', variable: 'dbConfigFile')]) {
                            sh '''
                                install -D -m 644 $dbConfigFile ./src/test/resources/properties/env.properties
                            '''
                        }
                    }
                }
            }
        }

        stage('SSH Equipment & Build') {
            steps {
                cache(maxCacheSize: 1024, caches: [
                    arbitraryFileCache(
                        path: '${GRADLE_USER_HOME}',
                        excludes: '**/*.lock',
                        cacheValidityDecidingFile: 'gradle/wrapper/gradle-wrapper.properties'
                    )
                ]) {
                    sh './gradlew build -x test'
                }
            }
        }

        stage('main 브랜치는 Prod 서버에 배포한다') {
            when {
                expression { env.BRANCH_NAME == 'main' }
            }
            steps {
                sh 'ssh -fN mydeploy'
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKERHUB_PSW', usernameVariable: 'DOCKERHUB_USR')]) {
                    sh '''
                        echo $DOCKERHUB_PSW | docker login -u $DOCKERHUB_USR --password-stdin && \
                        docker build -t ${IMAGE_NAME}:latest . && \
                        docker push ${IMAGE_NAME}:latest && \
                        docker builder prune -f
                    '''
                }
                sh 'ssh mydeploy "sh deploy.sh"'
            }
        }

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }
    }
}
