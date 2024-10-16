#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define CONTEXT_CONFIG_CLASS PACKAGE_BASE_NAME "/RocContextConfig"

int context_config_unmarshal(JNIEnv* env, roc_context_config* config, jobject jconfig);
