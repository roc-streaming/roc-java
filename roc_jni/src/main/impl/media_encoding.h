#pragma once

#include "platform.h"

#include <jni.h>
#include <roc/config.h>

#include <stdbool.h>

ATTR_NODISCARD bool media_encoding_unmarshal(
    JNIEnv* env, jobject jencoding, roc_media_encoding* result);
