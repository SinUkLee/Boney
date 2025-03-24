pipeline {
    agent { label 'ec2' }

    environment {
        TZ = 'Asia/Seoul'
    }

    options {
        skipDefaultCheckout(true)
    }

    stages {
        stage('Fix Permissions') {
            steps {
                sh 'sudo chown -R ubuntu:ubuntu $WORKSPACE || true'
            }
        }

        stage('Checkout Source') {
            steps {
                echo "📦 Git 리포지토리 클론 중..."
                git branch: 'release',
                    url: 'https://lab.ssafy.com/s12-fintech-finance-sub1/S12P21B208.git',
                    credentialsId: 'choihyunman'
            }
        }

        stage('Load .env File') {
            steps {
                echo "🔐 .env 파일 로딩 중..."
                withCredentials([file(credentialsId: 'choi', variable: 'ENV_FILE')]) {
                    sh '''
                    rm -f .env
                    cp $ENV_FILE .env
                    '''
                }
            }
        }

        stage('Copy application.yml') {
            steps {
                echo "📄 application.yml 복사 중..."
                withCredentials([file(credentialsId: 'app-yml', variable: 'APP_YML')]) {
                    sh '''
                    mkdir -p BE/src/main/resources
                    cp $APP_YML BE/src/main/resources/application.yml
                    '''
                }
            }
        }

        stage('Stop Existing Containers') {
            steps {
                echo "🛑 기존 컨테이너 중지 및 삭제 중..."
                sh '''
                docker compose down --remove-orphans || true
                docker rm -f frontend || true
                docker rm -f backend || true
                docker rm -f mysql || true
                '''
            }
        }

        stage('Build & Deploy') {
            steps {
                echo "⚙️ 이미지 빌드 & 컨테이너 실행 중..."
                sh 'docker compose build'
                sh 'docker compose up -d'
            }
        }
    }

    post {
        success {
            echo '✅ 배포 성공!'
        }
        failure {
            echo '❌ 배포 실패!'
        }
    }
}
