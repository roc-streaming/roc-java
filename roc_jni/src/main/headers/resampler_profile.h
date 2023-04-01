#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define RESAMPLER_PROFILE_CLASS PACKAGE_BASE_NAME "/ResamplerProfile"

roc_resampler_profile get_resampler_profile(JNIEnv* env, jobject jresampler_profile);
