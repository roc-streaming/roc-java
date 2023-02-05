#!/bin/bash

me=$(basename "$0")

api="${API}"
arch="x86_64"

function usage() {
    cat << EOF
Usage: $me [OPTIONS] ACTION

Options:
    --api=API        android API for emulator
    --arch=ARCH      android ARCH for emulator
    --image=IMAGE    android system image for emulator
    --name=NAME      android device name
    --help           show this help and exit

Actions:
    create           create a new android device
    start            boot on android device
EOF
}

function arg_error() {
    echo "$me: unrecognized option '$1'"
    echo "Try '$me --help' for more information."
}

function run_emulator() {
    if [ "${CI:-}" = "true" -o "${CI:-}" = "1" ] && [[ "$OSTYPE" == darwin* ]]; then
        (cd "$ANDROID_SDK_ROOT/tools" && ./emulator "$@")
    else
        emulator "$@"
    fi
}

function create_device() {
    if [ "${image-}" = "" ]; then
        echo "no 'image' specified" >&2
        echo "Try '$me --help' for more information." >&2
        exit 1
    fi
    if [ "${name-}" = "" ]; then
        echo "no 'name' specified" >&2
        echo "Try '$me --help' for more information." >&2
        exit 1
    fi
    package="system-images;android-${api};${image};${arch}"
    if [ ! -d ${ANDROID_SDK_ROOT}/system-images/android-${api}/${image}/${arch} ]; then
        echo "--- installing system image android-${api};${image};${arch}"
        ( yes | sdkmanager $package > /dev/null ) || exit 1
    fi
    echo "creating device \"${name}\", package \"${package}\""
    echo no | avdmanager create avd --name "${name}" --package $package > /dev/null
}

function start_device() {
    if [ "${name-}" = "" ]; then
        echo "no 'name' specified" >&2
        echo "Try '$me --help' for more information." >&2
        exit 1
    fi
    adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill &> /dev/null; done
    echo "starting device \"${name}\""
    flags="-no-audio -no-boot-anim -no-window -gpu off"
    # check if hardware acceleration is available
    run_emulator -accel-check | grep -q 'is installed and usable' && \
        flags="${flags} -accel on" || \
        flags="${flags} -accel off"
    run_emulator -avd "${name}" $flags &> /dev/null &
    boot_completed="0"
    while [ "$boot_completed" != "1" ]; do
        boot_completed=$(adb wait-for-device shell getprop sys.boot_completed | tr -d '\r')
        sleep 1
    done
    echo "device \"${name}\" started"
}

# read arguments
optspec=":h-:"
while getopts "$optspec" optchar; do
    case "${optchar}" in
        -)
            case "${OPTARG}" in
                api)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    api="$val"
                    ;;
                arch)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    arch="$val"
                    ;;
                image)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    image="$val"
                    ;;
                name)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    name="$val"
                    ;;
                help)
                    usage
                    exit 0
                    ;;
                *)
                    arg_error "--${OPTARG}" >&2
                    exit 1
                    ;;
            esac;;
        h)
            usage
            exit 0
            ;;
        *)
            arg_error "-${OPTARG}" >&2
            exit 1
            ;;
    esac
done

shift $(($OPTIND - 1))
action=$1

case "$action" in
    create)
        create_device
        ;;
    start)
        start_device
        ;;
    *)
        echo "unrecognized action ${action}" >&2
        usage
        exit 1
        ;;
esac
