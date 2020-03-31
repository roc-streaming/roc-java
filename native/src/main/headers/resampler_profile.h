#include <jni.h>

#ifndef RESAMPLER_PROFILE_H_
#define RESAMPLER_PROFILE_H_
#ifdef __cplusplus
extern "C" {
#endif

#include <roc/config.h>

#define RESAMPLER_PROFILE_CLASS     "com/rocproject/roc/config/ResamplerProfile"

roc_resampler_profile get_resampler_profile(JNIEnv *env, jobject jresampler_profile);

#ifdef __cplusplus
}
#endif
#endif /* RESAMPLER_PROFILE_H_ */