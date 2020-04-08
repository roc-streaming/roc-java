#include <jni.h>

#ifndef _CONTEXT
#define _CONTEXT
#ifdef __cplusplus
extern "C" {
#endif

#include <roc/config.h>
#include <roc/context.h>

void context_config_unmarshall(JNIEnv *env, roc_context_config* conf, jobject jconfig);

roc_context* get_context(JNIEnv *env, jobject jcontext);

#ifdef __cplusplus
}
#endif
#endif