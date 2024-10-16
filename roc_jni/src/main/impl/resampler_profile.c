#include "resampler_profile.h"
#include "common.h"

#include <roc/config.h>

roc_resampler_profile get_resampler_profile(JNIEnv* env, jobject jresamplerProfile) {
    jclass resamplerProfileClass = NULL;

    resamplerProfileClass = (*env)->FindClass(env, RESAMPLER_PROFILE_CLASS);
    assert(resamplerProfileClass != NULL);

    return (roc_resampler_profile) get_enum_value(env, resamplerProfileClass, jresamplerProfile);
}
