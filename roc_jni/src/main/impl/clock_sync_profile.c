#include "clock_sync_profile.h"
#include "common.h"

#include <roc/config.h>

roc_clock_sync_profile get_clock_sync_profile(JNIEnv* env, jobject jclockSyncProfile) {
    jclass clockSyncProfile = NULL;

    clockSyncProfile = (*env)->FindClass(env, CLOCK_SYNC_PROFILE_CLASS);
    assert(clockSyncProfile != NULL);

    return (roc_clock_sync_profile) get_enum_value(env, clockSyncProfile, jclockSyncProfile);
}
