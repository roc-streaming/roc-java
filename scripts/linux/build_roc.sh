#!/usr/bin/env bash
set -euxo pipefail

git clone --recurse-submodules https://github.com/roc-streaming/roc-toolkit.git /tmp/roc
work_dir=$(pwd)
cd /tmp/roc
git checkout -q 863a0227b78464c3a56fc0484bec73c891e4b7a8
git submodule update --init --recursive
cd $work_dir

scons -C /tmp/roc -Q --compiler=gcc --build-3rdparty=openfec
sudo scons -C /tmp/roc -Q --compiler=gcc --build-3rdparty=openfec install
