#!/usr/bin/env bash

set -euxo pipefail

export TERM=dumb

cd android
# automatically creates staging repository and upload archives to there
./gradlew publish
# close staging and release repository
./gradlew closeAndReleaseRepository
