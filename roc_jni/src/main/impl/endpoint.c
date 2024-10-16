#include "org_rocstreaming_roctoolkit_Endpoint.h"

#include "common.h"
#include "endpoint.h"
#include "protocol.h"

#include <roc/config.h>

#include <stdlib.h>

int endpoint_unmarshal(JNIEnv* env, roc_endpoint** endpoint, jobject jendpoint) {
    jclass endpointClass = NULL;
    jobject jprotocol = NULL;
    roc_protocol protocol = (roc_protocol) 0;
    jstring jhost = NULL;
    const char* host = NULL;
    int port = 0;
    jstring jresource = NULL;
    const char* resource = NULL;
    int err = 0;

    // endpoint
    endpointClass = (*env)->FindClass(env, ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    assert(endpoint != NULL);
    assert(*endpoint == NULL);

    if (jendpoint == NULL) {
        err = -1;
        goto out;
    }
    if ((err = roc_endpoint_allocate(endpoint)) != 0) {
        *endpoint = NULL;
        goto out;
    }

    // protocol
    jprotocol = get_object_field(env, endpointClass, jendpoint, "protocol", "L" PROTOCOL_CLASS ";");
    if (jprotocol != NULL) {
        protocol = get_protocol(env, jprotocol);
        if ((err = roc_endpoint_set_protocol(*endpoint, protocol)) != 0) goto out;
    }

    // host
    jhost = (jstring) get_object_field(env, endpointClass, jendpoint, "host", "Ljava/lang/String;");
    if (jhost != NULL) {
        host = (*env)->GetStringUTFChars(env, jhost, 0);
        if (host == NULL) {
            err = -1;
            goto out;
        }
        if ((err = roc_endpoint_set_host(*endpoint, host)) != 0) goto out;
    }

    // port
    port = get_int_field_value(env, endpointClass, jendpoint, "port", &err);
    if (err) goto out;
    if ((err = roc_endpoint_set_port(*endpoint, port)) != 0) goto out;

    // resource
    jresource = (jstring) get_object_field(
        env, endpointClass, jendpoint, "resource", "Ljava/lang/String;");
    if (jresource != NULL) {
        resource = (*env)->GetStringUTFChars(env, jresource, 0);
        if (resource == NULL) {
            err = -1;
            goto out;
        }
        if ((err = roc_endpoint_set_resource(*endpoint, resource)) != 0) goto out;
    }

out:
    if (host != NULL) (*env)->ReleaseStringUTFChars(env, jhost, host);
    if (resource != NULL) (*env)->ReleaseStringUTFChars(env, jresource, resource);

    if (err != 0 && *endpoint != NULL) {
        roc_endpoint_deallocate(*endpoint);
        *endpoint = NULL;
    }

    return err;
}

void endpoint_set_protocol(JNIEnv* env, jobject endpoint, roc_protocol protocol) {
    jclass endpointClass = NULL;
    jfieldID attrId = NULL;
    jobject protocolObj = NULL;

    endpointClass = (*env)->FindClass(env, ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    attrId = (*env)->GetFieldID(env, endpointClass, "protocol", "L" PROTOCOL_CLASS ";");
    assert(attrId != NULL);

    protocolObj = get_protocol_enum(env, protocol);
    assert(protocolObj != NULL);
    (*env)->SetObjectField(env, endpoint, attrId, protocolObj);
}

void endpoint_set_host(JNIEnv* env, jobject endpoint, const char* buf) {
    jclass endpointClass = NULL;
    jfieldID attrId = NULL;

    endpointClass = (*env)->FindClass(env, ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    attrId = (*env)->GetFieldID(env, endpointClass, "host", "Ljava/lang/String;");
    assert(attrId != NULL);
    (*env)->SetObjectField(env, endpoint, attrId, (*env)->NewStringUTF(env, buf));
}

void endpoint_set_port(JNIEnv* env, jobject endpoint, int port) {
    jclass endpointClass = NULL;
    jfieldID attrId = NULL;

    endpointClass = (*env)->FindClass(env, ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    attrId = (*env)->GetFieldID(env, endpointClass, "port", "I");
    assert(attrId != NULL);
    (*env)->SetIntField(env, endpoint, attrId, port);
}

void endpoint_set_resource(JNIEnv* env, jobject endpoint, const char* buf) {
    jclass endpointClass = NULL;
    jfieldID attrId = NULL;

    endpointClass = (*env)->FindClass(env, ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    attrId = (*env)->GetFieldID(env, endpointClass, "resource", "Ljava/lang/String;");
    assert(attrId != NULL);
    (*env)->SetObjectField(env, endpoint, attrId, (*env)->NewStringUTF(env, buf));
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_init(
    JNIEnv* env, jobject thisObj, jstring juri) {
    jclass endpointClass = NULL;
    roc_endpoint* endpoint = NULL;
    const char* uri = NULL;
    roc_protocol protocol = (roc_protocol) 0;
    char* host = NULL;
    size_t hostSz = 0;
    int port = 0;
    char* resource = NULL;
    size_t resourceSz = 0;

    // endpoint
    endpointClass = (*env)->FindClass(env, ENDPOINT_CLASS);
    assert(endpointClass != NULL);

    if (roc_endpoint_allocate(&endpoint) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Can't allocate roc_endpoint");
        endpoint = NULL;
        goto out;
    };

    // uri
    if (juri == NULL) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad uri argument");
        goto out;
    }
    uri = (*env)->GetStringUTFChars(env, juri, 0);
    if (uri == NULL) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad uri argument");
        goto out;
    }
    if (roc_endpoint_set_uri(endpoint, uri) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad uri argument");
        goto out;
    }

    // protocol
    if (roc_endpoint_get_protocol(endpoint, &protocol) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Can't get protocol from endpoint");
        goto out;
    }
    endpoint_set_protocol(env, thisObj, protocol);

    // host
    if (roc_endpoint_get_host(endpoint, NULL, &hostSz) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Can't get host from endpoint");
        goto out;
    }
    host = (char*) malloc(hostSz);
    if (roc_endpoint_get_host(endpoint, host, &hostSz) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Can't get host from endpoint");
        goto out;
    }
    endpoint_set_host(env, thisObj, host);

    // port
    if (roc_endpoint_get_port(endpoint, &port) == 0) {
        endpoint_set_port(env, thisObj, port);
    } else {
        endpoint_set_port(env, thisObj, -1);
    }

    // resource
    if (roc_endpoint_get_resource(endpoint, NULL, &resourceSz) == 0) {
        resource = (char*) malloc(resourceSz);
        if (roc_endpoint_get_resource(endpoint, resource, &resourceSz) != 0) {
            jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
            (*env)->ThrowNew(env, exceptionClass, "Can't get resource from endpoint");
            goto out;
        }
        endpoint_set_resource(env, thisObj, resource);
    }

out:
    if (juri != NULL && uri != NULL) (*env)->ReleaseStringUTFChars(env, juri, uri);

    free(host);
    free(resource);

    if (endpoint != NULL) {
        roc_endpoint_deallocate(endpoint);
    }
}

JNIEXPORT jstring JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_getUri(
    JNIEnv* env, jobject thisObj) {
    roc_endpoint* endpoint = NULL;
    jstring juri = NULL;
    char* uri = NULL;
    size_t uriSz = 0;

    if (endpoint_unmarshal(env, &endpoint, thisObj) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Can't unmarshal roc_endpoint");
        goto out;
    }

    if (roc_endpoint_get_uri(endpoint, NULL, &uriSz) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Can't get uri from endpoint");
        goto out;
    }
    uri = (char*) malloc(uriSz);
    if (roc_endpoint_get_uri(endpoint, uri, &uriSz) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Can't get uri from endpoint");
        goto out;
    }

    assert(uri != NULL);
    juri = (*env)->NewStringUTF(env, uri);

out:
    if (endpoint != NULL) {
        roc_endpoint_deallocate(endpoint);
    }
    free(uri);

    return juri;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_validate(
    JNIEnv* env, jobject thisObj) {
    roc_endpoint* endpoint = NULL;
    char* uri = NULL;
    size_t uriSz = 0;

    if (endpoint_unmarshal(env, &endpoint, thisObj) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Invalid roc_endpoint");
        goto out;
    }

    if (roc_endpoint_get_uri(endpoint, NULL, &uriSz) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Invalid roc_endpoint");
        goto out;
    }
    uri = (char*) malloc(uriSz);
    if (roc_endpoint_get_uri(endpoint, uri, &uriSz) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Invalid roc_endpoint");
        goto out;
    }

out:
    if (endpoint != NULL) {
        roc_endpoint_deallocate(endpoint);
    }
    free(uri);
}
