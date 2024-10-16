#include "resampler_backend.h"
#include "common.h"

#include <roc/config.h>

roc_resampler_backend get_resampler_backend(JNIEnv* env, jobject jresamplerBackend) {
    jclass resamplerBackendClass = NULL;

    resamplerBackendClass = (*env)->FindClass(env, RESAMPLER_BACKEND_CLASS);
    assert(resamplerBackendClass != NULL);

    return (roc_resampler_backend) get_enum_value(env, resamplerBackendClass, jresamplerBackend);
}
