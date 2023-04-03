#include "org_rocstreaming_roctoolkit_Receiver.h"

#include "channel_set.h"
#include "clock_source.h"
#include "common.h"
#include "endpoint.h"
#include "frame_encoding.h"
#include "resampler_backend.h"
#include "resampler_profile.h"

#include <roc/receiver.h>

#define RECEIVER_CLASS PACKAGE_BASE_NAME "/Receiver"
#define RECEIVER_CONFIG_CLASS PACKAGE_BASE_NAME "/ReceiverConfig"

int receiver_config_unmarshal(JNIEnv* env, roc_receiver_config* config, jobject jconfig) {
    jobject tempObject = NULL;
    jclass receiverConfigClass = NULL;
    int err = 0;

    receiverConfigClass = env->FindClass(RECEIVER_CONFIG_CLASS);
    assert(receiverConfigClass != NULL);

    config->frame_sample_rate
        = get_uint_field_value(env, receiverConfigClass, jconfig, "frameSampleRate", &err);
    if (err) return err;

    tempObject = get_object_field(
        env, receiverConfigClass, jconfig, "frameChannels", "L" CHANNEL_SET_CLASS ";");
    if (tempObject == NULL) return 1;
    config->frame_channels = (roc_channel_set) get_channel_set(env, tempObject);

    tempObject = get_object_field(
        env, receiverConfigClass, jconfig, "frameEncoding", "L" FRAME_ENCODING_CLASS ";");
    if (tempObject == NULL) return 1;
    config->frame_encoding = (roc_frame_encoding) get_frame_encoding(env, tempObject);

    tempObject = get_object_field(
        env, receiverConfigClass, jconfig, "clockSource", "L" CLOCK_SOURCE_CLASS ";");
    config->clock_source = get_clock_source(env, tempObject);

    tempObject = get_object_field(
        env, receiverConfigClass, jconfig, "resamplerBackend", "L" RESAMPLER_BACKEND_CLASS ";");
    config->resampler_backend = get_resampler_backend(env, tempObject);

    tempObject = get_object_field(
        env, receiverConfigClass, jconfig, "resamplerProfile", "L" RESAMPLER_PROFILE_CLASS ";");
    config->resampler_profile = (roc_resampler_profile) get_resampler_profile(env, tempObject);

    config->target_latency
        = get_ullong_field_value(env, receiverConfigClass, jconfig, "targetLatency", &err);
    if (err) return err;

    config->max_latency_overrun
        = get_ullong_field_value(env, receiverConfigClass, jconfig, "maxLatencyOverrun", &err);
    if (err) return err;

    config->max_latency_underrun
        = get_ullong_field_value(env, receiverConfigClass, jconfig, "maxLatencyUnderrun", &err);
    if (err) return err;

    config->no_playback_timeout
        = get_llong_field_value(env, receiverConfigClass, jconfig, "noPlaybackTimeout", &err);
    if (err) return err;

    config->broken_playback_timeout
        = get_llong_field_value(env, receiverConfigClass, jconfig, "brokenPlaybackTimeout", &err);
    if (err) return err;

    config->breakage_detection_window = get_ullong_field_value(
        env, receiverConfigClass, jconfig, "breakageDetectionWindow", &err);
    if (err) return err;

    return 0;
}

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_Receiver_open(
    JNIEnv* env, jclass receiverClass, jlong contextPtr, jobject jconfig) {
    roc_context* context = NULL;
    roc_receiver_config receiverConfig = {};
    roc_receiver* receiver = NULL;

    context = (roc_context*) contextPtr;

    if (receiver_config_unmarshal(env, &receiverConfig, jconfig) != 0) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "Bad arguments");
        return (jlong) NULL;
    }

    if ((roc_receiver_open(context, &receiverConfig, &receiver)) != 0) {
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Error opening receiver");
        return (jlong) NULL;
    }

    return (jlong) receiver;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Receiver_setMulticastGroup(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jint slot, jint interface, jstring jip) {
    roc_receiver* receiver = NULL;
    const char* ip = NULL;

    receiver = (roc_receiver*) receiverPtr;
    ip = env->GetStringUTFChars(jip, 0);

    if (roc_receiver_set_multicast_group(receiver, (roc_slot) slot, (roc_interface) interface, ip)
        != 0) {
        env->ReleaseStringUTFChars(jip, ip);
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Couldn't set multicast group");
        return;
    }
    env->ReleaseStringUTFChars(jip, ip);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Receiver_bind(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jint slot, jint interface, jobject jendpoint) {
    roc_receiver* receiver = NULL;
    roc_endpoint* endpoint = NULL;
    int port = 0;

    receiver = (roc_receiver*) receiverPtr;
    if (endpoint_unmarshal(env, &endpoint, jendpoint) != 0) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    if (roc_receiver_bind(receiver, (roc_slot) slot, (roc_interface) interface, endpoint) != 0) {
        roc_endpoint_deallocate(endpoint);
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Error binding receiver");
        return;
    }

    if (roc_endpoint_get_port(endpoint, &port) == 0) {
        endpoint_set_port(env, jendpoint, port);
    } else {
        endpoint_set_port(env, jendpoint, -1);
    }
    roc_endpoint_deallocate(endpoint);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Receiver_readFloats(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jfloatArray jsamples) {
    roc_receiver* receiver = NULL;
    roc_frame frame = {};
    jfloat* samples = NULL;
    jsize len = 0;

    if (jsamples == NULL) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    samples = env->GetFloatArrayElements(jsamples, 0);
    len = env->GetArrayLength(jsamples);

    receiver = (roc_receiver*) receiverPtr;

    memset(&frame, 0, sizeof(frame));
    frame.samples = samples;
    frame.samples_size = len * sizeof(float);

    if (roc_receiver_read(receiver, &frame) != 0) {
        jclass exceptionClass = env->FindClass(IO_EXCEPTION);
        env->ThrowNew(exceptionClass, "Error with receiver read");
        // no return
    }

    env->ReleaseFloatArrayElements(jsamples, samples, 0);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Receiver_close(
    JNIEnv* env, jclass receiverClass, jlong receiverPtr) {

    roc_receiver* receiver = (roc_receiver*) receiverPtr;

    if (roc_receiver_close(receiver) != 0) {
        jclass exceptionClass = env->FindClass(IO_EXCEPTION);
        env->ThrowNew(exceptionClass, "Error closing receiver");
    }
}
