#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define RECEIVER_CONFIG_CLASS PACKAGE_BASE_NAME "/RocReceiverConfig"

int receiver_config_unmarshal(JNIEnv* env, roc_receiver_config* config, jobject jconfig);
