#!/usr/bin/env bash

set -euo pipefail

function run_cmd() {
    echo "+++ $*"
    "$@" || exit 1
}

# default values must be in-sync with android/roc-android/build.gradle
: "${ROC_REVISION:=master}"
: "${JAVA_VERSION:=17}"
: "${SDK_LEVEL:=31}"
: "${API_LEVEL:=29}"
: "${NDK_VERSION:=26.3.11579264}"
: "${BUILD_TOOLS_VERSION:=35.0.0}"
: "${CMAKE_VERSION:=3.18.1}"
: "${AVD_IMAGE:=default}"
: "${AVD_ARCH:=x86_64}"
: "${OSSRH_USERNAME:=}"
: "${OSSRH_PASSWORD:=}"
: "${SIGNING_KEY_ID:=}"
: "${SIGNING_PASSWORD:=}"
: "${SIGNING_KEY:=}"

# go to project root
cd "$(dirname "$0")"/..

# parse arguments
action="${1:-}"
case "${action}" in
    build|test|publish|clean|purge)
        ;;
    *)
        echo "usage: $(basename $0) build|test|publish|clean|purge" >&2
        exit 1
        ;;
esac

# remove docker stuff we've created
if [[ "${action}" = purge ]]
then
    if docker ps -a --format '{{.Names}}' | grep -qF roc_android
    then
        run_cmd docker rm -f roc_android
    fi

    if docker volume ls --format '{{.Name}}' | grep -qF roc_android_sdk
    then
        run_cmd docker volume rm roc_android_sdk
    fi
fi

# remove build artifcats
if [[ "${action}" = purge || "${action}" = clean ]]
then
    run_cmd rm -rf android/build
    run_cmd rm -rf android/roc-android/build
    run_cmd rm -rf android/roc-android/.cxx

    exit
fi

# if container exists, but was mounted to different location, remove it
if docker ps -a --format '{{.Names}}' | grep -q roc_android
then
    mount_point="$(docker container inspect \
        --format '{{ range .Mounts }}{{ .Destination }}:{{ end }}' roc_android \
            | tr ':' '\n' | grep -E . | grep -vE '^/sdk|\.gradle$' | head -1)"

    if [ "$mount_point" != "${PWD}" ]
    then
        run_cmd docker rm -f roc_android
    fi
fi

# if container exists, but was based on different image, remove it
if docker ps -a --format '{{.Names}}' | grep -q roc_android
then
    image="$(docker container inspect --format '{{.Config.Image}}' roc_android)"

    if [ "$image" != "rocstreaming/env-android:jdk${JAVA_VERSION}" ]
    then
        run_cmd docker rm -f roc_android
        run_cmd rm -rf android/build
        run_cmd rm -rf android/roc-android/.cxx
    fi
fi

# if container exists, but was started with different parameters, remove it
if docker ps -a --format '{{.Names}}' | grep -q roc_android
then
    for var in SDK_LEVEL API_LEVEL BUILD_TOOLS_VERSION NDK_VERSION CMAKE_VERSION AVD_IMAGE AVD_ARCH
    do
        expected="${!var}"
        actual="$(docker inspect --format '{{range .Config.Env}}{{println .}}{{end}}' roc_android \
             | grep "^${var}=" | cut -d= -f2 || true)"

        if [ "$expected" != "$actual" ]
        then
            run_cmd docker rm -f roc_android
            run_cmd rm -rf android/build
            run_cmd rm -rf android/roc-android/.cxx
            break
        fi
    done
fi

# if container does not exist, create it
if ! docker ps -a --format '{{.Names}}' | grep -q roc_android
then
    mkdir -p android/.gradle

    docker_args=(
        --env CI="${CI:-false}"
        --env ROC_REVISION="${ROC_REVISION}"
        --env SDK_LEVEL="${SDK_LEVEL}"
        --env API_LEVEL="${API_LEVEL}"
        --env NDK_VERSION="${NDK_VERSION}"
        --env BUILD_TOOLS_VERSION="${BUILD_TOOLS_VERSION}"
        --env CMAKE_VERSION="${CMAKE_VERSION}"
        --env AVD_IMAGE="${AVD_IMAGE}"
        --env AVD_ARCH="${AVD_ARCH}"
        --env OSSRH_USERNAME="${OSSRH_USERNAME}"
        --env OSSRH_PASSWORD="${OSSRH_PASSWORD}"
        --env SIGNING_KEY_ID="${SIGNING_KEY_ID}"
        --env SIGNING_PASSWORD="${SIGNING_PASSWORD}"
        --env SIGNING_KEY="${SIGNING_KEY}"
        -v "${PWD}:${PWD}"
        -v "${PWD}/android/.gradle:/root/.gradle"
        -v roc_android_sdk:/sdk
        -w "${PWD}"
    )

    # for hardware acceleration in emulator
    if [ -e "/dev/kvm" ]
    then
        docker_args+=( --privileged --device /dev/kvm )
    fi

    # start container in background mode, ignore its entrypoint
    run_cmd docker run --name roc_android \
            --net host \
            -d --entrypoint "" \
            "${docker_args[@]}" \
           rocstreaming/env-android:jdk"${JAVA_VERSION}" \
           sleep infinity

    # explicitly execute and wait entrypoint, which installs Android components
    run_cmd docker exec roc_android \
            /opt/entrypoint.sh true

    # add user with the same uid as on host
    run_cmd docker exec roc_android \
            useradd -ms /bin/bash -u${UID} user
fi

# if container is not running, start it
if [ "$(docker inspect -f '{{.State.Running}}' roc_android)" != "true" ]
then
    run_cmd docker start roc_android
fi

# if libroc wasn't built yet, build it
if [ ! -e "android/build/libroc.done" ]
then
    run_cmd docker exec roc_android su -Ppc scripts/android/build_roc.sh user
    touch "android/build/libroc.done"
fi

# build bindings and AAR
run_cmd docker exec roc_android su -Ppc scripts/android/build_bindings.sh user

# run tests on emulator
if [[ "${action}" = test ]]
then
    run_cmd docker exec roc_android scripts/android/start_emulator.sh
    run_cmd docker exec roc_android su -Ppc scripts/android/run_instrumented_tests.sh user
    exit
fi

# publish artifacts to artifactory
if [[ "${action}" = publish ]]
then
    run_cmd docker exec roc_android su -Ppc scripts/android/publish.sh user
    exit
fi
