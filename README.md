# JNI bindings for Roc Toolkit

[![Build Status](https://travis-ci.org/roc-streaming/roc-java.svg?branch=master)](https://travis-ci.org/roc-streaming/roc-java)

_Work in progress!_

## Dependencies

You will need to have libroc and libroc-devel (headers) installed. Refer to official build [instructions](https://roc-streaming.org/toolkit/docs/building.html) on how to install libroc. There is no official distribution for any OS as of now, you will need to install from source.

## Development

Generate JNI headers:
```
./gradlew generateHeaders
```

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

Generate docs:
```
./gradlew javadoc
```

#### Configuration (building native code)

If libroc is not in default path you can specify `ROC_INCLUDE_PATH` (path to roc headers) and `ROC_LIBRARY_PATH` (path to roc library) variables with:
- environment variables 
- gradle system variables

Additional compilation and linking flags can be specified respectively with `CFLAGS` and `LDFLAGS` gradle system variables

#### Release

Release workflow:
 * make sure, that right version has been set in 'build.gradle' file, e.g. `0.0.1`
 * make github release with that version, e.g. `v0.0.1`
 * travis will run release stage and publish artifacts to bintray

Followed env variables should be set in travis:
 * `BINTRAY_USER` - bintray user
 * `BINTRAY_KEY` - bintray user api key
 * `BINTRAY_REPO` - bintray repository name
 * `BINTRAY_REPO_ORG` - bintray organization name

## License

[MIT](LICENSE)
