#include "org_rocstreaming_roctoolkit_Endpoint.h"
#include "common.h"
#include "endpoint.h"

#include <roc/config.h>

#define ENDPOINT_CLASS              PACKAGE_BASE_NAME "/Endpoint"
#define PROTOCOL_CLASS              PACKAGE_BASE_NAME "/Protocol"

int endpoint_unmarshal(JNIEnv *env, roc_endpoint** endpoint, jobject jendpoint) {
    jclass          endpointClass;
    jclass          protocolClass;
    jobject         tempObject;
    jstring         jstr;
    roc_protocol    protocol;
    const char*     host;
    int             port;
    const char*     resource;
    char            err = 0;

    if (jendpoint == NULL)
        return -1;

    endpointClass = env->FindClass(ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    protocolClass = env->FindClass(PROTOCOL_CLASS);
    assert(protocolClass != NULL);

    if (roc_endpoint_allocate(endpoint) != 0) return -1;

    tempObject = get_object_field(env, endpointClass, jendpoint, "protocol", "L" PROTOCOL_CLASS ";");
    protocol = (roc_protocol) get_enum_value(env, protocolClass, tempObject);
    if (roc_endpoint_set_protocol(*endpoint, protocol) != 0) return -1;

    jstr = (jstring) get_object_field(env, endpointClass, jendpoint, "host", "Ljava/lang/String;");
    host = env->GetStringUTFChars(jstr, 0);
    assert(host != NULL);
    if (roc_endpoint_set_host(*endpoint, host) != 0) {
        env->ReleaseStringUTFChars(jstr, host);
        return -1;
    }
    env->ReleaseStringUTFChars(jstr, host);

    port = get_int_field_value(env, endpointClass, jendpoint, "port", &err);
    if (err) return err;
    if (roc_endpoint_set_port(*endpoint, port) != 0) return -1;

    jstr = (jstring) get_object_field(env, endpointClass, jendpoint, "resource", "Ljava/lang/String;");
    if (jstr != NULL) {
        resource = env->GetStringUTFChars(jstr, 0);
        if (roc_endpoint_set_resource(*endpoint, resource) != 0) {
            env->ReleaseStringUTFChars(jstr, resource);
            return -1;
        }
        env->ReleaseStringUTFChars(jstr, resource);
    }
    return 0;
}

void endpoint_set_port(JNIEnv *env, jobject endpoint, int port) {
    jclass      endpointClass;
    jfieldID    attrId;

    endpointClass = env->FindClass(ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    attrId = env->GetFieldID(endpointClass, "port", "I");
    assert(attrId != NULL);

    env->SetIntField(endpoint, attrId, port);
}

static const char* protocolMapping(roc_protocol protocol) {
    switch (protocol) {
        case ROC_PROTO_RTSP:
            return "RTSP";
        case ROC_PROTO_RTP:
            return "RTP";
        case ROC_PROTO_RTP_RS8M_SOURCE:
            return "RTP_RS8M_SOURCE";
        case ROC_PROTO_RS8M_REPAIR:
            return "RS8M_REPAIR";
        case ROC_PROTO_RTP_LDPC_SOURCE:
            return "RTP_LDPC_SOURCE";
        case ROC_PROTO_LDPC_REPAIR:
            return "LDPC_REPAIR";
        case ROC_PROTO_RTCP:
            return "RTCP";
        default:
            return NULL;
    }
};

void endpoint_set_protocol(JNIEnv *env, jobject endpoint, roc_protocol protocol) {
    jclass      endpointClass;
    jclass      protocolClass;
    jfieldID    attrId;
    jfieldID    protocolField;
    const char* protocolValue;
    jobject     protocolObj;

    endpointClass = env->FindClass(ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    protocolClass = env->FindClass(PROTOCOL_CLASS);
    assert(protocolClass != NULL);

    attrId = env->GetFieldID(endpointClass, "protocol", "L" PROTOCOL_CLASS ";");
    assert(attrId != NULL);

    protocolValue = protocolMapping(protocol); // todo: maybe better to call java function to get Enum value by roc_protocol?
    protocolField = env->GetStaticFieldID(protocolClass, protocolValue, "L" PROTOCOL_CLASS ";");
    assert(protocolField != NULL);

    protocolObj = env->GetStaticObjectField(protocolClass, protocolField);
    env->SetObjectField(endpoint, attrId, protocolObj);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_init(JNIEnv *env, jobject thisObj, jstring juri) {
    roc_endpoint*   endpoint;
    const char*     uri;
    jclass          endpointClass;
    roc_protocol    protocol;
    int             port;
    jfieldID        hostAttrId;
    jfieldID        portAttrId;
    jfieldID        resourceAttrId;
    char            buf[128];
    size_t          bufsz = sizeof(buf);

    if (juri == NULL) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "Bad uri argument");
        return;
    }

    if (roc_endpoint_allocate(&endpoint) != 0 ) {
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Can't allocate roc_endpoint");
        return;
    };

    uri = env->GetStringUTFChars(juri, 0);
    if (roc_endpoint_set_uri(endpoint, uri) != 0) {
        env->ReleaseStringUTFChars(juri, uri);
        roc_endpoint_deallocate(endpoint);
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "Bad uri arguments");
        return;
    }
    env->ReleaseStringUTFChars(juri, uri);

    endpointClass = env->FindClass(ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    roc_endpoint_get_protocol(endpoint, &protocol);
    endpoint_set_protocol(env, thisObj, protocol);

    roc_endpoint_get_host(endpoint, buf, &bufsz);
    hostAttrId = env->GetFieldID(endpointClass, "host", "Ljava/lang/String;");
    assert(hostAttrId != NULL);
    env->SetObjectField(thisObj, hostAttrId, env->NewStringUTF(buf));

    portAttrId = env->GetFieldID(endpointClass, "port", "I");
    assert(portAttrId != NULL);
    if (roc_endpoint_get_port(endpoint, &port) == 0) {
        env->SetIntField(thisObj, portAttrId, port);
    } else {
        env->SetIntField(thisObj, portAttrId, -1);
    }

    if (roc_endpoint_get_resource(endpoint, buf, &bufsz) == 0) {
        resourceAttrId = env->GetFieldID(endpointClass, "resource", "Ljava/lang/String;");
        assert(resourceAttrId != NULL);
        env->SetObjectField(thisObj, resourceAttrId, env->NewStringUTF(buf));
    }

    roc_endpoint_deallocate(endpoint);
}

JNIEXPORT jstring JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_getUri(JNIEnv *env, jobject thisObj) {
    roc_endpoint*   endpoint;
    char            buf[128];
    size_t          bufsz = sizeof(buf);

    if (roc_endpoint_allocate(&endpoint) != 0 ) {
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Can't allocate roc_endpoint");
        return NULL;
    };

    if (endpoint_unmarshal(env, &endpoint, thisObj) != 0) {
        roc_endpoint_deallocate(endpoint);
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Can't unmarshal roc_endpoint");
        return NULL;
    }

    if (roc_endpoint_get_uri(endpoint, buf, &bufsz) != 0) {
        roc_endpoint_deallocate(endpoint);
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Can't get uri");
        return NULL;
    }

    roc_endpoint_deallocate(endpoint);

    return env->NewStringUTF(buf);
}

