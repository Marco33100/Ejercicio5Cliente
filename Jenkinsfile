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
                checkout scm
            }
        }

        stage('Verificar Java y Maven') {
            steps {
                sh 'java -version'
                sh 'javac -version'
                sh 'mvn -version'
            }
        }

        stage('Compilar y probar') {
            steps {
                sh 'mvn --batch-mode --no-transfer-progress clean verify'
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
