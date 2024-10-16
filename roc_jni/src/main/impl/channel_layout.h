#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define CHANNEL_LAYOUT_CLASS PACKAGE_BASE_NAME "/ChannelLayout"

roc_channel_layout get_channel_layout(JNIEnv* env, jobject jchannelLayout);
