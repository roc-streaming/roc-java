#include "com_github_rocproject_roc_Protocol.h"

#include <roc/config.h>

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_Protocol_getRocProtoRTP(JNIEnv *, jclass) {
    return ROC_PROTO_RTP;
}

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_Protocol_getRocProtoRTPRS8MSOURCE(JNIEnv *, jclass) {
    return ROC_PROTO_RTP_RS8M_SOURCE;
}

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_Protocol_getRocProtoRS8MREPAIR(JNIEnv *, jclass) {
    return ROC_PROTO_RS8M_REPAIR;
}

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_Protocol_getRocProtoRTPLDPCSOURCE(JNIEnv *, jclass) {
    return ROC_PROTO_RTP_LDPC_SOURCE;
}

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_Protocol_getRocProtoLDPCREPAIR(JNIEnv *, jclass) {
    return ROC_PROTO_LDPC_REPAIR;
}