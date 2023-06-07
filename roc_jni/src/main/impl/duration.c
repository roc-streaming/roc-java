#include "duration.h"
#include "common.h"

#include <roc/config.h>

roc_duration get_duration(JNIEnv* env, jobject jduration) {
    jclass durationClass = NULL;

    durationClass = (*env)->FindClass(env, DURATION_CLASS);
    assert(durationClass != NULL);

    return (roc_duration) get_enum_value(env, durationClass, jduration);
}