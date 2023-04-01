#include "org_rocstreaming_roctoolkit_Sender.h"

#include "channel_set.h"
#include "clock_source.h"
#include "common.h"
#include "endpoint.h"
#include "fec_encoding.h"
#include "frame_encoding.h"
#include "packet_encoding.h"
#include "resampler_backend.h"
#include "resampler_profile.h"

#include <roc/frame.h>
#include <roc/sender.h>

#define SENDER_CLASS PACKAGE_BASE_NAME "/Sender"
#define SENDER_CONFIG_CLASS PACKAGE_BASE_NAME "/SenderConfig"

char sender_config_unmarshal(JNIEnv* env, roc_sender_config* config, jobject jconfig) {
    jobject tempObject = NULL;
    jclass senderConfigClass = NULL;
    char err = 0;

    senderConfigClass = env->FindClass(SENDER_CONFIG_CLASS);
    assert(senderConfigClass != NULL);

    config->frame_sample_rate
        = get_uint_field_value(env, senderConfigClass, jconfig, "frameSampleRate", &err);
    if (err) return err;

    tempObject = get_object_field(
        env, senderConfigClass, jconfig, "frameChannels", "L" CHANNEL_SET_CLASS ";");
    if (tempObject == NULL) return 1;
    config->frame_channels = (roc_channel_set) get_channel_set(env, tempObject);

    tempObject = get_object_field(
        env, senderConfigClass, jconfig, "frameEncoding", "L" FRAME_ENCODING_CLASS ";");
    if (tempObject == NULL) return 1;
    config->frame_encoding = (roc_frame_encoding) get_frame_encoding(env, tempObject);

    config->packet_sample_rate
        = get_uint_field_value(env, senderConfigClass, jconfig, "packetSampleRate", &err);
    if (err) return err;

    tempObject = get_object_field(
        env, senderConfigClass, jconfig, "packetChannels", "L" CHANNEL_SET_CLASS ";");
    config->packet_channels = (roc_channel_set) get_channel_set(env, tempObject);

    tempObject = get_object_field(
        env, senderConfigClass, jconfig, "packetEncoding", "L" PACKET_ENCODING_CLASS ";");
    config->packet_encoding = (roc_packet_encoding) get_packet_encoding(env, tempObject);

    config->packet_length
        = get_ullong_field_value(env, senderConfigClass, jconfig, "packetLength", &err);
    if (err) return err;
    config->packet_interleaving
        = get_uint_field_value(env, senderConfigClass, jconfig, "packetInterleaving", &err);
    if (err) return err;

    tempObject = get_object_field(
        env, senderConfigClass, jconfig, "clockSource", "L" CLOCK_SOURCE_CLASS ";");
    config->clock_source = get_clock_source(env, tempObject);

    tempObject = get_object_field(
        env, senderConfigClass, jconfig, "resamplerBackend", "L" RESAMPLER_BACKEND_CLASS ";");
    config->resampler_backend = get_resampler_backend(env, tempObject);

    tempObject = get_object_field(
        env, senderConfigClass, jconfig, "resamplerProfile", "L" RESAMPLER_PROFILE_CLASS ";");
    config->resampler_profile = (roc_resampler_profile) get_resampler_profile(env, tempObject);

    tempObject = get_object_field(
        env, senderConfigClass, jconfig, "fecEncoding", "L" FEC_ENCODING_CLASS ";");
    config->fec_encoding = (roc_fec_encoding) get_fec_encoding(env, tempObject);

    config->fec_block_source_packets
        = get_uint_field_value(env, senderConfigClass, jconfig, "fecBlockSourcePackets", &err);
    if (err) return err;
    config->fec_block_repair_packets
        = get_uint_field_value(env, senderConfigClass, jconfig, "fecBlockRepairPackets", &err);
    return err;
}

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_Sender_open(
    JNIEnv* env, jclass senderClass, jlong contextPtr, jobject jconfig) {
    roc_context* context = NULL;
    roc_sender_config config = {};
    roc_sender* sender = NULL;

    context = (roc_context*) contextPtr;

    if (sender_config_unmarshal(env, &config, jconfig) != 0) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "Bad arguments");
        return (jlong) NULL;
    }

    if ((roc_sender_open(context, &config, &sender)) != 0) {
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Error opening sender");
        return (jlong) NULL;
    }

    return (jlong) sender;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Sender_close(
    JNIEnv* env, jclass senderClass, jlong senderPtr) {

    roc_sender* sender = (roc_sender*) senderPtr;

    if (roc_sender_close(sender) != 0) {
        jclass exceptionClass = env->FindClass(IO_EXCEPTION);
        env->ThrowNew(exceptionClass, "Error closing sender");
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Sender_setOutgoingAddress(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jint slot, jint interface, jstring jip) {
    roc_sender* sender = NULL;
    const char* ip = NULL;

    sender = (roc_sender*) senderPtr;
    ip = env->GetStringUTFChars(jip, 0);

    if (roc_sender_set_outgoing_address(sender, (roc_slot) slot, (roc_interface) interface, ip)
        != 0) {
        env->ReleaseStringUTFChars(jip, ip);
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Couldn't set outgoing address");
        return;
    }
    env->ReleaseStringUTFChars(jip, ip);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Sender_connect(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jint slot, jint interface, jobject jendpoint) {
    roc_endpoint* endpoint = NULL;
    roc_sender* sender = NULL;

    if (jendpoint == NULL) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    sender = (roc_sender*) senderPtr;
    if (endpoint_unmarshal(env, &endpoint, jendpoint) != 0) {
        jclass exceptionClass = env->FindClass(IO_EXCEPTION);
        env->ThrowNew(exceptionClass, "Error unmarshalling endpoint");
        return;
    }

    if (roc_sender_connect(sender, (roc_slot) slot, (roc_interface) interface, endpoint) != 0) {
        roc_endpoint_deallocate(endpoint);
        jclass exceptionClass = env->FindClass(IO_EXCEPTION);
        env->ThrowNew(exceptionClass, "Error with sender connect");
        return;
    }
    roc_endpoint_deallocate(endpoint);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Sender_writeFloats(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jfloatArray jsamples) {
    jfloat* samples = NULL;
    jsize len = 0;
    roc_frame frame = {};

    if (jsamples == NULL) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "Bad arguments");
        return;
    }

    samples = env->GetFloatArrayElements(jsamples, 0);
    assert(samples != NULL);

    len = env->GetArrayLength(jsamples);

    memset(&frame, 0, sizeof(frame));

    frame.samples = samples;
    frame.samples_size = len * sizeof(float);

    roc_sender* sender = (roc_sender*) senderPtr;
    if (roc_sender_write(sender, &frame) != 0) {
        jclass exceptionClass = env->FindClass(IO_EXCEPTION);
        env->ThrowNew(exceptionClass, "Error sending frame");
    }

    env->ReleaseFloatArrayElements(jsamples, samples, 0);
}
