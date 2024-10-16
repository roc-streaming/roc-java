#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define CLOCK_SYNC_BACKEND_CLASS PACKAGE_BASE_NAME "/ClockSyncBackend"

roc_clock_sync_backend get_clock_sync_backend(JNIEnv* env, jobject jclockSyncBackend);
