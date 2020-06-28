#include "org_rocstreaming_roctoolkit_LogLevel.h"

#include <roc/log.h>


JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_LogLevel_getRocLogNone(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_NONE;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_LogLevel_getRocLogError(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_ERROR;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_LogLevel_getRocLogInfo(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_INFO;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_LogLevel_getRocLogDebug(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_DEBUG;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_LogLevel_getRocLogTrace(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_TRACE;
}