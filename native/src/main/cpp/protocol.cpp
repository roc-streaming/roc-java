#include "com_rocproject_roc_config_Protocol.h"

#include <roc/config.h>

JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_Protocol_getRocProtoRTP(JNIEnv *, jclass) {
    return ROC_PROTO_RTP;
}

/*
 * Class:     com_rocproject_roc_config_Protocol
 * Method:    getRocProtoRTPRS8MSOURCE
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_Protocol_getRocProtoRTPRS8MSOURCE(JNIEnv *, jclass) {
    return ROC_PROTO_RTP_RS8M_SOURCE;
}

/*
 * Class:     com_rocproject_roc_config_Protocol
 * Method:    getRocProtoRS8MREPAIR
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_Protocol_getRocProtoRS8MREPAIR(JNIEnv *, jclass) {
    return ROC_PROTO_RS8M_REPAIR;
}

/*
 * Class:     com_rocproject_roc_config_Protocol
 * Method:    getRocProtoRTPLDPCSOURCE
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_Protocol_getRocProtoRTPLDPCSOURCE(JNIEnv *, jclass) {
    return ROC_PROTO_RTP_LDPC_SOURCE;
}

/*
 * Class:     com_rocproject_roc_config_Protocol
 * Method:    getRocProtoLDPCREPAIR
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_Protocol_getRocProtoLDPCREPAIR(JNIEnv *, jclass) {
    return ROC_PROTO_LDPC_REPAIR;
}