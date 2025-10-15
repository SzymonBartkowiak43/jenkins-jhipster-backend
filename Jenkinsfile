#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    stage('build') {
        sh "chmod +x mvnw"
        sh "./mvnw -ntp package -P-webapp -DskipTests"
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

    stage('run') {
        // Znajdź plik JAR
        def jarFile = sh(script: "find \$(pwd) -name '*.jar' | grep -v original | head -1", returnStdout: true).trim()

        // Zatrzymaj poprzednią instancję jeśli działa
        sh "pkill -f java || true"

        // Uruchom aplikację
        sh "nohup java -jar ${jarFile} --spring.profiles.active=dev > app.log 2>&1 &"

        echo "Aplikacja uruchomiona. Logi dostępne w pliku app.log"
    }
}
