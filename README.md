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
* [modules for sound servers](https://roc-streaming.org/toolkit/docs/tools/sound_server_modules.html) (PulseAudio, PipeWire)
* [C library](https://roc-streaming.org/toolkit/docs/api.html) and [bindings for other languages](https://roc-streaming.org/toolkit/docs/api/bindings.html)
* [end-user apps](https://roc-streaming.org/toolkit/docs/tools/applications.html)

## Documentation

Documentation for the Java API generated from javadoc comments can be found on [javadoc.io](https://javadoc.io/doc/org.roc-streaming.roctoolkit/roc-android/latest/index.html).

Documentation for the C API can be found [here](https://roc-streaming.org/toolkit/docs/api.html).

## Quick start

#### Sender

```java
import org.rocstreaming.roctoolkit;

try (RocContext context = new RocContext()) {
    RocSenderConfig config = RocSenderConfig.builder()
        .frameSampleRate(SAMPLE_RATE)
        .frameChannels(ChannelSet.STEREO)
        .frameEncoding(FrameEncoding.PCM_FLOAT)
        .resamplerProfile(ResamplerProfile.DISABLE)
        .fecEncoding(FecEncoding.RS8M)
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
        .frameSampleRate(SAMPLE_RATE)
        .frameChannels(ChannelSet.STEREO)
        .frameEncoding(FrameEncoding.PCM_FLOAT)
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

## Versioning

Java bindings and the C library both use [semantic versioning](https://semver.org/).

Bindings are **compatible** with the C library when:

* **major** version of bindings **is same** as major version of C library
* **minor** version of bindings **is same or higher** as minor version of C library

Patch versions of bindings and C library are independent.

For example, version 1.2.3 of the bindings would be compatible with 1.2.x and 1.3.x, but not with 1.1.x (minor version is lower) or 2.x.x (major version is different).

Note that prebuilt AAR package for Android already ships the right version of libroc, so you don't need to bother with compatibility bewteen bindings and libroc if you're using AAR.

## Using prebuilt AAR for Android

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

## Building JAR from sources

First, follow [official instructions](https://roc-streaming.org/toolkit/docs/building.html) to install libroc system-wide. Take care to pick the right version as described above.

Then run:
```
./gradlew build
```

## Building AAR from sources (docker way)

This will install dependencies inside docker and run build:
```
./scripts/android_docker.sh build
```

This will start emulator inside docker and run tests on it:
```
./scripts/android_docker.sh test
```

## Building AAR from sources (manual way)

First, export required environment variables:
```
export API=26
export NDK_VERSION=21.1.6352462
export BUILD_TOOLS_VERSION=28.0.3
export CMAKE_VERSION=3.10.2.4988404
```

Then install Android components:
```
sdkmanager "platforms;android-${API}"
sdkmanager "build-tools;${BUILD_TOOLS_VERSION}"
sdkmanager "ndk;${NDK_VERSION}"
sdkmanager "cmake;${CMAKE_VERSION}"
```

Also install build-time dependencies of Roc Toolkit, e.g. on macOS run:
```
brew install scons ragel gengetopt
```

Now we can download and build Roc Toolkit:
```
./scripts/android/build_roc.sh
```

And finally build bindings and package everything into AAR:
```
cd android
./gradlew build
```

Optionally, run tests on device or emulator (you'll have to create one):
```
cd android
./gradlew cAT --info --stacktrace
```

## Developer instructions

#### Local build

Build (native code and Java code):
```
./gradlew build
```

Build only native code:
```
./gradlew roc_jni:build
```

Run tests:
```
./gradlew test
```

If libroc is not in default path you can specify `ROC_INCLUDE_PATH` (path to roc headers) and `ROC_LIBRARY_PATH` (path to roc library) variables with:
- environment variables
- gradle system variables

Additional compilation and linking flags can be specified respectively with `CFLAGS` and `LDFLAGS` gradle system variables

#### Android build via docker

This command will pull docker image, install Android SDK and NDK inside it, download and build Roc Toolkit, build JNI bindings, and package everything into AAR:
```
./scripts/android_docker.sh build
```

To run instrumented tests in Android emulator inside docker image, use this:
```
./scripts/android_docker.sh test
```

To remove build results, run:
```
./scripts/android_docker.sh clean
```

To remove build results and docker container, run:
```
./scripts/android_docker.sh purge
```

If desired, you can export some variables for Android environment configuration; each variable has default value and is optional:
```
export JAVA_VERSION=8
export API=26
export NDK_VERSION=21.1.6352462
export BUILD_TOOLS_VERSION=28.0.3
export CMAKE_VERSION=3.10.2.4988404
export AVD_IMAGE=default
export AVD_ARCH=x86_64

./scripts/android_docker.sh [build|test]
```

Additional information on the `env-android` docker image, which is used by this script, is available [here](https://roc-streaming.org/toolkit/docs/development/continuous_integration.html#android-environment).

#### Documentation build

Generate docs:
```
./gradlew javadoc
```

#### Publishing Android release

Release workflow:
 * make github release with tag version, e.g. `v0.1.0`
 * GitHub Actions will run release step and publish artifacts to artifactory

Followed env variables should be set in GitHub Actions:
 * `OSSRH_USERNAME` - Sonatype OSSRH user
 * `OSSRH_PASSWORD` - Sonatype OSSRH password
 * `SIGNING_KEY_ID` - gpg key id
 * `SIGNING_PASSWORD` - gpg passphrase
 * `SIGNING_KEY` - gpg private key

## Authors

See [here](https://github.com/roc-streaming/roc-java/graphs/contributors).

## License

Bindings are licensed under [MIT](LICENSE).

For details on Roc Toolkit licensing, see [here](https://roc-streaming.org/toolkit/docs/about_project/licensing.html).
