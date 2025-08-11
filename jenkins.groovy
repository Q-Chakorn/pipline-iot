pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', credentialsId: 'github-connec', url: 'https://github.com/Q-Chakorn/testiot.git'
            }
        }
    }
}
