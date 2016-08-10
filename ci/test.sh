#!/bin/bash

set -e -x

pushd osusume-java-spring
    TERM=dumb ./gradlew clean test build
popd