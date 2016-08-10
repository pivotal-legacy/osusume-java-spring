#!/bin/bash

set -e -x

pushd osusume-java-spring
    make tests
popd