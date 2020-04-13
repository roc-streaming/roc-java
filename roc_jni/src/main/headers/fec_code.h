#include <jni.h>

#ifndef FEC_CODE_H_
#define FEC_CODE_H_
#ifdef __cplusplus
extern "C" {
#endif

#include "common.h"
#include <roc/config.h>

#define FEC_CODE_CLASS              PACKAGE_BASE_NAME "/FecCode"

roc_fec_code get_fec_code(JNIEnv *env, jobject jfec_code);

#ifdef __cplusplus
}
#endif
#endif /* CHANNEL_SET_H_ */