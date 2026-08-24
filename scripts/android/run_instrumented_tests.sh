#!/usr/bin/env bash

set -euxo pipefail

export TERM=dumb

if [ -d /home/user ]
then
    export HOME=/home/user
    export ANDROID_USER_HOME="${HOME}/.android"
fi

cd android

./gradlew cAT --info --stacktrace || \
    { adb logcat -t 500; exit 1; }
