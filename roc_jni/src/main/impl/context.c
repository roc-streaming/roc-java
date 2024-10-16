#include "org_rocstreaming_roctoolkit_RocContext.h"

#include "common.h"
#include "context_config.h"
#include "media_encoding.h"

#include <roc/context.h>

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_RocContext_nativeOpen(
    JNIEnv* env, jclass contextClass, jobject config) {
    roc_context* context = NULL;
    roc_context_config context_config = {};

    if (context_config_unmarshal(env, &context_config, config) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Wrong context configuration values");
        return (jlong) NULL;
    }

    if (roc_context_open(&context_config, &context) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error opening context");
        return (jlong) NULL;
    }

    return (jlong) context;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocContext_nativeClose(
    JNIEnv* env, jclass contextClass, jlong contextPtr) {
    roc_context* context = (roc_context*) contextPtr;

    if (roc_context_close(context) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error closing context");
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocContext_nativeRegisterEncoding(
    JNIEnv* env, jclass contextClass, jlong contextPtr, jint encodingId, jobject jencoding) {
    roc_context* context = (roc_context*) contextPtr;
    roc_media_encoding encoding = {};

    if (media_encoding_unmarshal(env, &encoding, jencoding) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Invalid MediaEncoding");
        return;
    }

    if (roc_context_register_encoding(context, (int) encodingId, &encoding) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error registering encoding");
        return;
    }
}
