#include "org_rocstreaming_roctoolkit_ResamplerProfile.h"

#include "resampler_profile.h"
#include "common.h"

#include <roc/config.h>

roc_resampler_profile get_resampler_profile(JNIEnv *env, jobject jresampler_profile) {
    jclass resamplerProfileClass;

    resamplerProfileClass = env->FindClass(RESAMPLER_PROFILE_CLASS);
    assert(resamplerProfileClass != NULL);

    return (roc_resampler_profile) get_enum_value(env, resamplerProfileClass, jresampler_profile);
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_ResamplerProfile_getRocResamplerDisable(JNIEnv *, jclass) {
    return ROC_RESAMPLER_DISABLE;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_ResamplerProfile_getRocResamplerDefault(JNIEnv *, jclass) {
    return ROC_RESAMPLER_DEFAULT;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_ResamplerProfile_getRocResamplerHigh(JNIEnv *, jclass) {
    return ROC_RESAMPLER_HIGH;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_ResamplerProfile_getRocResamplerMedium(JNIEnv *, jclass) {
    return ROC_RESAMPLER_MEDIUM;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_ResamplerProfile_getRocResamplerLow(JNIEnv *, jclass) {
    return ROC_RESAMPLER_LOW;
}