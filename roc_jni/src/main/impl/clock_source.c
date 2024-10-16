#include "clock_source.h"
#include "common.h"

#include <roc/config.h>

roc_clock_source get_clock_source(JNIEnv* env, jobject jclockSource) {
    jclass clockSourceClass = NULL;

    clockSourceClass = (*env)->FindClass(env, CLOCK_SOURCE_CLASS);
    assert(clockSourceClass != NULL);

    return (roc_clock_source) get_enum_value(env, clockSourceClass, jclockSource);
}
