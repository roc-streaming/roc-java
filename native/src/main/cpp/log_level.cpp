#include "com_rocproject_roc_log_LogLevel.h"

#include <roc/log.h>

/*
 * Class:     com_rocproject_roc_log_LogLevel
 * Method:    getRocLogNone
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_log_LogLevel_getRocLogNone(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_NONE;
}

/*
 * Class:     com_rocproject_roc_log_LogLevel
 * Method:    getRocLogError
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_log_LogLevel_getRocLogError(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_ERROR;
}

/*
 * Class:     com_rocproject_roc_log_LogLevel
 * Method:    getRocLogInfo
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_log_LogLevel_getRocLogInfo(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_INFO;
}

/*
 * Class:     com_rocproject_roc_log_LogLevel
 * Method:    getRocLogDebug
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_log_LogLevel_getRocLogDebug(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_DEBUG;
}

/*
 * Class:     com_rocproject_roc_log_LogLevel
 * Method:    getRocLogTrace
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_log_LogLevel_getRocLogTrace(JNIEnv *env, jclass thisObj) {
    return ROC_LOG_TRACE;
}