#pragma once

#include "helpers.h"
#include "platform.h"

#include <jni.h>
#include <roc/config.h>

#include <stdbool.h>

ATTR_NODISCARD bool interface_config_unmarshal(
    JNIEnv* env, jobject jconfig, roc_interface_config* result);
