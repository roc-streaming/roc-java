#include <jni.h>

#ifndef FRAME_ENCODING_H_
#define FRAME_ENCODING_H_
#ifdef __cplusplus
extern "C" {
#endif

#include <roc/config.h>

#define FRAME_ENCODING_CLASS        "com/github/rocproject/roc/FrameEncoding"

void frame_encoding_JNI_OnLoad(JNIEnv *env);
void frame_encoding_JNI_OnUnload(JNIEnv *env);

roc_frame_encoding get_frame_encoding(JNIEnv *env, jobject jframe_encoding);

#ifdef __cplusplus
}
#endif
#endif /* FRAME_ENCODING_H_ */