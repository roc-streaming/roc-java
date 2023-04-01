#include <jni.h>

#ifndef FRAME_ENCODING_H_
#define FRAME_ENCODING_H_

#include "common.h"

#include <roc/config.h>

#define FRAME_ENCODING_CLASS PACKAGE_BASE_NAME "/FrameEncoding"

roc_frame_encoding get_frame_encoding(JNIEnv* env, jobject jframe_encoding);

#endif /* FRAME_ENCODING_H_ */
