#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define INTERFACE_CONFIG_CLASS PACKAGE_BASE_NAME "/InterfaceConfig"

int interface_config_unmarshal(JNIEnv* env, roc_interface_config* config, jobject jconfig);
