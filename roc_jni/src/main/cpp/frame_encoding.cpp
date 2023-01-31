#include "frame_encoding.h"
#include "common.h"

roc_frame_encoding get_frame_encoding(JNIEnv *env, jobject jframe_encoding) {
    jclass frameEncodingClass;

    frameEncodingClass = env->FindClass(FRAME_ENCODING_CLASS);
    assert(frameEncodingClass != NULL);

    return (roc_frame_encoding) get_enum_value(env, frameEncodingClass, jframe_encoding);
}
