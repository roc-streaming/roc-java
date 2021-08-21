#!/usr/bin/env bash
set -euxo pipefail

export TERM=dumb

cd android
./gradlew uploadArchives
# close staging and release repository
./gradlew closeAndReleaseRepository
