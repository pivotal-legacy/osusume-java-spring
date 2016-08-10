#!/bin/bash

set -e -x

pushd osusume-java-spring
    TERM=dumb OSUSUME_DATABASE_URL=jdbc:postgresql://localhost/osusume-dev ./gradlew clean test build
popd