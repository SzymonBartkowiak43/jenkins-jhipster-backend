#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        sh "java -version"
    }

    stage('clean') {
        sh "chmod +x mvnw"
        sh "./mvnw -ntp clean -P-webapp"
    }

    stage('packaging') {
        // Buduj z profilem dev
        sh "./mvnw -ntp package -P-webapp -Pdev -DskipTests"
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

    stage('deploy and run') {
        def jarFile = sh(script: "find \$(pwd) -name '*.jar' | grep -v original | head -1", returnStdout: true).trim()

        // Zatrzymaj istniejącą aplikację (jeśli działa)
        sh "pkill -f '${jarFile}' || true"

        // Uruchom aplikację bezpośrednio
        sh """
        nohup java -jar ${jarFile} \
        --spring.profiles.active=dev \
        --server.address=0.0.0.0 \
        --server.port=8081 \
        --spring.datasource.url=jdbc:h2:mem:stefikback \
        --spring.datasource.username=sa \
        --spring.datasource.password= \
        > app.log 2>&1 &
        """

        sh "sleep 30"
        sh "cat app.log | tail -n 50 || echo 'Log file not found'"
    }

    stage('check application status') {
        sh "ps aux | grep java || echo 'No Java process found'"
        sh "netstat -tulpn | grep 8081 || echo 'No process listening on port 8081'"
    }

    stage('verify') {
        try {
            sh "curl -v http://localhost:8081/actuator/health || curl -v http://localhost:8081/management/health || echo 'Failed to connect to health endpoint'"
            echo "Aplikacja powinna być dostępna pod adresem: http://localhost:8081"
        } catch(err) {
            echo "Weryfikacja nie powiodła się: ${err}"
            throw err
        }
    }
}
