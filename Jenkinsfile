pipeline {
    agent any
    tools {
        gradle 'gradle'
    }
    stages {
        stage('Git Clone') {
            steps {
                git branch: 'cicdtest', url: 'https://github.com/KA-ForCloud/backend-chatting.git'
            }
        }
        stage('BE-Build') {
            steps {
                    sh "./gradlew clean build --exclude-task test"
            }
        }
        stage('Deploy') {
            steps {
                echo "Deploy Start"
                sshagent(credentials: ['kic_key']) {
                    echo "sshagent start"
                    sh '''
                        ssh -o StrictHostKeyChecking=no centos@210.109.62.6 uptime
                        scp /var/jenkins_home/workspace/forCloud_Backend-chatting_Pipeline/build/libs/chat-0.0.1-SNAPSHOT.jar centos@210.109.62.6:/home/centos/Backend-chatting
                        ssh -t centos@210.109.62.6 ./deploy1.sh
                    '''
                    echo "Success"
                }
            }
        }
    }
}
