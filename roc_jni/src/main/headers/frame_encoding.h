#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define FRAME_ENCODING_CLASS PACKAGE_BASE_NAME "/FrameEncoding"

roc_frame_encoding get_frame_encoding(JNIEnv* env, jobject jframe_encoding);
