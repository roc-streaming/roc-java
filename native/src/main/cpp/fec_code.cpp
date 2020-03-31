#include "com_rocproject_roc_config_FecCode.h"

#include "fec_code.h"
#include "common.h"

#include <cassert>
#include <roc/config.h>

roc_fec_code get_fec_code(JNIEnv *env, jobject jfec_code) {
    jclass fecCodeClass;

    fecCodeClass = env->FindClass(FEC_CODE_CLASS);
    assert(fecCodeClass != NULL);

    return (roc_fec_code) get_enum_value(env, fecCodeClass, jfec_code);
}

JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_FecCode_getRocFecCodeDisable(JNIEnv *, jclass) {
    return ROC_FEC_DISABLE;
}

/*
 * Class:     com_rocproject_roc_config_FecCode
 * Method:    getRocFecCodeDefault
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_FecCode_getRocFecCodeDefault(JNIEnv *, jclass) {
    return ROC_FEC_DEFAULT;
}

/*
 * Class:     com_rocproject_roc_config_FecCode
 * Method:    getRocFecCodeRS8M
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_FecCode_getRocFecCodeRS8M(JNIEnv *, jclass) {
    return ROC_FEC_RS8M;
}

/*
 * Class:     com_rocproject_roc_config_FecCode
 * Method:    getRocFecCodeLDPCSTAIRCASE
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_FecCode_getRocFecCodeLDPCSTAIRCASE(JNIEnv *, jclass) {
    return ROC_FEC_LDPC_STAIRCASE;
}