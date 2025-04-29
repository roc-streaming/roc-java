# Developer instructions

## Desktop gradle targets

Build JAR:
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

Generate documentation:
```
./gradlew javadoc
```

Format Java code:
```
./gradlew spotlessApply
```

Format C code:
```
./gradlew clangFormat
```

## Android gradle targets

Build AAR:
```
cd android
./gradlew build
```

Run tests on device or emulator (you'll have to create one):
```
cd android
./gradlew cAT --info --stacktrace
```

## Android docker commands

This command will pull docker image, install Android SDK and NDK inside it, download and build Roc Toolkit, build JNI bindings, and package everything into AAR:
```
./scripts/android_docker.sh build
```

To run instrumented tests in Android emulator (it will automatically create emulator inside docker container):
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

(Normally, docker container remains running in the background with a Gradle daemon).

Additional information on the `env-android` docker image, which is used by this script, is available [here](https://roc-streaming.org/toolkit/docs/portability/android_environment.html).

## Compatibility settings in gradle

These settings define requirements for build and target machines:

* `gradle/wrapper/gradle-wrapper.properties`

    * `distributionUrl` - Gradle version (places limits on both build and target)

* `build.gradle`, `commons/build.gradle`, `cmake-library/build.gradle`

    * `sourceCompatibility` - Minimum build-time Java version (Desktop)
    * `targetCompatibility` - Minimum run-time Java version (Desktop)

* `android/build.gradle`

    * `com.android.tools.build:gradle` - Android Gradle Plugin version (must be compatible with gradle)

* `android/roc-android/build.gradle`

    * `sourceCompatibility` - Minimum build-time Java version (Android)
    * `targetCompatibility` - Minimum run-time Java version (Android)
    * `compileSdkVersion` - Build-time Android version
    * `minSdkVersion` - Minimum run-time Android version
    * `targetSdkVersion` - Tested run-time Android version

## Publishing Android release

Release workflow:
 * make github release with tag version, e.g. `v1.2.3`
 * GitHub Actions will run release step and publish artifacts to artifactory

Followed env variables should be set in GitHub Actions:
 * `OSSRH_USERNAME` - Sonatype OSSRH user
 * `OSSRH_PASSWORD` - Sonatype OSSRH password
 * `SIGNING_KEY_ID` - gpg key id
 * `SIGNING_PASSWORD` - gpg passphrase
 * `SIGNING_KEY` - gpg private key
