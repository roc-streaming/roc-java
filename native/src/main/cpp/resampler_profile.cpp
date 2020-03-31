#include "com_rocproject_roc_config_ResamplerProfile.h"

#include "resampler_profile.h"
#include "common.h"

#include <cassert>
#include <roc/config.h>

roc_resampler_profile get_resampler_profile(JNIEnv *env, jobject jresampler_profile) {
    jclass resamplerProfileClass;

    resamplerProfileClass = env->FindClass(RESAMPLER_PROFILE_CLASS);
    assert(resamplerProfileClass != NULL);

    return (roc_resampler_profile) get_enum_value(env, resamplerProfileClass, jresampler_profile);
}

JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_ResamplerProfile_getRocResamplerDisable(JNIEnv *, jclass) {
    return ROC_RESAMPLER_DISABLE;
}

/*
 * Class:     com_rocproject_roc_config_ResamplerProfile
 * Method:    getRocResamplerDefault
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_ResamplerProfile_getRocResamplerDefault(JNIEnv *, jclass) {
    return ROC_RESAMPLER_DEFAULT;
}

/*
 * Class:     com_rocproject_roc_config_ResamplerProfile
 * Method:    getRocResamplerHigh
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_ResamplerProfile_getRocResamplerHigh(JNIEnv *, jclass) {
    return ROC_RESAMPLER_HIGH;
}

/*
 * Class:     com_rocproject_roc_config_ResamplerProfile
 * Method:    getRocResamplerMedium
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_ResamplerProfile_getRocResamplerMedium(JNIEnv *, jclass) {
    return ROC_RESAMPLER_MEDIUM;
}

/*
 * Class:     com_rocproject_roc_config_ResamplerProfile
 * Method:    getRocResamplerLow
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_ResamplerProfile_getRocResamplerLow(JNIEnv *, jclass) {
    return ROC_RESAMPLER_LOW;
}