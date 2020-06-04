#include "com_github_rocproject_roc_FecCode.h"

#include "fec_code.h"
#include "common.h"

#include <roc/config.h>

roc_fec_code get_fec_code(JNIEnv *env, jobject jfec_code) {
    jclass fecCodeClass;

    fecCodeClass = env->FindClass(FEC_CODE_CLASS);
    assert(fecCodeClass != NULL);

    return (roc_fec_code) get_enum_value(env, fecCodeClass, jfec_code);
}

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_FecCode_getRocFecCodeDisable(JNIEnv *, jclass) {
    return ROC_FEC_DISABLE;
}

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_FecCode_getRocFecCodeDefault(JNIEnv *, jclass) {
    return ROC_FEC_DEFAULT;
}

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_FecCode_getRocFecCodeRS8M(JNIEnv *, jclass) {
    return ROC_FEC_RS8M;
}

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_FecCode_getRocFecCodeLDPCSTAIRCASE(JNIEnv *, jclass) {
    return ROC_FEC_LDPC_STAIRCASE;
}