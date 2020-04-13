# JNI bindings for Roc

_Work in progress!_

## Dependencies
You will need to have libroc and libroc-devel (headers) installed. Refer to official build [instructions](https://roc-project.github.io/roc/docs/building.html) on how to install libroc. There is no official distribution for any OS as of now, you will need to install from source.

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

## License

[MIT](LICENSE)