#include "packet_encoding.h"
#include "common.h"

roc_packet_encoding get_packet_encoding(JNIEnv* env, jobject jpacket_encoding) {
    jclass packetEncodingClass = NULL;

    packetEncodingClass = env->FindClass(PACKET_ENCODING_CLASS);
    assert(packetEncodingClass != NULL);

    return (roc_packet_encoding) get_enum_value(env, packetEncodingClass, jpacket_encoding);
}