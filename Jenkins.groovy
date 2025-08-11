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
            """
        } else if (params.SERVICES == 'iaq-agent') {
            sh """
                cd ${iaqPath} && ls -la
            """
        }
    }
    // stage('push images to ACR'){

    // }
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
