# JNI bindings for Roc Toolkit

[![build](https://github.com/roc-streaming/roc-java/actions/workflows/build.yaml/badge.svg)](https://github.com/roc-streaming/roc-java/actions/workflows/build.yaml) [![Android release](https://img.shields.io/maven-central/v/org.roc-streaming.roctoolkit/roc-android?color=blue&label=aar)](https://search.maven.org/artifact/org.roc-streaming.roctoolkit/roc-android) [![Android javadoc](https://javadoc.io/badge2/org.roc-streaming.roctoolkit/roc-android/javadoc.svg?color=blue&label=aar-javadoc)](https://javadoc.io/doc/org.roc-streaming.roctoolkit/roc-android) [![Matrix chat](https://matrix.to/img/matrix-badge.svg)](https://app.element.io/#/room/#roc-streaming:matrix.org)

This library provides JNI bindings for [Roc Toolkit](https://github.com/roc-streaming/roc-toolkit), a toolkit for real-time audio streaming over the network.

The bindings can be used in Java, Kotlin, and other JVM-based languages.

Android support included!

## About Roc

Compatible senders and receivers include:

* [command-line tools](https://roc-streaming.org/toolkit/docs/running/command_line_tools.html)
* [PulseAudio modules](https://roc-streaming.org/toolkit/docs/running/pulseaudio_modules.html)
* [C library](https://roc-streaming.org/toolkit/docs/api.html)
* [Go bindings](https://github.com/roc-streaming/roc-go/)

Key features:

* real-time streaming with guaranteed latency;
* restoring lost packets using Forward Erasure Correction codes;
* converting between the sender and receiver clock domains;
* CD-quality audio;
* multiple profiles for different CPU and latency requirements;
* portability;
* relying on open, standard protocols.

## Documentation

Documentation for the Java API generated from javadoc comments can be found on [javadoc.io](https://javadoc.io/doc/org.roc-streaming.roctoolkit/roc-android/latest/index.html).

Documentation for the C API can be found [here](https://roc-streaming.org/toolkit/docs/api.html).

## Versioning

Java bindings and the C library both use [semantic versioning](https://semver.org/).

Rules prior to 1.0.0 release:

* According to semantic versioning, there is no compatibility promise until 1.0.0 is released. Small breaking changes are possible. For convenience, breaking changes are introduced only in minor version updates, but not in patch version updates.

Rules starting from 1.0.0 release:

* The first two components (major and minor) of the bindings and the C library versions correspond to each other. The third component (patch) is indepdendent.

  **Bindings are compatible with the C library if its major version is the same, and minor version is the same or higher.**

  For example, version 1.2.3 of the bindings would be compatible with 1.2.x and 1.3.x, but not with 1.1.x (minor version is lower) or 2.x.x (major version is different).

Note that prebuilt AAR package for Android already ships the right version of libroc, so you don't need to bother with compatibility bewteen bindings and libroc if you're using AAR.

## Using prebuilt AAR for Android

Add mavenCentral repository in `build.gradle` file:

    repositories {
        mavenCentral()
    }

Add dependency to project ([versions](https://search.maven.org/artifact/org.roc-streaming.roctoolkit/roc-android)):

    implementation 'org.roc-streaming.roctoolkit:roc-android:0.0.1'

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
export API=28
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

To clean build results and remove docker container, run this:
```
./scripts/android_docker.sh clean
```

If desired, you can export some variables for Android environment configuration; each variable has default value and is optional:
```
export JAVA_VERSION=8
export API=28
export NDK_VERSION=21.1.6352462
export BUILD_TOOLS_VERSION=28.0.3
export CMAKE_VERSION=3.10.2.4988404
export AVD_IMAGE=default
export AVD_ARCH=x86_64

./scripts/android_docker.sh [build|test]
```

Additional information on the `env-android` docker image, which is used by this script, is available [here](https://github.com/roc-streaming/roc-toolkit/blob/develop/docs/sphinx/development/continuous_integration.rst#android-environment).


#### Device script

There is a helper script named `scripts/android_device.sh` that takes care of creating and booting up AVDs.

It is used in docker and on CI, but you can also use it directly. Supported commands are:

* `create` an AVD:

    ```
    ./scripts/android_device.sh create --api=<API> --image=<IMAGE> --arch=<ARCH> --name=<AVD-NAME>
    ```

    The string ``"system-images;android-<API>;<IMAGE>;<ARCH>"`` defines the emulator system image to be installed (it must be present in the list offered by ``sdkmanager --list``)

* `start` device and wait until boot is completed:

    ```
    ./scripts/android_device.sh start --name=<AVD-NAME>
    ```

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
