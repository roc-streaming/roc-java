#include "com_rocproject_roc_sender_Sender.h"
#include "common.h"
#include "context.h"
#include "channel_set.h"
#include "frame_encoding.h"
#include "packet_encoding.h"
#include "resampler_profile.h"
#include "fec_code.h"
#include "address.h"

#include <cassert>
#include <cstring>

#include <roc/sender.h>
#include <roc/frame.h>

#define SENDER_CLASS                "com/rocproject/roc/sender/Sender"
#define SENDER_CONFIG_CLASS         "com/rocproject/roc/config/SenderConfig"

void sender_config_unmarshall(JNIEnv *env, roc_sender_config* config, jobject jconfig) {
    jobject tempObject;
    jclass senderConfigClass;

    senderConfigClass = env->FindClass(SENDER_CONFIG_CLASS);
    assert(senderConfigClass != NULL);

    config->frame_sample_rate = (unsigned int) get_int_field_value(env, senderConfigClass, jconfig, "frameSampleRate");

    tempObject = get_object_field(env, senderConfigClass, jconfig, "frameChannels", "L" CHANNEL_SET_CLASS ";");
    config->frame_channels = (roc_channel_set) get_channel_set(env, tempObject);

    tempObject = get_object_field(env, senderConfigClass, jconfig, "frameEncoding", "L" FRAME_ENCODING_CLASS ";");
    config->frame_encoding = (roc_frame_encoding) get_frame_encoding(env, tempObject);

    config->packet_sample_rate = (unsigned int) get_int_field_value(env, senderConfigClass, jconfig, "packetSampleRate");

    tempObject = get_object_field(env, senderConfigClass, jconfig, "packetChannels", "L" CHANNEL_SET_CLASS ";");
    config->packet_channels = (roc_channel_set) get_channel_set(env, tempObject);

    tempObject = get_object_field(env, senderConfigClass, jconfig, "packetEncoding", "L" PACKET_ENCODING_CLASS ";");
    config->packet_encoding = (roc_packet_encoding) get_packet_encoding(env, tempObject);

    config->packet_length = (unsigned long long) get_long_field_value(env, senderConfigClass, jconfig, "packetLength");
    config->packet_interleaving = (unsigned int) get_int_field_value(env, senderConfigClass, jconfig, "packetInterleaving");
    config->automatic_timing = (unsigned int) get_int_field_value(env, senderConfigClass, jconfig, "automaticTiming");

    tempObject = get_object_field(env, senderConfigClass, jconfig, "resamplerProfile", "L" RESAMPLER_PROFILE_CLASS ";");
    config->resampler_profile = (roc_resampler_profile) get_resampler_profile(env, tempObject);

    tempObject = get_object_field(env, senderConfigClass, jconfig, "fecCode", "L" FEC_CODE_CLASS ";");
    config->fec_code = (roc_fec_code) get_fec_code(env, tempObject);

    config->fec_block_source_packets = (unsigned int) get_int_field_value(env, senderConfigClass, jconfig, "fecBlockSourcePackets");
    config->fec_block_repair_packets = (unsigned int) get_int_field_value(env, senderConfigClass, jconfig, "fecBlockRepairPackets");
}

/*
 * Class:     com_rocproject_roc_sender_Sender
 * Method:    senderOpen
 * Signature: (Lcom/rocproject/roc/context/Context;Lcom/rocproject/roc/config/SenderConfig;)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_senderOpen(JNIEnv *env, jobject thisObj, jobject jcontext, jobject jconfig) {
    roc_context*            context;
    roc_sender_config       config;
    roc_sender*             sender;
    jclass                  senderClass;

    if (jcontext == NULL || jconfig == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    senderClass = env->FindClass(SENDER_CLASS);
    assert(senderClass != NULL);

    context = (roc_context*) get_context(env, jcontext);
    sender_config_unmarshall(env, &config, jconfig);

    if ((sender = roc_sender_open(context, &config)) == NULL) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error opening sender");
        return;
    }

    set_native_pointer(env, senderClass, thisObj, sender);
}

JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_close(JNIEnv *env, jobject thisObj) {
    jclass      senderClass;
    roc_sender* sender;

    senderClass = env->FindClass(SENDER_CLASS);
    assert(senderClass != NULL);

    sender = (roc_sender*) get_native_pointer(env, senderClass, thisObj);

    if (roc_sender_close(sender) != 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error closing sender");
    }
}

JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_bind(JNIEnv *env, jobject thisObj, jobject jaddress) {
    roc_address     address;
    roc_sender*     sender;
    int             port;
    jclass          senderClass;

    if (jaddress == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    senderClass = env->FindClass(SENDER_CLASS);
    assert(senderClass != NULL);

    sender = (roc_sender*) get_native_pointer(env, senderClass, thisObj);

    address_unmarshall(env, &address, jaddress);

    if ((port = roc_address_port(&address)) < 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error binding sender");
        return;
    }

    if (roc_sender_bind(sender, &address) != 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error binding sender");
        return;
    }

    // ephemeral port
    if (!port) {
        port = roc_address_port(&address);
        address_set_port(env, jaddress, port);
    }
}

JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_connect(JNIEnv *env, jobject thisObj, jint portType, jint protocol, jobject jaddress) {
    roc_address     address;
    roc_sender*     sender;
    jclass          senderClass;

    if (jaddress == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    senderClass = env->FindClass(SENDER_CLASS);
    assert(senderClass != NULL);

    sender = (roc_sender*) get_native_pointer(env, senderClass, thisObj);
    address_unmarshall(env, &address, jaddress);

    if (roc_sender_connect(sender, (roc_port_type) portType, (roc_protocol) protocol, &address) != 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error with sender connect");
    }
}

JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_writeFloat(JNIEnv *env, jobject thisObj, jfloat sample) {
    jclass      senderClass;
    roc_sender* sender;
    roc_frame   frame;
    float       samples[2] = { sample, sample };

    senderClass = env->FindClass(SENDER_CLASS);
    assert(senderClass != NULL);

    sender = (roc_sender*) get_native_pointer(env, senderClass, thisObj);

    memset(&frame, 0, sizeof(frame));

    frame.samples = samples;
    frame.samples_size = 2 * sizeof(float);

    if (roc_sender_write(sender, &frame) != 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error sending frame");
    }
}

JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_writeFloats(JNIEnv *env, jobject thisObj, jfloatArray jsamples) {
    jclass      senderClass;
    jfloat*     samples;
    jsize       len;
    roc_frame   frame;

    if (jsamples == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    senderClass = env->FindClass(SENDER_CLASS);
    assert(senderClass != NULL);

    samples = env->GetFloatArrayElements(jsamples, 0);
    assert(samples != NULL);

    len = env->GetArrayLength(jsamples);

    memset(&frame, 0, sizeof(frame));

    frame.samples = samples;
    frame.samples_size = len * sizeof(float);

    roc_sender* sender = (roc_sender*) get_native_pointer(env, senderClass, thisObj);
    if (roc_sender_write(sender, &frame) != 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error sending frame");
    }

    env->ReleaseFloatArrayElements(jsamples, samples, 0);
}
