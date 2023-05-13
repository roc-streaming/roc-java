#!/usr/bin/env bash

set -euxo pipefail

export PATH="${ANDROID_SDK_ROOT}/tools/bin:${PATH}"
export PATH="${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${PATH}"

# create avd if it doesn't exist
if ! avdmanager list avd -c | grep -qF roc_device
then
    device --name roc_device \
           --image "${AVD_IMAGE}" --arch "${AVD_ARCH}" --api "${API}" \
           create
fi

# restart emulator
device --name roc_device \
    start

# show avd list
avdmanager list avd
