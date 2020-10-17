# JNI bindings for Roc Toolkit

[![Build Status](https://travis-ci.org/roc-streaming/roc-java.svg?branch=master)](https://travis-ci.org/roc-streaming/roc-java)
[![Android release](https://img.shields.io/bintray/v/roc-streaming/maven/roc-android?color=blue&label=aar)](https://bintray.com/roc-streaming/maven/roc-android/_latestVersion)

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

Documentation for the Java API is not available online yet. You can find it in the source code as javadoc comments.

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

Add jcenter repository in `build.gradle` file:

    repositories {
        jcenter()
    }

Add dependency to project ([last version](https://bintray.com/roc-streaming/maven/roc-android/_latestVersion)):

    implementation 'org.rocstreaming.roctoolkit:roc-android:0.0.1'

## Building JAR from sources

First, follow [official instructions](https://roc-streaming.org/toolkit/docs/building.html) to install libroc system-wide. Take care to pick the right version as described above.

Then run:
```
./gradlew build
```

## Building AAR from sources

Export some variables for Android environment configuration, for example:
```
export JAVA_VERSION=8
export ANDROID_API=28
export ANDROID_BUILD_TOOLS_VERSION=28.0.3
export ANDROID_NDK_VERSION=21.1.6352462
export ROC_BASE_DIR=/tmp/roc-build   # libroc prefix destination path
```

Build libroc for all Android ABIs with:
```
scripts/travis/android/install.sh
```

Build Android subproject and run instrumented tests:
```
scripts/travis/android/script.sh
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

#### Android build

First follow instructions from `Building AAR from sources` section above.

The last step will run a fresh docker container and a new AVD at each execution. When it's already done first time, and you only need to build and test Android subproject, you can just run `/bin/bash` on `rocstreaming/env-android:jdk$JAVA_VERSION` Docker image:

    docker run -it --rm --privileged --env API=$ANDROID_API \
        --env BUILD_TOOLS_VERSION=$ANDROID_BUILD_TOOLS_VERSION \
        --env NDK_VERSION=$ANDROID_NDK_VERSION \
        --env ROC_BASE_DIR=$ROC_BASE_DIR \
        -v $PWD:$PWD -v $ROC_BASE_DIR:$ROC_BASE_DIR \
        -v android-sdk:/sdk -w $PWD \
            rocstreaming/env-android:jdk$JAVA_VERSION /bin/bash

Inside Docker container bash session you create an AVD:

    device --name "roc_device" --image "default" --api "${API}" create
    device --name "roc_device" start

and build and test roc-android:

    cd android
    ./gradlew build
    ./gradlew cAT --info --stacktrace

Additional information on our `env-android` Docker image is available [here](https://github.com/roc-streaming/roc-toolkit/blob/develop/docs/sphinx/development/continuous_integration.rst#android-environment).

#### Documentation build

Generate docs:
```
./gradlew javadoc
```

#### Configuration (building native code)

If libroc is not in default path you can specify `ROC_INCLUDE_PATH` (path to roc headers) and `ROC_LIBRARY_PATH` (path to roc library) variables with:
- environment variables
- gradle system variables

Additional compilation and linking flags can be specified respectively with `CFLAGS` and `LDFLAGS` gradle system variables

#### Android release

Release workflow:
 * make github release with tag version, e.g. `v0.1.0`
 * travis will run release stage and publish artifacts to bintray

Followed env variables should be set in travis:
 * `BINTRAY_USER` - bintray user
 * `BINTRAY_KEY` - bintray user api key
 * `BINTRAY_REPO` - bintray repository name
 * `BINTRAY_REPO_ORG` - bintray organization name

## Authors

See [here](https://github.com/roc-streaming/roc-java/graphs/contributors).

## License

Bindings are licensed under [MIT](LICENSE).

For details on Roc Toolkit licensing, see [here](https://roc-streaming.org/toolkit/docs/about_project/licensing.html).
