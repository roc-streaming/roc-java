#pragma once

#include <jni.h>

#include <roc/endpoint.h>

#define ENDPOINT_CLASS PACKAGE_BASE_NAME "/Endpoint"

int endpoint_unmarshal(JNIEnv* env, roc_endpoint** endpoint, jobject jendpoint);

void endpoint_set_port(JNIEnv* env, jobject endpoint, int port);
