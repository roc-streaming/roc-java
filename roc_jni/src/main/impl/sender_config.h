#pragma once

#include "platform.h"

#include <jni.h>
#include <roc/config.h>

#include <stdbool.h>

ATTR_NODISCARD bool sender_config_unmarshal(
    JNIEnv* env, jobject jconfig, roc_sender_config* result);
