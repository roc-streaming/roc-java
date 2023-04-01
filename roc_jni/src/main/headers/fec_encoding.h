#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define FEC_ENCODING_CLASS PACKAGE_BASE_NAME "/FecEncoding"

roc_fec_encoding get_fec_encoding(JNIEnv* env, jobject jfec_encoding);
