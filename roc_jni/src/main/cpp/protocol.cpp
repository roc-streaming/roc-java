#include "protocol.h"
#include "common.h"

roc_protocol get_protocol(JNIEnv *env, jobject jprotocol) {
    jclass        protocolClass;

    protocolClass = env->FindClass(PROTOCOL_CLASS);
    assert(protocolClass != NULL);

    return (roc_protocol) get_enum_value(env, protocolClass, jprotocol);
}