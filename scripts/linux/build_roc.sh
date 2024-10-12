#!/usr/bin/env bash

set -euxo pipefail

git clone --recurse-submodules -b ${ROC_REVISION:-master} \
    https://github.com/roc-streaming/roc-toolkit.git /tmp/roc

scons -C /tmp/roc -Q --compiler=gcc --build-3rdparty=openfec
sudo scons -C /tmp/roc -Q --compiler=gcc --build-3rdparty=openfec install
