#include "context_config.h"
#include "common.h"

#include <roc/config.h>

int context_config_unmarshal(JNIEnv* env, roc_context_config* conf, jobject jconfig) {
    jclass contextConfigClass = NULL;
    int err = 0;

    contextConfigClass = (*env)->FindClass(env, CONTEXT_CONFIG_CLASS);
    assert(contextConfigClass != NULL);

    memset(conf, 0, sizeof(roc_context_config));

    conf->max_packet_size
        = get_uint_field_value(env, contextConfigClass, jconfig, "maxPacketSize", &err);
    if (err) return err;

    conf->max_frame_size
        = get_uint_field_value(env, contextConfigClass, jconfig, "maxFrameSize", &err);
    if (err) return err;

    return 0;
}
