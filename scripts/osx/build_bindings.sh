#!/usr/bin/env bash

set -euxo pipefail

./gradlew build -Dgenerator=ninja
