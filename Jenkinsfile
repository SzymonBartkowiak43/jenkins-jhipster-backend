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
        // Buduj z profilem dev zamiast prod
        sh "./mvnw -ntp package -P-webapp -Pdev -DskipTests"
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

    stage('deploy and run') {
        def jarFile = sh(script: "find target -name '*.jar' | grep -v original", returnStdout: true).trim()

        sh "pkill -f '${jarFile}' || true"

        // Uruchom z aktywnym profilem dev i z bazą H2
        sh "nohup java -jar ${jarFile} --spring.profiles.active=dev --spring.datasource.url=jdbc:h2:mem:stefikback --spring.datasource.username=sa --spring.datasource.password= --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect --server.port=8081 > app.log 2>&1 &"

        sh "sleep 30"
    }

    stage('verify') {
        try {
            sh "curl -s http://localhost:8081/management/health | grep UP || (cat app.log && exit 1)"
            sh "cat app.log"
            echo "Aplikacja jest dostępna pod adresem: http://localhost:8081"
        } catch(err) {
            echo "Weryfikacja nie powiodła się: ${err}"
            sh "cat app.log"
            throw err
        }
    }
}
