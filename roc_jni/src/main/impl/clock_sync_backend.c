#include "clock_sync_backend.h"
#include "common.h"

#include <roc/config.h>

roc_clock_sync_backend get_clock_sync_backend(JNIEnv* env, jobject jclockSyncBackend) {
    jclass clockSyncBackend = NULL;

    clockSyncBackend = (*env)->FindClass(env, CLOCK_SYNC_BACKEND_CLASS);
    assert(clockSyncBackend != NULL);

    return (roc_clock_sync_backend) get_enum_value(env, clockSyncBackend, jclockSyncBackend);
}
