#!/usr/bin/env bash
set -euxo pipefail

export TERM=dumb

# add route for multicast traffic
nifaces=( $(adb shell "ip a" | grep -Eo "[0-9]+: ([0-9a-f]+).*state UP") )
for niface in "${nifaces[@]}"; do
    adb shell "su 0 ip route add 224.0.0.0/4 dev ${niface} table local" || continue
done

cd android

./gradlew cAT --info --stacktrace || \
    { adb logcat -t 500; exit 1; }
