#!/usr/bin/env bash
set -euxo pipefail

git clone https://github.com/roc-project/roc.git /tmp/roc
scons -C /tmp/roc -Q --compiler=gcc --build-3rdparty=openfec
sudo scons -C /tmp/roc -Q --compiler=gcc --build-3rdparty=openfec install