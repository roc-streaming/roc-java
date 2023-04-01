#include <jni.h>

#ifndef CHANNEL_SET_H_
#define CHANNEL_SET_H_
#ifdef __cplusplus
extern "C" {
#endif

#include "common.h"

#include <roc/config.h>

#define CHANNEL_SET_CLASS PACKAGE_BASE_NAME "/ChannelSet"

roc_channel_set get_channel_set(JNIEnv* env, jobject jchannel_set);

#ifdef __cplusplus
}
#endif
#endif /* CHANNEL_SET_H_ */
