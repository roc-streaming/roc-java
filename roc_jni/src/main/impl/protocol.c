#include "protocol.h"
#include "common.h"

roc_protocol get_protocol(JNIEnv* env, jobject jprotocol) {
    jclass protocolClass = NULL;

    protocolClass = (*env)->FindClass(env, PROTOCOL_CLASS);
    assert(protocolClass != NULL);

    return (roc_protocol) get_enum_value(env, protocolClass, jprotocol);
}

jobject get_protocol_enum(JNIEnv* env, roc_protocol protocol) {
    jclass protocolUtilsClass = NULL;
    jobject jprotocol = NULL;
    jmethodID getProtocolMethodID = NULL;

    protocolUtilsClass = (*env)->FindClass(env, PROTOCOL_UTILS_CLASS);
    assert(protocolUtilsClass != NULL);

    getProtocolMethodID
        = (*env)->GetStaticMethodID(env, protocolUtilsClass, "getByValue", "(I)L" PROTOCOL_CLASS ";");
    assert(getProtocolMethodID != NULL);

    jprotocol = (jobject) (*env)->CallStaticObjectMethod(
        env, protocolUtilsClass, getProtocolMethodID, (jint) protocol);
    assert(jprotocol != NULL);
    return jprotocol;
}
