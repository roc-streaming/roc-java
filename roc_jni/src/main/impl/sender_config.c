#include "sender_config.h"
#include "endpoint.h"
#include "exceptions.h"
#include "helpers.h"
#include "media_encoding.h"

#include <assert.h>
#include <string.h>

bool sender_config_unmarshal(JNIEnv* env, jobject jconfig, roc_sender_config* result) {
    assert(env);
    assert(jconfig);
    assert(result);

    memset(result, 0, sizeof(*result));

    jclass jclass = find_class(env, SENDER_CONFIG_CLASS);
    if (!jclass) {
        return false;
    }

    jobject jencoding = NULL;
    int enum_value = 0;

    // frame_encoding
    if (!read_object_field(env, jclass, jconfig, SENDER_CONFIG_CLASS, "frameEncoding",
            MEDIA_ENCODING_CLASS, &jencoding)) {
        return false;
    }
    if (jencoding) {
        if (!media_encoding_unmarshal(env, jencoding, &result->frame_encoding)) {
            return false;
        }
    }

    // packet_encoding
    if (!read_enum_field(env, jclass, jconfig, SENDER_CONFIG_CLASS, "packetEncoding",
            PACKET_ENCODING_CLASS, &enum_value)) {
        return false;
    }
    result->packet_encoding = (roc_packet_encoding) enum_value;

    // packet_length
    if (!read_unsigned_duration_field(
            env, jclass, jconfig, SENDER_CONFIG_CLASS, "packetLength", &result->packet_length)) {
        return false;
    }

    // packet_interleaving
    if (!read_uint_field(env, jclass, jconfig, SENDER_CONFIG_CLASS, "packetInterleaving",
            &result->packet_interleaving)) {
        return false;
    }

    // fec_encoding
    if (!read_enum_field(env, jclass, jconfig, SENDER_CONFIG_CLASS, "fecEncoding",
            FEC_ENCODING_CLASS, &enum_value)) {
        return false;
    }
    result->fec_encoding = (roc_fec_encoding) enum_value;

    // fec_block_source_packets
    if (!read_uint_field(env, jclass, jconfig, SENDER_CONFIG_CLASS, "fecBlockSourcePackets",
            &result->fec_block_source_packets)) {
        return false;
    }

    // fec_block_repair_packets
    if (!read_uint_field(env, jclass, jconfig, SENDER_CONFIG_CLASS, "fecBlockRepairPackets",
            &result->fec_block_repair_packets)) {
        return false;
    }

    // clock_source
    if (!read_enum_field(env, jclass, jconfig, SENDER_CONFIG_CLASS, "clockSource",
            CLOCK_SOURCE_CLASS, &enum_value)) {
        return false;
    }
    result->clock_source = (roc_clock_source) enum_value;

    // resampler_backend
    if (!read_enum_field(env, jclass, jconfig, SENDER_CONFIG_CLASS, "resamplerBackend",
            RESAMPLER_BACKEND_CLASS, &enum_value)) {
        return false;
    }
    result->resampler_backend = (roc_resampler_backend) enum_value;

    // resampler_profile
    if (!read_enum_field(env, jclass, jconfig, SENDER_CONFIG_CLASS, "resamplerProfile",
            RESAMPLER_PROFILE_CLASS, &enum_value)) {
        return false;
    }
    result->resampler_profile = (roc_resampler_profile) enum_value;

    return true;
}
