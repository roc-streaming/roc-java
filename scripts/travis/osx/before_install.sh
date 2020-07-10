#!/usr/bin/env bash
set -euxo pipefail

brew unlink python@2
brew list | grep -vE 'pkg-config|automake|libtool|cmake|xz|readline|openssl|sqlite|python' | xargs brew pin

brew install "scons"
brew install "ragel"
brew install "gengetopt"
brew install "libuv"
brew install "sox"
brew install "cpputest"
