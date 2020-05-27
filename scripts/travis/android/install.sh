#!/usr/bin/env bash
set -euxo pipefail

target_host=("aarch64-linux-android" "armv7a-linux-androideabi" "i686-linux-android" "x86_64-linux-android")
target_abi=("arm64-v8a" "armeabi-v7a" "x86" "x86_64")

working_dir=$(pwd)

git clone https://github.com/roc-project/roc.git /tmp/roc
cd /tmp/roc

for i in "${!target_host[@]}"; do
    host="${target_host[i]}${ANDROID_API}"
    abi="${target_abi[i]}"
    docker run -t --rm --env API=$ANDROID_API --env BUILD_TOOLS_VERSION=$ANDROID_BUILD_TOOLS_VERSION \
        --env NDK_VERSION=$ANDROID_NDK_VERSION --env ROC_BASE_DIR=$ROC_BASE_DIR \
        -v "${PWD}:${PWD}" -v $ROC_BASE_DIR:$ROC_BASE_DIR \
        -v $working_dir/scripts/travis/android/entrypoint/install.sh:/opt/install.sh \
        -v android-sdk:/sdk -w "${PWD}" \
            rocproject/java-android:jdk$JAVA_VERSION \
                /opt/install.sh $host $abi
done
