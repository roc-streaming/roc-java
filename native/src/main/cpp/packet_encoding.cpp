#include "com_rocproject_roc_config_PacketEncoding.h"

#include "packet_encoding.h"
#include "common.h"

#include <cassert>

roc_packet_encoding get_packet_encoding(JNIEnv *env, jobject jpacket_encoding) {
    jclass packetEncodingClass;

    packetEncodingClass = env->FindClass(PACKET_ENCODING_CLASS);
    assert(packetEncodingClass != NULL);

    return (roc_packet_encoding) get_enum_value(env, packetEncodingClass, jpacket_encoding);
}

JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_PacketEncoding_getRocPacketEncodingAVPL16(JNIEnv *, jclass) {
    return ROC_PACKET_ENCODING_AVP_L16;
}