#include "fec_encoding.h"
#include "common.h"

#include <roc/config.h>

roc_fec_encoding get_fec_encoding(JNIEnv *env, jobject jfec_encoding) {
    jclass fecEncodingClass = NULL;

    fecEncodingClass = env->FindClass(FEC_ENCODING_CLASS);
    assert(fecEncodingClass != NULL);

    return (roc_fec_encoding) get_enum_value(env, fecEncodingClass, jfec_encoding);
}
