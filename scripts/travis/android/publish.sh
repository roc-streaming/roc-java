#!/usr/bin/env bash
set -euxo pipefail

docker run -t --rm --privileged --device /dev/kvm \
    --env API=$ANDROID_API \
    --env BUILD_TOOLS_VERSION=$ANDROID_BUILD_TOOLS_VERSION \
    --env NDK_VERSION=$ANDROID_NDK_VERSION \
    --env ROC_BASE_DIR=$ROC_BASE_DIR \
    --env BINTRAY_USER=$BINTRAY_USER \
    --env BINTRAY_KEY=$BINTRAY_KEY \
    --env BINTRAY_REPO=$BINTRAY_REPO \
    --env BINTRAY_REPO_ORG=$BINTRAY_REPO_ORG \
    -v "${PWD}:${PWD}" -v $ROC_BASE_DIR:$ROC_BASE_DIR \
    -v android-sdk:/sdk -w "${PWD}" \
        rocstreaming/env-android:jdk$JAVA_VERSION \
            $PWD/scripts/travis/android/entrypoint/publish.sh
