#include "protocol.h"
#include "common.h"

roc_protocol get_protocol(JNIEnv *env, jobject jprotocol) {
    jclass        protocolClass = NULL;

    protocolClass = env->FindClass(PROTOCOL_CLASS);
    assert(protocolClass != NULL);

    return (roc_protocol) get_enum_value(env, protocolClass, jprotocol);
}

jobject get_protocol_enum(JNIEnv *env, roc_protocol protocol) {
    jclass        protocolClass = NULL;
    jobject       jprotocol = NULL;
    jmethodID     getProtocolMethodID = NULL;

    protocolClass = env->FindClass(PROTOCOL_CLASS);
    assert(protocolClass != NULL);

    getProtocolMethodID = env->GetStaticMethodID(protocolClass, "getByValue", "(I)L" PROTOCOL_CLASS ";");
    assert(getProtocolMethodID != NULL);

    jprotocol = (jobject) env->CallStaticObjectMethod(protocolClass, getProtocolMethodID, (jint) protocol);
    assert(jprotocol != NULL);
    return jprotocol;
}