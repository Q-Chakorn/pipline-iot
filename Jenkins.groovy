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
}
