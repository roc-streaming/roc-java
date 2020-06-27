#!/usr/bin/env bash
set -euxo pipefail

git clone https://github.com/roc-streaming/roc-toolkit.git /tmp/roc
scons -C /tmp/roc -Q --build-3rdparty=openfec
sudo scons -C /tmp/roc -Q --build-3rdparty=openfec install
