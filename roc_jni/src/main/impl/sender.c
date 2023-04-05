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

#define SENDER_CONFIG_CLASS PACKAGE_BASE_NAME "/SenderConfig"

static int sender_config_unmarshal(JNIEnv* env, roc_sender_config* config, jobject jconfig) {
    jclass senderConfigClass = NULL;
    jobject jobj = NULL;
    int err = 0;

    senderConfigClass = (*env)->FindClass(env, SENDER_CONFIG_CLASS);
    assert(senderConfigClass != NULL);

    // set all fields to zeros
    memset(config, 0, sizeof(*config));

    // frame_sample_rate
    config->frame_sample_rate
        = get_uint_field_value(env, senderConfigClass, jconfig, "frameSampleRate", &err);
    if (err) return err;

    // frame_channels
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "frameChannels", "L" CHANNEL_SET_CLASS ";");
    if (jobj != NULL) config->frame_channels = (roc_channel_set) get_channel_set(env, jobj);

    // frame_encoding
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "frameEncoding", "L" FRAME_ENCODING_CLASS ";");
    if (jobj != NULL) config->frame_encoding = (roc_frame_encoding) get_frame_encoding(env, jobj);

    // packet_sample_rate
    config->packet_sample_rate
        = get_uint_field_value(env, senderConfigClass, jconfig, "packetSampleRate", &err);
    if (err) return err;

    // packet_channels
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "packetChannels", "L" CHANNEL_SET_CLASS ";");
    if (jobj != NULL) config->packet_channels = (roc_channel_set) get_channel_set(env, jobj);

    // packet_encoding
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "packetEncoding", "L" PACKET_ENCODING_CLASS ";");
    if (jobj != NULL)
        config->packet_encoding = (roc_packet_encoding) get_packet_encoding(env, jobj);

    // packet_length
    config->packet_length
        = get_ullong_field_value(env, senderConfigClass, jconfig, "packetLength", &err);
    if (err) return err;

    // packet_interleaving
    config->packet_interleaving
        = get_uint_field_value(env, senderConfigClass, jconfig, "packetInterleaving", &err);
    if (err) return err;

    // clock_source
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "clockSource", "L" CLOCK_SOURCE_CLASS ";");
    if (jobj != NULL) config->clock_source = get_clock_source(env, jobj);

    // resampler_backend
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "resamplerBackend", "L" RESAMPLER_BACKEND_CLASS ";");
    if (jobj != NULL) config->resampler_backend = get_resampler_backend(env, jobj);

    // resampler_profile
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "resamplerProfile", "L" RESAMPLER_PROFILE_CLASS ";");
    if (jobj != NULL)
        config->resampler_profile = (roc_resampler_profile) get_resampler_profile(env, jobj);

    // fec_encoding
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "fecEncoding", "L" FEC_ENCODING_CLASS ";");
    if (jobj != NULL) config->fec_encoding = (roc_fec_encoding) get_fec_encoding(env, jobj);

    // fec_block_source_packets
    config->fec_block_source_packets
        = get_uint_field_value(env, senderConfigClass, jconfig, "fecBlockSourcePackets", &err);
    if (err) return err;

    // fec_block_repair_packets
    config->fec_block_repair_packets
        = get_uint_field_value(env, senderConfigClass, jconfig, "fecBlockRepairPackets", &err);
    if (err) return err;

    return 0;
}

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_Sender_open(
    JNIEnv* env, jclass senderClass, jlong contextPtr, jobject jconfig) {
    roc_context* context = NULL;
    roc_sender_config config = {};
    roc_sender* sender = NULL;

    context = (roc_context*) contextPtr;

    if (sender_config_unmarshal(env, &config, jconfig) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad config argument");
        goto out;
    }

    if ((roc_sender_open(context, &config, &sender)) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error opening sender");
        sender = NULL;
        goto out;
    }

out:
    return (jlong) sender;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Sender_setOutgoingAddress(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jint slot, jint interface, jstring jip) {
    roc_sender* sender = NULL;
    const char* ip = NULL;

    sender = (roc_sender*) senderPtr;

    ip = (*env)->GetStringUTFChars(env, jip, 0);
    assert(ip != NULL);

    if (roc_sender_set_outgoing_address(sender, (roc_slot) slot, (roc_interface) interface, ip)
        != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Can't set outgoing address");
        goto out;
    }

out:
    if (ip != NULL) (*env)->ReleaseStringUTFChars(env, jip, ip);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Sender_connect(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jint slot, jint interface, jobject jendpoint) {
    roc_sender* sender = NULL;
    roc_endpoint* endpoint = NULL;

    sender = (roc_sender*) senderPtr;

    if (jendpoint == NULL) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad endpoint argument");
        goto out;
    }

    if (endpoint_unmarshal(env, &endpoint, jendpoint) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error unmarshalling endpoint");
        goto out;
    }

    if (roc_sender_connect(sender, (roc_slot) slot, (roc_interface) interface, endpoint) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error connecting sender");
        goto out;
    }

out:
    if (endpoint != NULL) {
        roc_endpoint_deallocate(endpoint);
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Sender_writeFloats(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jfloatArray jsamples) {
    roc_sender* sender = NULL;
    jfloat* samples = NULL;
    jsize len = 0;
    roc_frame frame = {};

    sender = (roc_sender*) senderPtr;

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

    if (roc_sender_write(sender, &frame) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error writing frame");
        goto out;
    }

out:
    if (samples != NULL) (*env)->ReleaseFloatArrayElements(env, jsamples, samples, 0);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Sender_close(
    JNIEnv* env, jclass senderClass, jlong senderPtr) {

    roc_sender* sender = (roc_sender*) senderPtr;

    if (roc_sender_close(sender) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error closing sender");
    }
}
