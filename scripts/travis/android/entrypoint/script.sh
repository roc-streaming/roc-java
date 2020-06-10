#!/bin/bash
set -euxo pipefail

roc_device_name="roc_device"
roc_device_image="default"

# create and start avd
device --name "${roc_device_name}" --image "${roc_device_image}" create
device --name "${roc_device_name}" start

cd android
./gradlew build
./gradlew cAT
