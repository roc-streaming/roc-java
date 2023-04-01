#include <jni.h>

#ifndef RESAMPLER_PROFILE_H_
#define RESAMPLER_PROFILE_H_

#include "common.h"

#include <roc/config.h>

#define RESAMPLER_PROFILE_CLASS PACKAGE_BASE_NAME "/ResamplerProfile"

roc_resampler_profile get_resampler_profile(JNIEnv* env, jobject jresampler_profile);

#endif /* RESAMPLER_PROFILE_H_ */
