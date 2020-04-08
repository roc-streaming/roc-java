#include <jni.h>

#ifndef FEC_CODE_H_
#define FEC_CODE_H_
#ifdef __cplusplus
extern "C" {
#endif

#include <roc/config.h>

#define FEC_CODE_CLASS              "com/github/rocproject/roc/FecCode"

roc_fec_code get_fec_code(JNIEnv *env, jobject jfec_code);

#ifdef __cplusplus
}
#endif
#endif /* CHANNEL_SET_H_ */