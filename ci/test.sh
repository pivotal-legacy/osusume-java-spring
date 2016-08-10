#!/bin/bash

set -e -x

pushd osusume-java-spring
    dropdb --if-exists -e osusume-test
    createdb -e osusume-test
    ./gradlew flywayMigrate
    ./gradlew build
OSUSUME_DATABASE_URL=jdbc:postgresql://localhost/osusume-test java -jar build/libs/osusume-java-spring-0.0.1-SNAPSHOT.jar &
JAVA_SERVER_PID=$!
    TERM=dumb ./gradlew clean test build
popd