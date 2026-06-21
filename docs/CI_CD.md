# BudgetQuest CI/CD

BudgetQuest uses a `Jenkinsfile` to define a basic Continuous Integration pipeline.

## What CI Means

CI stands for Continuous Integration.

In this project, CI means:

1. Install frontend dependencies.
2. Build the React frontend.
3. Run backend tests for every Spring Boot service.
4. Validate Docker Compose configuration.
5. Collect test reports.

## Pipeline Stages

## Frontend Build

```groovy
stage('Frontend Build') {
    steps {
        dir('frontend') {
            sh 'npm ci'
            sh 'npm run build'
        }
    }
}