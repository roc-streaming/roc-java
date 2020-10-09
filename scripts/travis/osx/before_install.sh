#!/usr/bin/env bash
set -euxo pipefail

if [ "${JAVA_VERSION-}" = "8" ]; then
    brew cask uninstall --force java
    brew tap adoptopenjdk/openjdk
    brew cask install adoptopenjdk/openjdk/adoptopenjdk8
fi

brew unlink python@2
brew list | grep -vE 'pkg-config|automake|libtool|cmake|xz|readline|openssl|sqlite|python|gdbm' | xargs brew pin

brew install "scons"
brew install "ragel"
brew install "gengetopt"
brew install "libuv"
brew install "sox"
brew install "cpputest"
brew install "ninja"
