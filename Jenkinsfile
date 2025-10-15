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

    stage('backend build') {
        sh "./mvnw -ntp verify -P-webapp -DskipTests"
    }

    stage('packaging') {
        sh "./mvnw -ntp verify -P-webapp -Pdev -DskipTests"
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

    stage('build docker image') {
        sh "./mvnw -ntp -Pdev jib:dockerBuild -DskipTests"

        sh "docker images | grep stefikback"
    }
}
