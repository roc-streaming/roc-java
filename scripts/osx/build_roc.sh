#!/usr/bin/env bash
set -euxo pipefail

git clone --recurse-submodules https://github.com/roc-streaming/roc-toolkit.git -b develop  /tmp/roc
#work_dir=$(pwd)
#cd /tmp/roc
#git checkout -q 863a0227b78464c3a56fc0484bec73c891e4b7a8
#git submodule update --init --recursive
#cd $work_dir

scons -C /tmp/roc -Q --build-3rdparty=openfec
sudo scons -C /tmp/roc -Q --build-3rdparty=openfec install
