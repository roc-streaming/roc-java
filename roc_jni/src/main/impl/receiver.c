#include "org_rocstreaming_roctoolkit_RocReceiver.h"

#include "channel_set.h"
#include "clock_source.h"
#include "common.h"
#include "endpoint.h"
#include "frame_encoding.h"
#include "resampler_backend.h"
#include "resampler_profile.h"

#include <roc/receiver.h>

#define RECEIVER_CONFIG_CLASS PACKAGE_BASE_NAME "/RocReceiverConfig"

static int receiver_config_unmarshal(JNIEnv* env, roc_receiver_config* config, jobject jconfig) {
    jclass receiverConfigClass = NULL;
    jobject jobj = NULL;
    int err = 0;

    receiverConfigClass = (*env)->FindClass(env, RECEIVER_CONFIG_CLASS);
    assert(receiverConfigClass != NULL);

    // set all fields to zeros
    memset(config, 0, sizeof(*config));

    // frame_sample_rate
    config->frame_sample_rate
        = get_uint_field_value(env, receiverConfigClass, jconfig, "frameSampleRate", &err);
    if (err) return err;

    // frame_channels
    jobj = get_object_field(
        env, receiverConfigClass, jconfig, "frameChannels", "L" CHANNEL_SET_CLASS ";");
    if (jobj != NULL) config->frame_channels = (roc_channel_set) get_channel_set(env, jobj);

    // frame_encoding
    jobj = get_object_field(
        env, receiverConfigClass, jconfig, "frameEncoding", "L" FRAME_ENCODING_CLASS ";");
    if (jobj != NULL) config->frame_encoding = (roc_frame_encoding) get_frame_encoding(env, jobj);

    // clock_source
    jobj = get_object_field(
        env, receiverConfigClass, jconfig, "clockSource", "L" CLOCK_SOURCE_CLASS ";");
    if (jobj != NULL) config->clock_source = get_clock_source(env, jobj);

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
        = get_ullong_field_value(env, receiverConfigClass, jconfig, "targetLatency", &err);
    if (err) return err;

    // max_latency_overrun
    config->max_latency_overrun
        = get_ullong_field_value(env, receiverConfigClass, jconfig, "maxLatencyOverrun", &err);
    if (err) return err;

    // max_latency_underrun
    config->max_latency_underrun
        = get_ullong_field_value(env, receiverConfigClass, jconfig, "maxLatencyUnderrun", &err);
    if (err) return err;

    // no_playback_timeout
    config->no_playback_timeout
        = get_llong_field_value(env, receiverConfigClass, jconfig, "noPlaybackTimeout", &err);
    if (err) return err;

    // broken_playback_timeout
    config->broken_playback_timeout
        = get_llong_field_value(env, receiverConfigClass, jconfig, "brokenPlaybackTimeout", &err);
    if (err) return err;

    // breakage_detection_window
    config->breakage_detection_window = get_ullong_field_value(
        env, receiverConfigClass, jconfig, "breakageDetectionWindow", &err);
    if (err) return err;

    return 0;
}

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_open(
    JNIEnv* env, jclass receiverClass, jlong contextPtr, jobject jconfig) {
    roc_context* context = NULL;
    roc_receiver_config receiverConfig = {};
    roc_receiver* receiver = NULL;

    context = (roc_context*) contextPtr;

    if (receiver_config_unmarshal(env, &receiverConfig, jconfig) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad config argument");
        goto out;
    }

    if ((roc_receiver_open(context, &receiverConfig, &receiver)) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error opening receiver");
        receiver = NULL;
        goto out;
    }

out:
    return (jlong) receiver;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_setMulticastGroup(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jint slot, jint interface, jstring jip) {
    roc_receiver* receiver = NULL;
    const char* ip = NULL;

    receiver = (roc_receiver*) receiverPtr;

    ip = (*env)->GetStringUTFChars(env, jip, 0);
    assert(ip != NULL);

    if (roc_receiver_set_multicast_group(receiver, (roc_slot) slot, (roc_interface) interface, ip)
        != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Can't set multicast group");
        goto out;
    }

out:
    if (ip != NULL) (*env)->ReleaseStringUTFChars(env, jip, ip);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_bind(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jint slot, jint interface, jobject jendpoint) {
    roc_receiver* receiver = NULL;
    roc_endpoint* endpoint = NULL;
    int port = 0;

    receiver = (roc_receiver*) receiverPtr;

    if (endpoint_unmarshal(env, &endpoint, jendpoint) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad endpoint argument");
        goto out;
    }

    if (roc_receiver_bind(receiver, (roc_slot) slot, (roc_interface) interface, endpoint) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error binding receiver");
        goto out;
    }

    if (roc_endpoint_get_port(endpoint, &port) == 0) {
        endpoint_set_port(env, jendpoint, port);
    } else {
        endpoint_set_port(env, jendpoint, -1);
    }

out:
    if (endpoint != NULL) {
        roc_endpoint_deallocate(endpoint);
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_readFloats(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jfloatArray jsamples) {
    roc_receiver* receiver = NULL;
    jfloat* samples = NULL;
    jsize len = 0;
    roc_frame frame = {};

    receiver = (roc_receiver*) receiverPtr;

    if (jsamples == NULL) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad samples argument");
        goto out;
    }
    samples = (*env)->GetFloatArrayElements(env, jsamples, 0);
    len = (*env)->GetArrayLength(env, jsamples);
    assert(samples != NULL);

    memset(&frame, 0, sizeof(frame));
    frame.samples = samples;
    frame.samples_size = len * sizeof(float);

    if (roc_receiver_read(receiver, &frame) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error reading frame");
        goto out;
    }

out:
    if (samples != NULL) (*env)->ReleaseFloatArrayElements(env, jsamples, samples, 0);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_close(
    JNIEnv* env, jclass receiverClass, jlong receiverPtr) {

    roc_receiver* receiver = (roc_receiver*) receiverPtr;

    if (roc_receiver_close(receiver) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error closing receiver");
    }
}
