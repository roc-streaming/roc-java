#!/bin/bash
set -euxo pipefail

host=$1
abi=$2

scons -Q clean

# remove soversion from soname (for gradle android plugin)
LDFLAGS="-Wl,-soname,libroc.so" scons -Q --compiler=clang \
    --host=$host \
    --libdir=${ROC_BASE_DIR}/lib/$abi \
    --incdir=${ROC_BASE_DIR}/include/$abi \
    --disable-tools \
    --disable-examples \
    --disable-tests \
    --disable-pulseaudio \
    --disable-sox \
    --build-3rdparty=libuv,openfec

scons -Q --compiler=clang \
    --host=$host \
    --libdir=${ROC_BASE_DIR}/lib/$abi \
    --incdir=${ROC_BASE_DIR}/include/$abi \
    --disable-tools \
    --disable-examples \
    --disable-tests \
    --disable-pulseaudio \
    --disable-sox \
    --build-3rdparty=libuv,openfec install

# remove symlink from libroc.so (let gradle android plugin packs full library instead of symlink)
cd ${ROC_BASE_DIR}/lib/$abi && \
    target=$(readlink -f libroc.so) && \
    rm libroc.so && \
    cp $target libroc.so
