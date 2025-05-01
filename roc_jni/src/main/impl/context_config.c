#include "context_config.h"
#include "helpers.h"
#include "package.h"

#include <assert.h>
#include <string.h>

bool context_config_unmarshal(JNIEnv* env, jobject jconfig, roc_context_config* result) {
    assert(env);
    assert(jconfig);
    assert(result);

    memset(result, 0, sizeof(*result));

    jclass jclass = find_class(env, CONTEXT_CONFIG_CLASS);
    if (!jclass) {
        return false;
    }

    if (!read_uint_field(env, jclass, jconfig, CONTEXT_CONFIG_CLASS, "maxPacketSize",
            &result->max_packet_size)) {
        return false;
    }

    if (!read_uint_field(
            env, jclass, jconfig, CONTEXT_CONFIG_CLASS, "maxFrameSize", &result->max_frame_size)) {
        return false;
    }

    return true;
}
