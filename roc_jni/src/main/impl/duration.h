#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define DURATION_CLASS PACKAGE_BASE_NAME "/Duration"

roc_duration get_duration(JNIEnv* env, jobject jduration);