FROM debian:stable

ENV DEBIAN_FRONTEND noninteractive

ARG NDK="21"
ARG API="28"

RUN apt-get update

# native compilers
RUN apt-get install -y g++

# tools
RUN apt-get install -y \
    scons \
    ragel \
    gengetopt \
    python \
    unzip \
    wget

# Roc dependencies
RUN apt-get install -y \
    libtool \
    intltool \
    autoconf \
    automake \
    automake-1.15 \
    pkg-config \
    make \
    cmake

# toolchain
RUN wget -q -O /tmp/ndk.zip \
    "https://dl.google.com/android/repository/android-ndk-r${NDK}-linux-x86_64.zip" && \
    ( cd /tmp && unzip -qq ndk.zip -d /opt) && \
    rm /tmp/ndk.zip
    
# for roc build
# use bash instead of default sh
RUN ["/bin/bash", "-c", "cd /opt/android-ndk-r${NDK}/toolchains/llvm/prebuilt/linux-x86_64/bin && \
    for f in *androideabi*; do ln -s $f ${f/androideabi/android}; done && \
    for f in *$API-clang; do ln -s $f ${f/$API/} && ln -s $f ${f/$API-clang/-gcc}; done && \
    for f in *$API-clang++; do ln -s $f ${f/$API/}; done && \
    ln -s armv7a-linux-android-clang arm-linux-android-clang && \
    ln -s armv7a-linux-android-clang++ arm-linux-android-clang++ && \
    ln -s armv7a-linux-android-gcc arm-linux-android-gcc"]

# path
ENV PATH="/opt/android-ndk-r${NDK}/toolchains/llvm/prebuilt/linux-x86_64/bin:${PATH}"
