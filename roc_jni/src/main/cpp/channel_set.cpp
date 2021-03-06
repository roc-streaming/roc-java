#include "org_rocstreaming_roctoolkit_ChannelSet.h"

#include "channel_set.h"
#include "common.h"

#include <roc/config.h>

roc_channel_set get_channel_set(JNIEnv *env, jobject jchannel_set) {
    jclass channelSetClass;

    channelSetClass = env->FindClass(CHANNEL_SET_CLASS);
    assert(channelSetClass != NULL);

    return (roc_channel_set) get_enum_value(env, channelSetClass, jchannel_set);
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_ChannelSet_getRocChannelSetStereo(JNIEnv *env, jclass thisObj) {
    return ROC_CHANNEL_SET_STEREO;
}