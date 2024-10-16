#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define FORMAT_CLASS PACKAGE_BASE_NAME "/Format"

roc_format get_format(JNIEnv* env, jobject jformat);
