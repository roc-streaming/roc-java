#!/usr/bin/env bash
set -euxo pipefail

if [ "${JAVA_VERSION-}" = "8" ]; then
    export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
    export TERM=dumb
fi

java -version
./gradlew build -Dgenerator=ninja
