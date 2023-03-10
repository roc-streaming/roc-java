#include <stdlib.h>

#include "org_rocstreaming_roctoolkit_Endpoint.h"
#include "common.h"
#include "endpoint.h"
#include "protocol.h"

#include <roc/config.h>

#define ENDPOINT_CLASS              PACKAGE_BASE_NAME "/Endpoint"

int endpoint_unmarshal(JNIEnv *env, roc_endpoint** endpoint, jobject jendpoint) {
    jclass          endpointClass = NULL;
    jobject         tempObject = NULL;
    jstring         jstr = NULL;
    roc_protocol    protocol = (roc_protocol)0;
    const char*     host = NULL;
    int             port = 0;
    const char*     resource = NULL;
    char            err = 0;

    assert(*endpoint == NULL);
    if (jendpoint == NULL)
        return -1;

    endpointClass = env->FindClass(ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    tempObject = get_object_field(env, endpointClass, jendpoint, "protocol", "L" PROTOCOL_CLASS ";");
    protocol = get_protocol(env, tempObject);

    if ((err = roc_endpoint_allocate(endpoint)) != 0) return err;
    if ((err = roc_endpoint_set_protocol(*endpoint, protocol)) != 0) {
        roc_endpoint_deallocate(*endpoint);
        *endpoint = NULL;
        return err;
    }

    jstr = (jstring) get_object_field(env, endpointClass, jendpoint, "host", "Ljava/lang/String;");
    host = env->GetStringUTFChars(jstr, 0);
    assert(host != NULL);
    if ((err = roc_endpoint_set_host(*endpoint, host)) != 0) {
        env->ReleaseStringUTFChars(jstr, host);
        roc_endpoint_deallocate(*endpoint);
        *endpoint = NULL;
        return err;
    }
    env->ReleaseStringUTFChars(jstr, host);

    port = get_int_field_value(env, endpointClass, jendpoint, "port", &err);
    if (err) {
        roc_endpoint_deallocate(*endpoint);
        *endpoint = NULL;
        return err;
    }
    if ((err = roc_endpoint_set_port(*endpoint, port)) != 0) {
        roc_endpoint_deallocate(*endpoint);
        *endpoint = NULL;
        return err;
    }

    jstr = (jstring) get_object_field(env, endpointClass, jendpoint, "resource", "Ljava/lang/String;");
    if (jstr != NULL) {
        resource = env->GetStringUTFChars(jstr, 0);
        if ((err = roc_endpoint_set_resource(*endpoint, resource)) != 0) {
            env->ReleaseStringUTFChars(jstr, resource);
            roc_endpoint_deallocate(*endpoint);
            *endpoint = NULL;
            return err;
        }
        env->ReleaseStringUTFChars(jstr, resource);
    }
    return 0;
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
    jclass      endpointClass = NULL;
    jclass      protocolClass = NULL;
    jfieldID    attrId = NULL;
    jfieldID    protocolField = NULL;
    const char* protocolValue = NULL;
    jobject     protocolObj = NULL;

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
    assert(protocolObj != NULL);
    env->SetObjectField(endpoint, attrId, protocolObj);
}

void endpoint_set_host(JNIEnv *env, jobject endpoint, char* buf) {
    jclass      endpointClass = NULL;
    jfieldID    attrId = NULL;

    endpointClass = env->FindClass(ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    attrId = env->GetFieldID(endpointClass, "host", "Ljava/lang/String;");
    assert(attrId != NULL);
    env->SetObjectField(endpoint, attrId, env->NewStringUTF(buf));
}

void endpoint_set_port(JNIEnv *env, jobject endpoint, int port) {
    jclass      endpointClass = NULL;
    jfieldID    attrId = NULL;

    endpointClass = env->FindClass(ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    attrId = env->GetFieldID(endpointClass, "port", "I");
    assert(attrId != NULL);
    env->SetIntField(endpoint, attrId, port);
}

void endpoint_set_resource(JNIEnv *env, jobject endpoint, char* buf) {
    jclass      endpointClass = NULL;
    jfieldID    attrId = NULL;

    endpointClass = env->FindClass(ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    attrId = env->GetFieldID(endpointClass, "resource", "Ljava/lang/String;");
    assert(attrId != NULL);
    env->SetObjectField(endpoint, attrId, env->NewStringUTF(buf));
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_init(JNIEnv *env, jobject thisObj, jstring juri) {
    roc_endpoint*   endpoint = NULL;
    const char*     uri = NULL;
    jclass          endpointClass = NULL;
    roc_protocol    protocol = (roc_protocol)0;
    int             port = 0;
    char*           buf = NULL;
    size_t          bufsz = 0;

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

    roc_endpoint_get_host(endpoint, NULL, &bufsz);
    buf = (char*)malloc(bufsz);
    roc_endpoint_get_host(endpoint, buf, &bufsz);
    endpoint_set_host(env, thisObj, buf);
    free(buf);

    if (roc_endpoint_get_port(endpoint, &port) == 0) {
        endpoint_set_port(env, thisObj, port);
    } else {
        endpoint_set_port(env, thisObj, -1);
    }

    if (roc_endpoint_get_resource(endpoint, NULL, &bufsz) == 0) {
        buf = (char*)malloc(bufsz);
        if (roc_endpoint_get_resource(endpoint, buf, &bufsz) == 0) {
            endpoint_set_resource(env, thisObj, buf);
        }
        free(buf);
    }

    roc_endpoint_deallocate(endpoint);
}

JNIEXPORT jstring JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_getUri(JNIEnv *env, jobject thisObj) {
    roc_endpoint*   endpoint = NULL;
    jstring         jstr = NULL;
    char*           buf = NULL;
    size_t          bufsz = 0;

    if (endpoint_unmarshal(env, &endpoint, thisObj) != 0) {
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Can't unmarshal roc_endpoint");
        return NULL;
    }

    if (roc_endpoint_get_uri(endpoint, NULL, &bufsz) != 0) {
        roc_endpoint_deallocate(endpoint);
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Can't get uri");
        return NULL;
    }
    buf = (char*)malloc(bufsz);
    if (roc_endpoint_get_uri(endpoint, buf, &bufsz) != 0) {
        roc_endpoint_deallocate(endpoint);
        jclass exceptionClass = env->FindClass(EXCEPTION);
        env->ThrowNew(exceptionClass, "Can't get uri");
        return NULL;
    }

    jstr = env->NewStringUTF(buf);
    roc_endpoint_deallocate(endpoint);
    free(buf);
    return jstr;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_validate(JNIEnv *env, jobject thisObj, jint protocol, jstring host, jint port, jstring resource) {
  roc_endpoint* endpoint = NULL;
  if (endpoint_unmarshal(env, &endpoint, thisObj) != 0) {
      jclass exceptionClass = env->FindClass(EXCEPTION);
      env->ThrowNew(exceptionClass, "Invalid roc_endpoint");
      return;
  }
  roc_endpoint_deallocate(endpoint);
}