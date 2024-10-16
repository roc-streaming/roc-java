#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define RESAMPLER_BACKEND_CLASS PACKAGE_BASE_NAME "/ResamplerBackend"

roc_resampler_backend get_resampler_backend(JNIEnv* env, jobject jresamplerBackend);
