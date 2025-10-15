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

    stage('packaging') {
        sh "./mvnw -ntp verify -P-webapp -Pprod -DskipTests"
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

    stage('deploy and run') {
        // Znajdź JAR używając polecenia shell zamiast findFiles
        def jarFile = sh(script: "find target -name '*.jar' | grep -v original", returnStdout: true).trim()

        // Zatrzymaj istniejącą aplikację (jeśli działa)
        sh "pkill -f '${jarFile}' || true"

        // Uruchom aplikację w tle
        sh "nohup java -jar ${jarFile} --server.port=8081 > app.log 2>&1 &"

        // Poczekaj na uruchomienie
        sh "sleep 30"
    }

    stage('verify') {
        try {
            // Sprawdź, czy aplikacja działa
            sh "curl -s http://localhost:8081/management/health | grep UP || (cat app.log && exit 1)"

            // Pokaż logi
            sh "cat app.log"

            echo "Aplikacja jest dostępna pod adresem: http://localhost:8081"
        } catch(err) {
            echo "Weryfikacja nie powiodła się: ${err}"
            sh "cat app.log"
            throw err
        }
    }
}
