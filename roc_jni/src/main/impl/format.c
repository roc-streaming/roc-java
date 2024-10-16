#include "format.h"
#include "common.h"

roc_format get_format(JNIEnv* env, jobject jformat) {
    jclass formatClass = NULL;

    formatClass = (*env)->FindClass(env, FORMAT_CLASS);
    assert(formatClass != NULL);

    return (roc_format) get_enum_value(env, formatClass, jformat);
}
