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

node("macbook"){
    checkout scm
    cleanWs()
    stage('gitclone'){
        sh """
            git clone https://github.com/Q-Chakorn/testiot.git
        """
    }
}
