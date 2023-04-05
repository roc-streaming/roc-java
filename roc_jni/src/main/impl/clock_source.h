#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define CLOCK_SOURCE_CLASS PACKAGE_BASE_NAME "/ClockSource"

roc_clock_source get_clock_source(JNIEnv* env, jobject jclock_source);
