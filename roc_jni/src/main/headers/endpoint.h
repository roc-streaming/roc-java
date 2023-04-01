#include <jni.h>

#ifndef ENDPOINT_H_
#define ENDPOINT_H_

#include <roc/endpoint.h>

int endpoint_unmarshal(JNIEnv* env, roc_endpoint** endpoint, jobject jendpoint);

void endpoint_set_port(JNIEnv* env, jobject endpoint, int port);

#endif /* ENDPOINT_H_ */
