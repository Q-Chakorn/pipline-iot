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
def datalogPath = "/Users/ikkyu/root/workspace/test/testiot/testiot/datalogger-agent"
def iaqPath = "/Users/ikkyu/root/workspace/test/testiot/testiot/iaq-agent"
def deployPath = "/home/testdevops/testiot"
properties([
    parameters([
        choice(name: 'SERVICES', choices: ['datalogger-agent', 'iaq-agent'], description: ''),
        string(name: 'TAG', defaultValue: '', description: 'Docker image tag')
    ])
])
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
                docker buildx build --platform linux/amd64,linux/arm64 -t testiotacr.azurecr.io/datalogger-agent:${TAG} --push .
            """
        } else if (params.SERVICES == 'iaq-agent') {
            sh """
                cd ${iaqPath} && ls -la
                docker buildx build --platform linux/amd64,linux/arm64 -t iaq-agent:${TAG} .
                docker tag iaq-agent:${TAG} testiotacr.azurecr.io/iaq-agent:${TAG}
            """
        }
    }
    stage('push images to ACR'){
        withCredentials([azureServicePrincipal('azure-credentials')]) {
            if (params.SERVICES == 'datalogger-agent') {
                sh """
                    az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant $AZURE_TENANT_ID
                    az acr login --name testiotacr
                    docker push testiotacr.azurecr.io/datalogger-agent:${TAG}
                """
            } else if (params.SERVICES == 'iaq-agent') {
                sh """
                    az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant $AZURE_TENANT_ID
                    az acr login --name testiotacr
                    docker push testiotacr.azurecr.io/iaq-agent:${TAG}
                """
            }
        }
    }
    stage('pull image from acr'){
        sshagent(['ssh-credentials']) {
            withCredentials([azureServicePrincipal('azure-credentials')]){
                if (params.SERVICES == 'datalogger-agent') {
                    sh"""
                        ssh -o StrictHostKeyChecking=no testdevops@20.6.33.223 '''
                        az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant $AZURE_TENANT_ID
                        az acr login --name testiotacr
                        docker pull testiotacr.azurecr.io/datalogger-agent:${TAG}
                        '''
                    """
                }else if (params.SERVICES == 'iaq-agent') {
                    sh"""
                        ssh -o StrictHostKeyChecking=no testdevops@20.6.33.223 '''
                        az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant $AZURE_TENANT_ID
                        az acr login --name testiotacr
                        docker pull testiotacr.azurecr.io/iaq-agent:${TAG}
                        '''
                    """                
                }
            }
        }
    }
    stage('prepare'){
        sshagent(['ssh-credentials']) {
            sh"""
                ssh -o StrictHostKeyChecking=no testdevops@20.6.33.223 '''
                    if [ -d 'testiot' ]; then
                        echo 'Directory testiot exists, pulling latest changes'
                        cd testiot && git pull
                    else
                        echo 'Directory testiot does not exist, cloning repository'
                        git clone https://github.com/Q-Chakorn/testiot.git
                    fi
                cd ${deployPath}
                docker-compose -f 4.datalogger-agent.yaml down
                docker-compose -f 3.iaq-agent.yaml down
                '''
            """
        }
    }
    stage('deploy'){
        sshagent(['ssh-credentials']) {
            if (params.SERVICES == 'datalogger-agent') {
                sh """
                    ssh -o StrictHostKeyChecking=no testdevops@20.6.33.223 '''
                    cd ${deployPath}
                    docker-compose -f 4.datalogger-agent.yaml up -d
                    '''
                """
            }else if (params.SERVICES == 'iaq-agent') {
                sh """
                    ssh -o StrictHostKeyChecking=no testdevops@20.6.33.223 '''
                    cd ${deployPath}
                    docker-compose -f 3.iaq-agent.yaml up -d
                    '''
                """
            }
        }
    }
}
