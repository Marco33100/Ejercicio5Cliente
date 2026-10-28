pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    options {
        skipDefaultCheckout(true)
    }

    stages {
        stage('Descargar proyecto') {
            steps {
                withEnv([
                    'GIT_CONFIG_COUNT=1',
                    'GIT_CONFIG_KEY_0=http.version',
                    'GIT_CONFIG_VALUE_0=HTTP/1.1'
                ]) {
                    retry(2) {
                        checkout scmGit(
                            branches: [[name: '*/main']],
                            extensions: [
                                cloneOption(
                                    shallow: true,
                                    depth: 1,
                                    noTags: true,
                                    honorRefspec: true,
                                    timeout: 30
                                )
                            ],
                            userRemoteConfigs: [[
                                url: 'https://github.com/Marco33100/Ejercicio5Cliente.git',
                                refspec: '+refs/heads/main:refs/remotes/origin/main'
                            ]]
                        )
                    }
                }
            }
        }

        stage('Ver Directorio') {
            steps {
                sh 'pwd'
                sh 'ls -la'
                sh 'cat pom.xml'
            }
        }

        stage('Verificar Java y Maven') {
            steps {
                sh 'java -version'
                sh 'javac -version'
                sh 'mvn -version'
            }
        }

        stage('Build & Test') {
            steps {
                dir('.') {
                    sh '''
                        mvn --batch-mode --no-transfer-progress clean verify -Dsurefire.useFile=false
                    '''
                }
            }
        }

        stage('Publicar cobertura') {
            steps {
                recordCoverage(
                    tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco/jacoco.xml']],
                    sourceDirectories: [[path: 'src/main/java']],
                    sourceCodeRetention: 'LAST_BUILD',
                    failOnError: true
                )
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarServer') {
                    dir('.') {
                        sh '''
                            set +x
                            SONAR_TOKEN="$SONAR_AUTH_TOKEN" \
                            mvn --batch-mode --no-transfer-progress \
                                org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356:sonar \
                                -Dsonar.projectKey=Ejercicio5Cliente \
                                -Dsonar.projectName=Ejercicio5Cliente \
                                -Dsonar.host.url=http://host.docker.internal:9000 \
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                                -Dsonar.exclusions=target/**,**/target/**
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (fileExists('target/surefire-reports')) {
                    junit testResults: 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}
