#!/bin/bash
set -euxo pipefail

host=$1
abi=$2

scons -Q clean
scons -Q --compiler=clang \
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
