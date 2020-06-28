#include "org_rocstreaming_roctoolkit_Family.h"

#include <roc/address.h>

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_Family_getRocAFInvalid(JNIEnv *env, jclass thisObj) {
    return ROC_AF_INVALID;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_Family_getRocAFAuto(JNIEnv *env, jclass thisObj) {
    return ROC_AF_AUTO;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_Family_getRocAFIPv4(JNIEnv *env, jclass thisObj) {
    return ROC_AF_IPv4;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_Family_getRocAFIPv6(JNIEnv *env, jclass thisObj) {
    return ROC_AF_IPv6;
}