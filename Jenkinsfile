#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    stage('build') {
        sh "chmod +x mvnw"
        sh "./mvnw -ntp package -P-webapp -DskipTests"
    }

    stage('build docker image') {
        try {
            sh "./mvnw -ntp -Pprod jib:dockerBuild -DskipTests"
            sh "docker images | grep stefikback"
            echo "Obraz Docker został zbudowany. Możesz go uruchomić komendą: docker run -p 8081:8080 stefikback:latest"
        } catch (Exception e) {
            echo "Błąd podczas budowania obrazu Docker. Możliwy brak dostępu do demona Docker."
            echo "Możesz uruchomić aplikację bezpośrednio: java -jar target/*.jar"
            throw e
        }
    }
}
