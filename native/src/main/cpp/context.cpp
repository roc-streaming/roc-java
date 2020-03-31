#include "com_rocproject_roc_context_Context.h"
#include "common.h"
#include "context.h"

#include <cstring>
#include <cassert>

#define CONTEXT_CLASS               "com/rocproject/roc/context/Context"
#define CONTEXT_CONFIG_CLASS        "com/rocproject/roc/config/ContextConfig"

void context_config_unmarshall(JNIEnv *env, roc_context_config* conf, jobject jconfig) {
    jclass contextConfigClass;

    contextConfigClass = env->FindClass(CONTEXT_CONFIG_CLASS);
    assert(contextConfigClass != NULL);

    memset(conf, 0, sizeof(roc_context_config));

    conf->max_packet_size = (unsigned int) get_int_field_value(env, contextConfigClass, jconfig, "maxPacketSize");
    conf->max_frame_size = (unsigned int) get_int_field_value(env, contextConfigClass, jconfig, "maxFrameSize");
}

JNIEXPORT void JNICALL Java_com_rocproject_roc_context_Context_rocContextOpen(JNIEnv *env, jobject thisObj, jobject config) {
    roc_context*        context;
    roc_context_config  context_config;
    jclass              contextClass;

    if (config == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Wrong context configuration values");
        return;
    }

    contextClass = env->FindClass(CONTEXT_CLASS);
    assert(contextClass != NULL);

    context_config_unmarshall(env, &context_config, config);

    if ((int)context_config.max_packet_size < 0 || (int)context_config.max_frame_size < 0) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Wrong context configuration values");
        return;
    }

    if ((context = roc_context_open(&context_config)) == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/Exception");
        env->ThrowNew(exceptionClass, "Error opening context");
        return;
    }

    set_native_pointer(env, contextClass, thisObj, context);
}

/*
 * Class:     com_rocproject_roc_context_Context
 * Method:    rocContextClose
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_context_Context_rocContextClose(JNIEnv *env, jobject thisObj) {
    roc_context*    context;
    jclass          contextClass;

    contextClass = env->FindClass(CONTEXT_CLASS);
    assert(contextClass != NULL);

    context = (roc_context*) get_native_pointer(env, contextClass, thisObj);

    if (roc_context_close(context) != 0) {
        jclass exceptionClass = env->FindClass("java/io/IOException");
        env->ThrowNew(exceptionClass, "Error closing context");
    }
}

roc_context* get_context(JNIEnv *env, jobject jcontext) {
    if (jcontext == NULL)
        return NULL;

    jclass contextClass;

    contextClass = env->FindClass(CONTEXT_CLASS);
    assert(contextClass != NULL);

    return (roc_context*) get_native_pointer(env, contextClass, jcontext);
}