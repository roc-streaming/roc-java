#include "interface_config.h"
#include "exceptions.h"
#include "helpers.h"
#include "package.h"

#include <assert.h>
#include <string.h>

bool interface_config_unmarshal(JNIEnv* env, jobject jconfig, roc_interface_config* result) {
    assert(env);
    assert(jconfig);
    assert(result);

    memset(result, 0, sizeof(*result));

    jclass jclass = find_class(env, INTERFACE_CONFIG_CLASS);
    if (!jclass) {
        return false;
    }

    // outgoing_address
    if (!read_string_field(env, jclass, jconfig, INTERFACE_CONFIG_CLASS, "outgoingAddress",
            result->outgoing_address, sizeof(result->outgoing_address))) {
        return false;
    }

    // multicast_group
    if (!read_string_field(env, jclass, jconfig, INTERFACE_CONFIG_CLASS, "multicastGroup",
            result->multicast_group, sizeof(result->multicast_group))) {
        return false;
    }

    // reuse_address
    if (!read_bool_field(
            env, jclass, jconfig, INTERFACE_CONFIG_CLASS, "reuseAddress", &result->reuse_address)) {
        return false;
    }

    return true;
}
