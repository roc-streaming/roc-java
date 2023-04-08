#!/usr/bin/env bash
set -euxo pipefail

if [ ! -z "${ANDROID_NDK_ROOT:-}" ]
then
    ndk_root="${ANDROID_NDK_ROOT}"
elif [ ! -z "${ANDROID_SDK_ROOT:-}" ]
then
    ndk_root="${ANDROID_SDK_ROOT}/ndk/${NDK_VERSION}"
else
    ndk_root="${ANDROID_HOME}/ndk/${NDK_VERSION}"
fi

case "$OSTYPE" in
    darwin*)
        toolchain="${ndk_root}/toolchains/llvm/prebuilt/darwin-$(uname -m)/bin"
        ;;
    linux*)
        toolchain="${ndk_root}/toolchains/llvm/prebuilt/linux-$(uname -m)/bin"
        ;;
esac

if [ ! -d "$toolchain" ];
then
    echo "Toolchain not found at $toolchain"
    exit 1
fi
export PATH="${toolchain}:${PATH}"

target_host=(
    "aarch64-linux-android"
    "armv7a-linux-androideabi"
    "i686-linux-android"
    "x86_64-linux-android"
)

target_abi=(
    "arm64-v8a"
    "armeabi-v7a"
    "x86"
    "x86_64"
)

if [ ! -d "android/build/roc-toolkit" ]
then
    mkdir -p "android/build/roc-toolkit"
    git clone -q --recurse-submodules https://github.com/roc-streaming/roc-toolkit.git \
        "android/build/roc-toolkit"
fi

if [ ! -d "android/build/libroc" ]
then
    mkdir -p "android/build/libroc"
fi

for i in "${!target_host[@]}"; do
    host="${target_host[i]}${API}"
    abi="${target_abi[i]}"

    scons -Q -C android/build/roc-toolkit \
        --compiler=clang \
        --host=$host \
        --incdir="${PWD}"/android/build/libroc/include/$abi \
        --libdir="${PWD}"/android/build/libroc/lib/$abi \
        --mandir="${PWD}"/android/build/libroc/man/$abi \
        --disable-soversion \
        --disable-tools \
        --disable-pulseaudio \
        --disable-sox \
        --disable-openssl \
        --build-3rdparty=libuv,openfec,speexdsp

    scons -Q -C android/build/roc-toolkit \
        --compiler=clang \
        --host=$host \
        --incdir="${PWD}"/android/build/libroc/include/$abi \
        --libdir="${PWD}"/android/build/libroc/lib/$abi \
        --mandir="${PWD}"/android/build/libroc/man/$abi \
        --disable-soversion \
        --disable-tools \
        --disable-pulseaudio \
        --disable-sox \
        --disable-openssl \
        --build-3rdparty=libuv,openfec,speexdsp install
done
