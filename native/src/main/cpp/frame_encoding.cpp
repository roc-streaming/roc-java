#include "com_rocproject_roc_config_FrameEncoding.h"

#include "frame_encoding.h"
#include "common.h"

#include <cassert>

roc_frame_encoding get_frame_encoding(JNIEnv *env, jobject jframe_encoding) {
    jclass frameEncodingClass;

    frameEncodingClass = env->FindClass(FRAME_ENCODING_CLASS);
    assert(frameEncodingClass != NULL);

    return (roc_frame_encoding) get_enum_value(env, frameEncodingClass, jframe_encoding);
}

JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_FrameEncoding_getRocFrameEncodingPCMFloat(JNIEnv *env, jclass thisObj) {
    return ROC_FRAME_ENCODING_PCM_FLOAT;
}