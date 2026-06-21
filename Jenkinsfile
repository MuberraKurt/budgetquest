pipeline {
    agent any

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

        stage('Backend Tests') {
            parallel {
                stage('Discovery Server') {
                    steps {
                        dir('backend/discovery-server') {
                            sh './mvnw test'
                        }
                    }
                }

                stage('Config Server') {
                    steps {
                        dir('backend/config-server') {
                            sh './mvnw test'
                        }
                    }
                }

                stage('API Gateway') {
                    steps {
                        dir('backend/api-gateway') {
                            sh './mvnw test'
                        }
                    }
                }

                stage('User Service') {
                    steps {
                        dir('backend/user-service') {
                            sh './mvnw test'
                        }
                    }
                }

                stage('Transaction Service') {
                    steps {
                        dir('backend/transaction-service') {
                            sh './mvnw test'
                        }
                    }
                }

                stage('Budget Service') {
                    steps {
                        dir('backend/budget-service') {
                            sh './mvnw test'
                        }
                    }
                }

                stage('Goal Service') {
                    steps {
                        dir('backend/goal-service') {
                            sh './mvnw test'
                        }
                    }
                }

                stage('Gamification Service') {
                    steps {
                        dir('backend/gamification-service') {
                            sh './mvnw test'
                        }
                    }
                }

                stage('Insight Service') {
                    steps {
                        dir('backend/insight-service') {
                            sh './mvnw test'
                        }
                    }
                }
            }
        }

        stage('Docker Compose Config Check') {
            steps {
                sh 'docker compose config'
            }
        }
    }
    post {
        always {
           junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
        }
    }
}