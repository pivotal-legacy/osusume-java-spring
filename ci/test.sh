#!/bin/bash

set -e -x

pushd osusume-java-spring
    DATA_DIR=/var/lib/pgsql/data
    su postgres -c "pg_ctl -D $DATA_DIR -l ${DATA_DIR}/logfile start"
    sleep 10
    su postgres -c "dropdb --if-exists -e osusume-test"
    su postgres -c "createdb -e osusume-test"
    su postgres -c "./gradlew flywayMigrate"
    su postgres -c "./gradlew build"
    su postgres -c "OSUSUME_DATABASE_URL=jdbc:postgresql://localhost/osusume-test java -jar build/libs/osusume-java-spring-0.0.1-SNAPSHOT.jar &
JAVA_SERVER_PID=$!"
    su postgres -c "TERM=dumb ./gradlew clean test build"
popd