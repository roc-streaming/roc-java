#include "com_github_rocproject_roc_Receiver.h"

#include "common.h"
#include "channel_set.h"
#include "frame_encoding.h"
#include "resampler_profile.h"
#include "context.h"
#include "address.h"

#include <cstring>
#include <cassert>

#include <roc/receiver.h>

#define RECEIVER_CLASS              "com/github/rocproject/roc/Receiver"
#define RECEIVER_CONFIG_CLASS       "com/github/rocproject/roc/ReceiverConfig"

void receiver_config_unmarshall(JNIEnv *env, roc_receiver_config* config, jobject jconfig) {
    jobject tempObject;
    jclass  receiverConfigClass;

    receiverConfigClass = env->FindClass(RECEIVER_CONFIG_CLASS);
    assert(receiverConfigClass != NULL);

    config->frame_sample_rate = (unsigned int) get_int_field_value(env, receiverConfigClass, jconfig, "frameSampleRate");

    tempObject = get_object_field(env, receiverConfigClass, jconfig, "frameChannels", "L" CHANNEL_SET_CLASS ";");
    config->frame_channels = (roc_channel_set) get_channel_set(env, tempObject);

    tempObject = get_object_field(env, receiverConfigClass, jconfig, "frameEncoding", "L" FRAME_ENCODING_CLASS ";");
    config->frame_encoding = (roc_frame_encoding) get_frame_encoding(env, tempObject);

    config->automatic_timing = (unsigned int) get_int_field_value(env, receiverConfigClass, jconfig, "automaticTiming");

    tempObject = get_object_field(env, receiverConfigClass, jconfig, "resamplerProfile", "L" RESAMPLER_PROFILE_CLASS ";");
    config->resampler_profile = (roc_resampler_profile) get_resampler_profile(env, tempObject);

    config->target_latency = (unsigned long long) get_long_field_value(env, receiverConfigClass, jconfig, "targetLatency");
    config->max_latency_overrun = (unsigned long long) get_long_field_value(env, receiverConfigClass, jconfig, "maxLatencyOverrun");
    config->max_latency_underrun = (unsigned long long) get_long_field_value(env, receiverConfigClass, jconfig, "maxLatencyUnderrun");
    config->no_playback_timeout = (long long) get_long_field_value(env, receiverConfigClass, jconfig, "noPlaybackTimeout");
    config->broken_playback_timeout = (long long) get_long_field_value(env, receiverConfigClass, jconfig, "brokenPlaybackTimeout");
    config->breakage_detection_window = (unsigned long long) get_long_field_value(env, receiverConfigClass, jconfig, "breakageDetectionWindow");
}

JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Receiver_receiverOpen(JNIEnv * env, jobject thisObj,
                    jobject jcontext, jobject jconfig) {
    roc_context*            context;
    roc_receiver_config     receiverConfig;
    roc_receiver*           receiver;
    jclass                  receiverClass;

    if (jcontext == NULL || jconfig == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    receiverClass = env->FindClass(RECEIVER_CLASS);
    assert(receiverClass != NULL);

    context = (roc_context*) get_context(env, jcontext);

    receiver_config_unmarshall(env, &receiverConfig, jconfig);

    if ((receiver = roc_receiver_open(context, &receiverConfig)) == NULL) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error opening receiver");
        return;
    }

    set_native_pointer(env, receiverClass, thisObj, receiver);
}

JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Receiver_bind(JNIEnv * env, jobject thisObj,
                    jint type, jint protocol, jobject jaddress) {
    roc_receiver*   receiver    = NULL;
    roc_address     address;
    int             port        = 0;
    jclass          receiverClass;

    receiverClass = env->FindClass(RECEIVER_CLASS);
    assert(receiverClass != NULL);

    receiver = (roc_receiver*) get_native_pointer(env, receiverClass, thisObj);

    if (address_unmarshall(env, &address, jaddress) != 0) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    if ((port = roc_address_port(&address)) < 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error binding receiver");
        return;
    }

    if (roc_receiver_bind(receiver, (roc_port_type) type, (roc_protocol) protocol, &address) != 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error binding receiver");
        return;
    }

    // ephemeral port
    if (!port) {
        port = roc_address_port(&address);
        address_set_port(env, jaddress, port);
    }
}

JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Receiver_readFloats(JNIEnv *env, jobject thisObj, jfloatArray jsamples) {
    roc_receiver*       receiver;
    roc_frame           frame;
    jfloat*             samples;
    jsize               len;
    jclass receiverClass;

    if (jsamples == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    receiverClass = env->FindClass(RECEIVER_CLASS);
    assert(receiverClass != NULL);

    samples = env->GetFloatArrayElements(jsamples, 0);
    len = env->GetArrayLength(jsamples);

    receiver = (roc_receiver*) get_native_pointer(env, receiverClass, thisObj);

    memset(&frame, 0, sizeof(frame));
    frame.samples = samples;
    frame.samples_size = len * sizeof(float);

    if (roc_receiver_read(receiver, &frame) != 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error with receiver read");
        // no return
    }

    env->ReleaseFloatArrayElements(jsamples, samples, 0);
}

JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Receiver_close(JNIEnv *env, jobject thisObj) {
    jclass receiverClass;

    receiverClass = env->FindClass(RECEIVER_CLASS);
    assert(receiverClass != NULL);

    roc_receiver* receiver = (roc_receiver*) get_native_pointer(env, receiverClass, thisObj);

    if (roc_receiver_close(receiver) != 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error closing receiver");
    }
}