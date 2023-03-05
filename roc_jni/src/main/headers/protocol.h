#include <jni.h>


#ifndef PROTOCOL_H_
#define PROTOCOL_H_
#ifdef __cplusplus
extern "C" {
#endif

#include "common.h"
#include <roc/config.h>

#define PROTOCOL_CLASS              PACKAGE_BASE_NAME "/Protocol"

roc_protocol get_protocol(JNIEnv *env, jobject jprotocol);

#ifdef __cplusplus
}
#endif
#endif /* PROTOCOL_H_ */