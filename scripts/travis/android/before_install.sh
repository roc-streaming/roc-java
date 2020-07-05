#!/usr/bin/env bash
set -euxo pipefail

docker pull rocstreaming/env-android:jdk$JAVA_VERSION
