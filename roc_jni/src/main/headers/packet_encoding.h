#include <jni.h>

#ifndef PACKET_ENCODING_H_
#define PACKET_ENCODING_H_
#ifdef __cplusplus
extern "C" {
#endif

#include <roc/config.h>

#define PACKET_ENCODING_CLASS        "com/github/rocproject/roc/PacketEncoding"

roc_packet_encoding get_packet_encoding(JNIEnv *env, jobject jpacket_encoding);

#ifdef __cplusplus
}
#endif
#endif /* PACKET_ENCODING_H_ */