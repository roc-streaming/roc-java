#include "receiver_config.h"
#include "clock_source.h"
#include "clock_sync_backend.h"
#include "clock_sync_profile.h"
#include "common.h"
#include "endpoint.h"
#include "format.h"
#include "media_encoding.h"
#include "resampler_backend.h"
#include "resampler_profile.h"

int receiver_config_unmarshal(JNIEnv* env, roc_receiver_config* config, jobject jconfig) {
    jclass receiverConfigClass = NULL;
    jobject jobj = NULL;
    int err = 0;

    receiverConfigClass = (*env)->FindClass(env, RECEIVER_CONFIG_CLASS);
    assert(receiverConfigClass != NULL);

    // set all fields to zeros
    assert(config != NULL);
    memset(config, 0, sizeof(*config));

    // frame_encoding
    jobj = get_object_field(
        env, receiverConfigClass, jconfig, "frameEncoding", "L" MEDIA_ENCODING_CLASS ";");
    if (jobj != NULL) {
        err = media_encoding_unmarshal(env, &config->frame_encoding, jobj);
        if (err) return err;
    }

    // clock_source
    jobj = get_object_field(
        env, receiverConfigClass, jconfig, "clockSource", "L" CLOCK_SOURCE_CLASS ";");
    if (jobj != NULL) config->clock_source = get_clock_source(env, jobj);

    // clock_sync_backend
    jobj = get_object_field(
        env, receiverConfigClass, jconfig, "clockSyncBackend", "L" CLOCK_SYNC_BACKEND_CLASS ";");
    if (jobj != NULL) config->clock_sync_backend = get_clock_sync_backend(env, jobj);

    // clock_sync_profile
    jobj = get_object_field(
        env, receiverConfigClass, jconfig, "clockSyncProfile", "L" CLOCK_SYNC_PROFILE_CLASS ";");
    if (jobj != NULL) config->clock_sync_profile = get_clock_sync_profile(env, jobj);

    // resampler_backend
    jobj = get_object_field(
        env, receiverConfigClass, jconfig, "resamplerBackend", "L" RESAMPLER_BACKEND_CLASS ";");
    if (jobj != NULL) config->resampler_backend = get_resampler_backend(env, jobj);

    // resampler_profile
    jobj = get_object_field(
        env, receiverConfigClass, jconfig, "resamplerProfile", "L" RESAMPLER_PROFILE_CLASS ";");
    if (jobj != NULL)
        config->resampler_profile = (roc_resampler_profile) get_resampler_profile(env, jobj);

    // target_latency
    config->target_latency
        = get_duration_field_value(env, receiverConfigClass, jconfig, "targetLatency", &err);
    if (err) return err;

    // latency_tolerance
    config->latency_tolerance
        = get_duration_field_value(env, receiverConfigClass, jconfig, "latencyTolerance", &err);
    if (err) return err;

    // no_playback_timeout
    config->no_playback_timeout
        = get_duration_field_value(env, receiverConfigClass, jconfig, "noPlaybackTimeout", &err);
    if (err) return err;

    // choppy_playback_timeout
    config->choppy_playback_timeout = get_duration_field_value(
        env, receiverConfigClass, jconfig, "choppyPlaybackTimeout", &err);
    if (err) return err;

    return 0;
}
