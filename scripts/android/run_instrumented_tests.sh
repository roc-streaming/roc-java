#!/usr/bin/env bash
set -euxo pipefail

export TERM=dumb

adb shell "ip a"
adb shell "ip r"
# add route for multicast traffic
nifaces=( $(adb shell "ip a" | grep 'state UP' | cut -d: -f2 | awk '{print $1}') )
for niface in "${nifaces[@]}"; do
    adb shell "su 0 ip route add 224.0.0.0/4 dev ${niface}" || continue
done

adb shell "ip a"
adb shell "ip r"

cd android

./gradlew cAT --info --stacktrace || \
    { adb logcat -t 500; exit 1; }
