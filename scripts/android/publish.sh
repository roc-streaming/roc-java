#!/usr/bin/env bash
set -euxo pipefail

export TERM=dumb

cd android
./gradlew bintrayUpload
