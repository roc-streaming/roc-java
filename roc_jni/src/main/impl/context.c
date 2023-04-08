#include "org_rocstreaming_roctoolkit_RocContext.h"

#include "common.h"

#include <roc/context.h>

#define CONTEXT_CONFIG_CLASS PACKAGE_BASE_NAME "/RocContextConfig"

static int context_config_unmarshal(JNIEnv* env, roc_context_config* conf, jobject jconfig) {
    jclass contextConfigClass = NULL;
    int err = 0;

    contextConfigClass = (*env)->FindClass(env, CONTEXT_CONFIG_CLASS);
    assert(contextConfigClass != NULL);

    memset(conf, 0, sizeof(roc_context_config));

    conf->max_packet_size
        = get_uint_field_value(env, contextConfigClass, jconfig, "maxPacketSize", &err);
    if (err) return err;

    conf->max_frame_size
        = get_uint_field_value(env, contextConfigClass, jconfig, "maxFrameSize", &err);
    if (err) return err;

    return 0;
}

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_RocContext_open(
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

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocContext_close(
    JNIEnv* env, jclass contextClass, jlong nativePtr) {

    roc_context* context = (roc_context*) nativePtr;

    if (roc_context_close(context) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error closing context");
    }
}
