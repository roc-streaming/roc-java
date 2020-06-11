#!/usr/bin/env bash
set -euxo pipefail

docker run -t --rm --privileged --device /dev/kvm --env API=$ANDROID_API --env BUILD_TOOLS_VERSION=$ANDROID_BUILD_TOOLS_VERSION \
    --env NDK_VERSION=$ANDROID_NDK_VERSION --env ROC_BASE_DIR=$ROC_BASE_DIR \
    -v "${PWD}:${PWD}" -v $ROC_BASE_DIR:$ROC_BASE_DIR \
    -v android-sdk:/sdk -w "${PWD}" \
        rocproject/java-android:jdk$JAVA_VERSION \
            $PWD/scripts/travis/android/entrypoint/script.sh
