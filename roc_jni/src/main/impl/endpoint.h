#pragma once

#include "platform.h"

#include <jni.h>
#include <roc/endpoint.h>

#include <stdbool.h>

ATTR_NODISCARD bool endpoint_unmarshal(JNIEnv* env, jobject jendpoint, roc_endpoint** result);

ATTR_NODISCARD bool endpoint_set_protocol(JNIEnv* env, jobject jendpoint, roc_protocol value);
ATTR_NODISCARD bool endpoint_set_host(JNIEnv* env, jobject jendpoint, const char* value);
ATTR_NODISCARD bool endpoint_set_port(JNIEnv* env, jobject jendpoint, int value);
ATTR_NODISCARD bool endpoint_set_resource(JNIEnv* env, jobject jendpoint, const char* resource);
