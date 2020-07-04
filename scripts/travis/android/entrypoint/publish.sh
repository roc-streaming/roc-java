#!/bin/bash
set -euxo pipefail

export TERM=dumb

cd android
./gradlew build bintrayUpload

