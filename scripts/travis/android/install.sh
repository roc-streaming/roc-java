#!/bin/bash
set -euxo pipefail

roc_build_dir=/tmp/roc
install_prefix_dir=$ROC_BASE_DIR/install

target_arch=("aarch64" "arm" "i686" "x86_64")
target_abi=("arm64-v8a" "armeabi-v7a" "x86" "x86_64")

git clone https://github.com/roc-project/roc.git $roc_build_dir
cd $roc_build_dir

git checkout 61d9cafd5e916413280dbb40f997a71ffdd28ac6 # resolve libuv build issue on master tag

for i in "${!target_arch[@]}"; do
    arch="${target_arch[i]}"
    abi="${target_abi[i]}"
    docker run -t --rm -v "${PWD}:${PWD}" -v $ROC_BASE_DIR:$ROC_BASE_DIR -w "${PWD}" \
        cross-linux-android:api$ANDROID_API \
        /bin/bash -c \
        "scons --compiler=clang \
            --host=$arch-linux-android \
            --libdir=${ROC_BASE_DIR}/lib/$abi \
            --incdir=${ROC_BASE_DIR}/include/$abi \
            --disable-tools \
            --disable-examples \
            --disable-tests \
            --build-3rdparty=libuv,openfec && \
        scons --compiler=clang \
            --host=$arch-linux-android \
            --libdir=${ROC_BASE_DIR}/lib/$abi \
            --incdir=${ROC_BASE_DIR}/include/$abi \
            --disable-tools \
            --disable-examples \
            --disable-tests \
            --build-3rdparty=libuv,openfec install"
done

find $ROC_BASE_DIR -type d -print

echo y | sdkmanager --licenses &> /dev/null
echo y | sdkmanager "platforms;android-${ANDROID_API}" &> /dev/null
echo y | sdkmanager "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" &> /dev/null
echo y | sdkmanager "ndk-bundle" &> /dev/null
echo y | sdkmanager "ndk;${ANDROID_NDK_VERSION}" &> /dev/null
echo y | sdkmanager "cmake;3.10.2.4988404" &> /dev/null