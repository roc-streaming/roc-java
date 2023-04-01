#include <jni.h>

#ifndef ENDPOINT_H_
#define ENDPOINT_H_
#ifdef __cplusplus
extern "C" {
#endif

#include <roc/endpoint.h>

int endpoint_unmarshal(JNIEnv *env, roc_endpoint** endpoint, jobject jendpoint);

void endpoint_set_port(JNIEnv *env, jobject endpoint, int port);

#ifdef __cplusplus
}
#endif
#endif /* ENDPOINT_H_ */
