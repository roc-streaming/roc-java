#!/usr/bin/env bash

set -euxo pipefail

sudo apt-get update
sudo apt-get install -y g++ \
     pkg-config \
     scons \
     ragel \
     gengetopt \
     libuv1-dev \
     libunwind-dev \
     libpulse-dev \
     libspeexdsp-dev \
     libsox-dev \
     libsndfile1-dev \
     libcpputest-dev \
     libtool \
     intltool \
     autoconf \
     automake \
     make \
     cmake \
     meson \
     ninja-build
