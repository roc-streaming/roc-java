#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define PROTOCOL_CLASS PACKAGE_BASE_NAME "/Protocol"
#define PROTOCOL_UTILS_CLASS PACKAGE_BASE_NAME "/ProtocolUtils"

roc_protocol get_protocol(JNIEnv* env, jobject jprotocol);

jobject get_protocol_enum(JNIEnv* env, roc_protocol protocol);
