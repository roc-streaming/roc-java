#include "receiver_config.h"
#include "endpoint.h"
#include "exceptions.h"
#include "helpers.h"
#include "media_encoding.h"

#include <assert.h>
#include <string.h>

bool receiver_config_unmarshal(JNIEnv* env, jobject jconfig, roc_receiver_config* result) {
    assert(env);
    assert(jconfig);
    assert(result);

    memset(result, 0, sizeof(*result));

    jclass jclass = find_class(env, RECEIVER_CONFIG_CLASS);
    if (!jclass) {
        return false;
    }

    jobject jencoding = NULL;
    int enum_value = 0;

    // frame_encoding
    if (!read_object_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS, "frameEncoding",
            MEDIA_ENCODING_CLASS, &jencoding)) {
        return false;
    }
    if (jencoding) {
        if (!media_encoding_unmarshal(env, jencoding, &result->frame_encoding)) {
            return false;
        }
    }

    // clock_source
    if (!read_enum_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS, "clockSource",
            CLOCK_SOURCE_CLASS, &enum_value)) {
        return false;
    }
    result->clock_source = (roc_clock_source) enum_value;

    // clock_sync_backend
    if (!read_enum_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS, "clockSyncBackend",
            CLOCK_SYNC_BACKEND_CLASS, &enum_value)) {
        return false;
    }
    result->clock_sync_backend = (roc_clock_sync_backend) enum_value;

    // clock_sync_profile
    if (!read_enum_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS, "clockSyncProfile",
            CLOCK_SYNC_PROFILE_CLASS, &enum_value)) {
        return false;
    }
    result->clock_sync_profile = (roc_clock_sync_profile) enum_value;

    // resampler_backend
    if (!read_enum_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS, "resamplerBackend",
            RESAMPLER_BACKEND_CLASS, &enum_value)) {
        return false;
    }
    result->resampler_backend = (roc_resampler_backend) enum_value;

    // resampler_profile
    if (!read_enum_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS, "resamplerProfile",
            RESAMPLER_PROFILE_CLASS, &enum_value)) {
        return false;
    }
    result->resampler_profile = (roc_resampler_profile) enum_value;

    // target_latency
    if (!read_unsigned_duration_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS, "targetLatency",
            &result->target_latency)) {
        return false;
    }

    // latency_tolerance
    if (!read_unsigned_duration_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS,
            "latencyTolerance", &result->latency_tolerance)) {
        return false;
    }

    // no_playback_timeout
    if (!read_signed_duration_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS,
            "noPlaybackTimeout", &result->no_playback_timeout)) {
        return false;
    }

    // choppy_playback_timeout
    if (!read_signed_duration_field(env, jclass, jconfig, RECEIVER_CONFIG_CLASS,
            "choppyPlaybackTimeout", &result->choppy_playback_timeout)) {
        return false;
    }

    return true;
}
