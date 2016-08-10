#!/bin/bash

set -e -x

pushd osusume-java-spring
    ./gradlew clean test build
popd