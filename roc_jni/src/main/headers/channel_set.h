#include <jni.h>

#ifndef CHANNEL_SET_H_
#define CHANNEL_SET_H_

#include "common.h"

#include <roc/config.h>

#define CHANNEL_SET_CLASS PACKAGE_BASE_NAME "/ChannelSet"

roc_channel_set get_channel_set(JNIEnv* env, jobject jchannel_set);

#endif /* CHANNEL_SET_H_ */
