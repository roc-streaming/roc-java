#include "channel_set.h"
#include "common.h"

#include <roc/config.h>

roc_channel_set get_channel_set(JNIEnv *env, jobject jchannel_set) {
    jclass channelSetClass = NULL;

    channelSetClass = env->FindClass(CHANNEL_SET_CLASS);
    assert(channelSetClass != NULL);

    return (roc_channel_set) get_enum_value(env, channelSetClass, jchannel_set);
}
