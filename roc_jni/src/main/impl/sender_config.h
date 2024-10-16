#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define SENDER_CONFIG_CLASS PACKAGE_BASE_NAME "/RocSenderConfig"

int sender_config_unmarshal(JNIEnv* env, roc_sender_config* config, jobject jconfig);
