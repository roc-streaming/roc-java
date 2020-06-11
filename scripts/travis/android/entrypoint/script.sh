#!/bin/bash
set -euxo pipefail

export TERM=dumb

roc_device_name="roc_device"
roc_device_image="default"

# create and start avd
device --name "${roc_device_name}" --image "${roc_device_image}" --api "${API}" create
device --name "${roc_device_name}" start

avdmanager list avd

cd android
./gradlew build
./gradlew cAT --info --stacktrace
