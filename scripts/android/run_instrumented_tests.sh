#!/usr/bin/env bash
set -euxo pipefail

export TERM=dumb

cd android

./gradlew cAT --info --stacktrace || \
    { adb logcat -t 500; exit 1; }
