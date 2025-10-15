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

    stage('build docker image') {
        // Użyjmy tego samego profilu dev dla spójności
        sh "./mvnw package -P-webapp -Pdev -DskipTests jib:dockerBuild"
        sh "docker images | grep stefikback"
    }

    stage('run docker container') {
        sh "docker stop stefikback || true"
        sh "docker rm stefikback || true"

        // Dodajmy zmienne środowiskowe dla konfiguracji aplikacji
        sh """
        docker run -d --name stefikback \
          -p 8081:8080 \
          -e SPRING_PROFILES_ACTIVE=dev \
          -e MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE='*' \
          -e MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always \
          stefikback:latest
        """

        // Krótkie oczekiwanie na uruchomienie
        sh "sleep 10"

        // Sprawdź logi, ale bez blokowania pipeline'a
        sh "docker logs stefikback || true"
        sh "sleep 20"  // Daj więcej czasu na pełne uruchomienie
    }

    stage('verify') {
        try {
            // Sprawdź health endpoint
            sh "curl -v http://localhost:8081/management/health || curl -v http://localhost:8081/actuator/health"

            echo "Aplikacja jest dostępna pod adresem: http://localhost:8081"

            // Pokaż logi z kontenera
            sh "docker logs stefikback | tail -n 50"
        } catch(err) {
            echo "Weryfikacja nie powiodła się: ${err}"
            sh "docker logs stefikback"
            throw err
        }
    }
}
