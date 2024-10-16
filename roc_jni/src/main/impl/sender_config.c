#include "sender_config.h"
#include "clock_source.h"
#include "common.h"
#include "endpoint.h"
#include "fec_encoding.h"
#include "format.h"
#include "media_encoding.h"
#include "packet_encoding.h"
#include "resampler_backend.h"
#include "resampler_profile.h"

int sender_config_unmarshal(JNIEnv* env, roc_sender_config* config, jobject jconfig) {
    jclass senderConfigClass = NULL;
    jobject jobj = NULL;
    int err = 0;

    senderConfigClass = (*env)->FindClass(env, SENDER_CONFIG_CLASS);
    assert(senderConfigClass != NULL);

    // set all fields to zeros
    assert(config != NULL);
    memset(config, 0, sizeof(*config));

    // frame_encoding
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "frameEncoding", "L" MEDIA_ENCODING_CLASS ";");
    if (jobj != NULL) {
        err = media_encoding_unmarshal(env, &config->frame_encoding, jobj);
        if (err) return err;
    }

    // packet_encoding
    jobj = get_object_field(
        env, senderConfigClass, jconfig, "packetEncoding", "L" PACKET_ENCODING_CLASS ";");
    if (jobj != NULL) config->packet_encoding = get_packet_encoding(env, jobj);

    // packet_length
    config->packet_length
        = get_duration_field_value(env, senderConfigClass, jconfig, "packetLength", &err);
    if (err) return err;

    // packet_interleaving
    config->packet_interleaving
        = get_uint_field_value(env, senderConfigClass, jconfig, "packetInterleaving", &err);
    if (err) return err;

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

    return 0;
}
