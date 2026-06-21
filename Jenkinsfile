pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    tools {
        nodejs 'node-22'
    }

    stages {
        stage('Frontend Build') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('Discovery Server Tests') {
            steps {
                dir('backend/discovery-server') {
                    sh './mvnw test'
                }
            }
        }

        stage('Config Server Tests') {
            steps {
                dir('backend/config-server') {
                    sh './mvnw test'
                }
            }
        }

        stage('API Gateway Tests') {
            steps {
                dir('backend/api-gateway') {
                    sh './mvnw test'
                }
            }
        }

        stage('User Service Tests') {
            steps {
                dir('backend/user-service') {
                    sh './mvnw test'
                }
            }
        }

        stage('Transaction Service Tests') {
            steps {
                dir('backend/transaction-service') {
                    sh './mvnw test'
                }
            }
        }

        stage('Budget Service Tests') {
            steps {
                dir('backend/budget-service') {
                    sh './mvnw test'
                }
            }
        }

        stage('Goal Service Tests') {
            steps {
                dir('backend/goal-service') {
                    sh './mvnw test'
                }
            }
        }

        stage('Gamification Service Tests') {
            steps {
                dir('backend/gamification-service') {
                    sh './mvnw test'
                }
            }
        }

        stage('Insight Service Tests') {
            steps {
                dir('backend/insight-service') {
                    sh './mvnw test'
                }
            }
        }

        stage('Docker Compose Config Check') {
            steps {
                sh '''
                    if command -v docker >/dev/null 2>&1; then
                        docker compose config
                    else
                        echo "Docker CLI is not available inside this Jenkins container; skipping Docker Compose config check."
                    fi
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
        }
    }
}
