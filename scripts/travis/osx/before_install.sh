#!/usr/bin/env bash
set -euxo pipefail

brew update

brew tap AdoptOpenJDK/openjdk
brew install --cask adoptopenjdk${JAVA_VERSION}

brew install "scons"
brew install "ragel"
brew install "gengetopt"
brew install "libuv"
brew install "sox"
brew install "cpputest"
brew install "ninja"
