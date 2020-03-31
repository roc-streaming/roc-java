#include "com_rocproject_roc_address_Family.h"

#include <roc/address.h>

JNIEXPORT jint JNICALL Java_com_rocproject_roc_address_Family_getRocAFInvalid(JNIEnv *env, jclass thisObj) {
    return ROC_AF_INVALID;
}

/*
 * Class:     com_rocproject_roc_address_Family
 * Method:    getRocAFAuto
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_address_Family_getRocAFAuto(JNIEnv *env, jclass thisObj) {
    return ROC_AF_AUTO;
}

/*
 * Class:     com_rocproject_roc_address_Family
 * Method:    getRocAFIPv4
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_address_Family_getRocAFIPv4(JNIEnv *env, jclass thisObj) {
    return ROC_AF_IPv4;
}

/*
 * Class:     com_rocproject_roc_address_Family
 * Method:    getRocAFIPv6
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_address_Family_getRocAFIPv6(JNIEnv *env, jclass thisObj) {
    return ROC_AF_IPv6;
}