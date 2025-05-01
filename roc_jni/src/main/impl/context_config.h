#pragma once

#include "platform.h"

#include <jni.h>
#include <roc/config.h>

#include <stdbool.h>

ATTR_NODISCARD bool context_config_unmarshal(
    JNIEnv* env, jobject jconfig, roc_context_config* result);
