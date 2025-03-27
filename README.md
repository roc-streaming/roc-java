# JNI bindings for Roc Toolkit

[![build](https://github.com/roc-streaming/roc-java/actions/workflows/build.yaml/badge.svg)](https://github.com/roc-streaming/roc-java/actions/workflows/build.yaml) [![codecov](https://codecov.io/gh/roc-streaming/roc-java/branch/main/graph/badge.svg?token=BOP1QUSX64)](https://codecov.io/gh/roc-streaming/roc-java) [![Android release](https://img.shields.io/maven-central/v/org.roc-streaming.roctoolkit/roc-android?color=blue&label=aar)](https://search.maven.org/artifact/org.roc-streaming.roctoolkit/roc-android) [![Android javadoc](https://javadoc.io/badge2/org.roc-streaming.roctoolkit/roc-android/javadoc.svg?color=blue&label=aar-javadoc)](https://javadoc.io/doc/org.roc-streaming.roctoolkit/roc-android) [![Matrix chat](https://matrix.to/img/matrix-badge.svg)](https://app.element.io/#/room/#roc-streaming:matrix.org)

This library provides JNI bindings for [Roc Toolkit](https://github.com/roc-streaming/roc-toolkit), a toolkit for real-time audio streaming over the network.

The bindings can be used in Java, Kotlin, and other JVM-based languages.

Android support included!

## About Roc

Key features of Roc Toolkit:

* real-time streaming with guaranteed latency;
* robust work on unreliable networks like Wi-Fi, due to use of Forward Erasure Correction codes;
* CD-quality audio;
* multiple profiles for different CPU and latency requirements;
* relying on open, standard protocols, like RTP and FECFRAME;
* interoperability with both Roc and third-party software.

Compatible Roc Toolkit senders and receivers include:

* [cross-platform command-line tools](https://roc-streaming.org/toolkit/docs/tools/command_line_tools.html)
* [modules for sound servers](https://roc-streaming.org/toolkit/docs/tools/sound_server_modules.html) (PipeWire, PulseAudio, macOS CoreAudio)
* [C library](https://roc-streaming.org/toolkit/docs/api.html) and [bindings for other languages](https://roc-streaming.org/toolkit/docs/api/bindings.html)
* [applications](https://roc-streaming.org/toolkit/docs/tools/applications.html) (Android)

## Documentation

Documentation for the Java API generated from javadoc comments can be found on [javadoc.io](https://javadoc.io/doc/org.roc-streaming.roctoolkit/roc-android/latest/index.html).

Documentation for the C API can be found [here](https://roc-streaming.org/toolkit/docs/api.html).

## Quick start

#### Sender

```java
import org.rocstreaming.roctoolkit;

try (RocContext context = new RocContext()) {
    RocSenderConfig config = RocSenderConfig.builder()
        .frameSampleRate(44100)
        .frameChannels(ChannelSet.STEREO)
        .frameEncoding(FrameEncoding.PCM_FLOAT)
        .fecEncoding(FecEncoding.RS8M)
        .clockSource(ClockSource.INTERNAL)
        .build();

    try (RocSender sender = new RocSender(context, config)) {
        Endpoint sourceEndpoint = new Endpoint("rtp+rs8m://192.168.0.1:10001");
        Endpoint repairEndpoint = new Endpoint("rs8m://192.168.0.1:10002");

        sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, sourceEndpoint);
        sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, repairEndpoint);

        while (/* not stopped */) {
            float[] samples = /* generate samples */

            sender.write(samples);
        }
    }
}
```

#### Receiver

```java
import org.rocstreaming.roctoolkit;

try (RocContext context = new RocContext()) {
    RocReceiverConfig config = RocReceiverConfig.builder()
        .frameSampleRate(44100)
        .frameChannels(ChannelSet.STEREO)
        .frameEncoding(FrameEncoding.PCM_FLOAT)
        .clockSource(ClockSource.INTERNAL)
        .build();

    try (RocReceiver receiver = new RocReceiver(context, config)) {
        Endpoint sourceEndpoint = new Endpoint("rtp+rs8m://0.0.0.0:10001");
        Endpoint repairEndpoint = new Endpoint("rs8m://0.0.0.0:10001");

        receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, sourceEndpoint);
        receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, repairEndpoint);

        while (/* not stopped */) {
            float[] samples = new float[320];
            receiver.read(samples);

            /* process received samples */
        }
    }
}
```

## Bindings version

Java bindings and the C library both use [semantic versioning](https://semver.org/).

Bindings are **compatible** with the C library when:

* **major** version of bindings **is same** as major version of C library
* **minor** version of bindings **is same or higher** as minor version of C library

Patch versions of bindings and C library are independent.

For example, version 1.2.3 of the bindings would be compatible with 1.2.x and 1.3.x, but not with 1.1.x (minor version is lower) or 2.x.x (major version is different).

Note that prebuilt AAR package for Android already ships the right version of libroc, so you don't need to bother with compatibility between bindings and libroc if you're using AAR.

## Java and Android versions

Minimum Java version:

* on build machine: JDK 17
* on target machine: JRE 8 (== Java 1.8)

Minimum Android SDK version:

* on build machine: API level 31 (Android 12 SDK)
* on target machine: API level 29 (Android 10 runtime)

Minimum Android NDK version:

* on build machine: NDK r21e

## Use prebuilt AAR for Android

Add mavenCentral repository in `build.gradle` file:
```
repositories {
    mavenCentral()
}
```

Add dependency to project ([versions](https://search.maven.org/artifact/org.roc-streaming.roctoolkit/roc-android)):
```
implementation 'org.roc-streaming.roctoolkit:roc-android:<VERSION>'
```

## Build JAR for desktop

Install JDK 17 or higher.

Follow [official instructions](https://roc-streaming.org/toolkit/docs/building.html) to install libroc system-wide. Take care to pick the right version as described above.

Then run:
```
./gradlew build
```

If libroc is not in default path you can specify `ROC_INCLUDE_PATH` (path to roc headers) and `ROC_LIBRARY_PATH` (path to roc library) variables with:
- environment variables
- gradle system variables

Additional compilation and linking flags can be specified respectively with `CFLAGS` and `LDFLAGS` gradle system variables.

JAR is located at `build/libs`. It expected that `libroc` shared library is present on system.

## Build AAR for Android (docker way)

Install Docker.

Optionally, export environment variables:
```
export ROC_REVISION=master
export SDK_LEVEL=31
export API_LEVEL=29
export NDK_VERSION=26.3.11579264
export BUILD_TOOLS_VERSION=35.0.0
export CMAKE_VERSION=3.18.1
```

This will pull docker image, install dependencies inside container, and run build:
```
./scripts/android_docker.sh build
```

AAR is located at `android/roc-android/build/outputs/aar`. It contains `libroc` built for all Android architectures.

## Build AAR for Android (manual way)

Install JDK 17 or higher and Android SDK 31 or higher.

Optionally, export environment variables:
```
export ROC_REVISION=master
export SDK_LEVEL=31
export API_LEVEL=29
export NDK_VERSION=26.3.11579264
export BUILD_TOOLS_VERSION=35.0.0
export CMAKE_VERSION=3.18.1
```

Install Android components:
```
sdkmanager "platforms;android-${SDK_LEVEL}"
sdkmanager "build-tools;${BUILD_TOOLS_VERSION}"
sdkmanager "ndk;${NDK_VERSION}"
sdkmanager "cmake;${CMAKE_VERSION}"
```

Install build-time dependencies of Roc Toolkit, e.g. on macOS run:
```
brew install scons ragel gengetopt
```

Download and build Roc Toolkit:
```
./scripts/android/build_roc.sh
```

And finally build bindings and package everything into AAR:
```
cd android
./gradlew build
```

AAR is located at `android/roc-android/build/outputs/aar`. It contains `libroc` built for all Android architectures.

## Android build variables

These environment variables are used when building for Android, with or without Docker. Each variable has default value and is optional.

When docker is used, corresponding components (SDK, NDK, etc) will be installed automatically according to the specified variables. When docker is not used, you should install them manually before build.

In both cases, export this variables before invoking `android_docker.sh` or `gradlew`.

| Variable              | Description                                         |
|-----------------------|-----------------------------------------------------|
| `JAVA_VERSION`        | Which JDK to pull (when using docker)               |
| `ROC_REVISION`        | Which Roc Toolkit to build (when using docker)      |
| `ROC_DIR`             | Which Roc Toolkit to use (when not using docker)    |
| `SDK_LEVEL`           | `android-platform` version and `compileSdkVersion`  |
| `API_LEVEL`           | `minSdkVersion` and `targetSdkVersion`              |
| `NDK_VERSION`         | Which `ndk` to install/use                          |
| `BUILD_TOOLS_VERSION` | Which `build-tools` to install/use                  |
| `CMAKE_VERSION`       | Which `cmake` to install/use                        |
| `AVD_IMAGE`           | Which image to use for emulator (when using docker) |
| `AVD_ARCH`            | Which arch to use for emulator (when using docker)  |

## Hacking

Contributions are always welcome! You can find issues needing help using [help wanted](https://github.com/roc-streaming/roc-vad/labels/help%20wanted) and [good first issue](https://github.com/roc-streaming/roc-vad/labels/good%20first%20issue) labels.

See [HACKING.md](HACKING.md) for details about the project internals.

## Authors

You can find list of authors and contributors [here](AUTHORS.md). Feel free to send a pull request if you're missing from the list or want to change your appearance.

For Roc Toolkit authors, see [here](https://roc-streaming.org/toolkit/docs/about_project/authors.html).

## License

Bindings are licensed under [MIT](LICENSE).

For details on Roc Toolkit licensing, see [here](https://roc-streaming.org/toolkit/docs/about_project/licensing.html).
