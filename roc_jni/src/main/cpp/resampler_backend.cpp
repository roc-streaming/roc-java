#include "resampler_backend.h"
#include "common.h"

#include <roc/config.h>

roc_resampler_backend get_resampler_backend(JNIEnv *env, jobject jresampler_backend) {
    jclass resamplerBackendClass;

    resamplerBackendClass = env->FindClass(RESAMPLER_BACKEND_CLASS);
    assert(resamplerBackendClass != NULL);

    return (roc_resampler_backend) get_enum_value(env, resamplerBackendClass, jresampler_backend);
}
