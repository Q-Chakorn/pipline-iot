// pipeline {
//     agent any
//     stages {
//         stage('Checkout') {
//             steps {
//                checkout scm
//             }
//         }
//     }
// }
properties([
    parameters([
        choice(name: 'SERVICES', choices: ['datalogger-agent', 'iaq-agent'], description: '')
        string(name: 'TAG', defaultValue: '', description: 'Docker image tag')
    ])
])
def datalogPath = "/Users/ikkyu/root/workspace/test/testiot/testiot/datalogger-agent"
def iaqPath = "/Users/ikkyu/root/workspace/test/testiot/testiot/iaq-agent"
node("macbook"){
    checkout scm
    cleanWs()
    stage('gitclone'){
        sh """
            git clone https://github.com/Q-Chakorn/testiot.git
            cd ${datalogPath} && ls -la
            cd ${iaqPath} && ls -la
        """
    }
    stage('build images'){
        if (params.SERVICES == 'datalogger-agent') {
            sh """
                cd ${datalogPath} && ls -la
                docker build -t datalogger-agent:${TAG} .
                docker tag datalogger-agent:${TAG} testiotacr/datalogger-agent:${TAG}
            """
        } else if (params.SERVICES == 'iaq-agent') {
            sh """
                cd ${iaqPath} && ls -la
                docker build -t iaq-agent:${TAG} .
                docker tag iaq-agent:${TAG} testiotacr/iaq-agent:${TAG}
            """
        }
    }
    stage('push images to ACR'){
        withCredentials([azureServicePrincipal('azure-credentials')]) {
            if (params.SERVICES == 'datalogger-agent') {
                sh """
                    az acr login --name testiotacr
                    docker push testiotacr/datalogger-agent:${TAG}
                """
            } else if (params.SERVICES == 'iaq-agent') {
                sh """
                    az acr login --name testiotacr
                    docker push testiotacr/iaq-agent:${TAG}
                """
            }
        }
    }
    // stage('deploy'){
    //     sshagent(['ssh-credentials']) {
    //         sh"""
    //             ssh -o StrictHostKeyChecking=no testdevops@20.6.33.223 "echo 'Connected to remote server'"
    //             ssh -o StrictHostKeyChecking=no testdevops@20.6.33.223 "
    //                 if [ -d 'testiot' ]; then
    //                     echo 'Directory testiot exists, pulling latest changes'
    //                     cd testiot && git pull
    //                 else
    //                     echo 'Directory testiot does not exist, cloning repository'
    //                     git clone https://github.com/Q-Chakorn/testiot.git
    //                 fi
    //             pwd
    //             ls -la
    //             "
    //         """
    //     }
    // }
}
