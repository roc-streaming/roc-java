#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define CLOCK_SYNC_PROFILE_CLASS PACKAGE_BASE_NAME "/ClockSyncProfile"

roc_clock_sync_profile get_clock_sync_profile(JNIEnv* env, jobject jclockSyncProfile);
