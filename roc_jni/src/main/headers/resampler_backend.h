#include <jni.h>

#ifndef RESAMPLER_BACKEND_H_
#define RESAMPLER_BACKEND_H_
#ifdef __cplusplus
extern "C" {
#endif

#include "common.h"
#include <roc/config.h>

#define RESAMPLER_BACKEND_CLASS     PACKAGE_BASE_NAME "/ResamplerBackend"

roc_resampler_backend get_resampler_backend(JNIEnv *env, jobject jresampler_backend);

#ifdef __cplusplus
}
#endif
#endif /* RESAMPLER_BACKEND_H_ */