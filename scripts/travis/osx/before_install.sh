#!/usr/bin/env bash
set -euxo pipefail

brew update

brew install "scons"
brew install "ragel"
brew install "gengetopt"
brew install "libuv"
brew install "sox"
brew install "cpputest"
