#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define MEDIA_ENCODING_CLASS PACKAGE_BASE_NAME "/MediaEncoding"

int media_encoding_unmarshal(JNIEnv* env, roc_media_encoding* encoding, jobject jencoding);
