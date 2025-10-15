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

    stage('nohttp') {
        sh "./mvnw -ntp checkstyle:check"
    }

    // Usunięto dodatkowy nawias klamrowy który był tutaj

    stage('packaging') {
        sh "./mvnw -ntp verify -P-webapp -Pprod -DskipTests"
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

    def dockerImage
    stage('publish docker') {
        // Budujemy lokalny obraz Docker zamiast publikowania go w rejestrze
        sh "./mvnw -ntp -Pprod verify jib:dockerBuild -DskipTests"

        // Pobieramy nazwę obrazu z pom.xml lub używamy stałej
        dockerImage = sh(script: "grep '<image>' pom.xml | sed 's/.*<image>\\(.*\\)<\\/image>.*/\\1/'", returnStdout: true).trim()

        // Jeśli nie możesz pobrać nazwy obrazu z pom.xml, możesz użyć stałej wartości:
        // dockerImage = "twoja-nazwa-obrazu:latest"
    }

    stage('deploy') {
        // Zatrzymaj istniejący kontener, jeśli istnieje
        sh "docker stop jhipster-app || true"
        sh "docker rm jhipster-app || true"

        // Uruchom aplikację w kontenerze Docker
        sh "docker run --name jhipster-app -d -p 8081:8080 ${dockerImage}"

        // Poczekaj chwilę na uruchomienie aplikacji
        sh "sleep 30"
    }

    stage('verify') {
        try {
            // Sprawdź, czy aplikacja działa - POPRAWIONY PORT NA 8081
            sh "curl -s http://localhost:8081/management/health | grep UP || (docker logs jhipster-app && exit 1)"

            // Wyświetl URL, pod którym można zobaczyć aplikację
            echo "Aplikacja jest dostępna pod adresem: http://localhost:8081"

            // Wyświetl logi kontenera
            sh "docker logs jhipster-app"
        } catch(err) {
            echo "Weryfikacja nie powiodła się: ${err}"
            sh "docker logs jhipster-app"
            throw err
        }
    }
}
