#include "org_rocstreaming_roctoolkit_Endpoint.h"

#include "endpoint.h"
#include "exceptions.h"
#include "helpers.h"

#include <roc/endpoint.h>

#include <assert.h>
#include <stdlib.h>
#include <string.h>

bool endpoint_unmarshal(JNIEnv* env, jobject jendpoint, roc_endpoint** result) {
    assert(env);
    assert(jendpoint);
    assert(result);

    jclass jclass = NULL;
    jobject jhost = NULL;
    jobject jresource = NULL;

    const char* host_str = NULL;
    const char* resource_str = NULL;

    int port_value = 0;
    int enum_value = 0;

    bool success = false;

    *result = NULL;

    jclass = find_class(env, ENDPOINT_CLASS);
    if (!jclass) {
        goto out;
    }

    if (roc_endpoint_allocate(result) != 0) {
        throw_exception(env, ASSERTION_ERROR, "Failed to allocate endpoint");
        goto out;
    }

    // protocol
    if (!read_enum_field(
            env, jclass, jendpoint, ENDPOINT_CLASS, "protocol", PROTOCOL_CLASS, &enum_value)) {
        goto out;
    }
    if (enum_value != 0) {
        if (roc_endpoint_set_protocol(*result, (roc_protocol) enum_value) != 0) {
            throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint protocol");
            goto out;
        }
    }

    // host
    if (!read_object_field(
            env, jclass, jendpoint, ENDPOINT_CLASS, "host", "java/lang/String", &jhost)) {
        goto out;
    }
    if (jhost) {
        host_str = (*env)->GetStringUTFChars(env, jhost, 0);
        if (roc_endpoint_set_host(*result, host_str) != 0) {
            throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint host");
            goto out;
        }
    }

    // port
    if (!read_int_field(env, jclass, jendpoint, ENDPOINT_CLASS, "port", &port_value)) {
        goto out;
    }
    if (roc_endpoint_set_port(*result, port_value) != 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint port");
        goto out;
    }

    // resource
    if (!read_object_field(
            env, jclass, jendpoint, ENDPOINT_CLASS, "resource", "java/lang/String", &jresource)) {
        goto out;
    }
    if (jresource) {
        resource_str = (*env)->GetStringUTFChars(env, jresource, 0);
        if (roc_endpoint_set_resource(*result, resource_str) != 0) {
            throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint resource");
            goto out;
        }
    }

    success = true;

out:
    if (host_str) {
        (*env)->ReleaseStringUTFChars(env, jhost, host_str);
    }
    if (resource_str) {
        (*env)->ReleaseStringUTFChars(env, jresource, resource_str);
    }

    if (!success && *result) {
        roc_endpoint_deallocate(*result);
        *result = NULL;
    }

    return success;
}

bool endpoint_set_protocol(JNIEnv* env, jobject jendpoint, roc_protocol value) {
    assert(env);
    assert(jendpoint);

    jclass jendpoint_class = find_class(env, ENDPOINT_CLASS);
    if (!jendpoint_class) {
        return false;
    }

    jfieldID jprotocol_field
        = find_field(env, jendpoint_class, ENDPOINT_CLASS, "protocol", PROTOCOL_CLASS);
    if (!jprotocol_field) {
        return false;
    }

    jclass jprotocol_class = find_class(env, PROTOCOL_CLASS);
    if (!jprotocol_class) {
        return false;
    }

    jobject jprotocol_value = find_enum_constant(env, jprotocol_class, PROTOCOL_CLASS, (int) value);
    if (!jprotocol_value) {
        return false;
    }

    (*env)->SetObjectField(env, jendpoint, jprotocol_field, jprotocol_value);
    return true;
}

bool endpoint_set_host(JNIEnv* env, jobject jendpoint, const char* value) {
    assert(env);
    assert(jendpoint);

    jclass jclass = find_class(env, ENDPOINT_CLASS);
    if (!jclass) {
        return false;
    }

    jfieldID jfid = find_field(env, jclass, ENDPOINT_CLASS, "host", "java/lang/String");
    if (!jfid) {
        return false;
    }

    jobject jvalue = value ? (*env)->NewStringUTF(env, value) : NULL;

    (*env)->SetObjectField(env, jendpoint, jfid, jvalue);
    return true;
}

bool endpoint_set_port(JNIEnv* env, jobject jendpoint, int value) {
    assert(env);
    assert(jendpoint);

    jclass jclass = find_class(env, ENDPOINT_CLASS);
    if (!jclass) {
        return false;
    }

    jfieldID jfid = find_field(env, jclass, ENDPOINT_CLASS, "port", "I");
    if (!jfid) {
        return false;
    }

    (*env)->SetIntField(env, jendpoint, jfid, value);
    return true;
}

bool endpoint_set_resource(JNIEnv* env, jobject jendpoint, const char* value) {
    assert(env);
    assert(jendpoint);

    jclass jclass = find_class(env, ENDPOINT_CLASS);
    if (!jclass) {
        return false;
    }

    jfieldID jfid = find_field(env, jclass, ENDPOINT_CLASS, "resource", "java/lang/String");
    if (!jfid) {
        return false;
    }

    jobject jvalue = value ? (*env)->NewStringUTF(env, value) : NULL;

    (*env)->SetObjectField(env, jendpoint, jfid, jvalue);
    return true;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_nativeParseUri(
    JNIEnv* env, jobject jobj, jstring juri) {
    assert(env);

    jclass jclass = NULL;
    roc_endpoint* endpoint = NULL;
    const char* uri = NULL;
    roc_protocol protocol = (roc_protocol) 0;
    char* host = NULL;
    size_t host_size = 0;
    int port = 0;
    char* resource = NULL;
    size_t resource_size = 0;

    jclass = find_class(env, ENDPOINT_CLASS);
    if (!jclass) {
        goto out;
    }

    if (roc_endpoint_allocate(&endpoint) != 0) {
        throw_exception(env, ASSERTION_ERROR, "Failed to allocate endpoint");
        goto out;
    }

    // parse uri
    if (!juri) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri: must not be null");
        goto out;
    }
    uri = (*env)->GetStringUTFChars(env, juri, 0);
    if (!uri) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri: must not be null");
        goto out;
    }
    if (roc_endpoint_set_uri(endpoint, uri) != 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri");
        goto out;
    }

    // copy protocol
    if (roc_endpoint_get_protocol(endpoint, &protocol) != 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri: bad protocol");
        goto out;
    }
    if (!endpoint_set_protocol(env, jobj, protocol)) {
        throw_exception(env, ASSERTION_ERROR, "Failed to update endpoint");
        goto out;
    }

    // copy host
    if (roc_endpoint_get_host(endpoint, NULL, &host_size) != 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri: bad host");
        goto out;
    }
    host = (char*) calloc(host_size + 1, sizeof(char));
    if (roc_endpoint_get_host(endpoint, host, &host_size) != 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri: bad host");
        goto out;
    }
    if (!endpoint_set_host(env, jobj, host)) {
        throw_exception(env, ASSERTION_ERROR, "Failed to update endpoint");
        goto out;
    }

    // copy port
    if (roc_endpoint_get_port(endpoint, &port) != 0) {
        // port is optional
        port = -1;
    }
    if (!endpoint_set_port(env, jobj, port)) {
        throw_exception(env, ASSERTION_ERROR, "Failed to update endpoint");
        goto out;
    }

    // copy resource
    if (roc_endpoint_get_resource(endpoint, NULL, &resource_size) != 0) {
        if (!endpoint_set_resource(env, jobj, NULL)) {
            throw_exception(env, ASSERTION_ERROR, "Failed to update endpoint");
            goto out;
        }
    } else {
        resource = (char*) calloc(resource_size + 1, sizeof(char));
        if (roc_endpoint_get_resource(endpoint, resource, &resource_size) != 0) {
            throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri: bad resource");
            goto out;
        }
        if (!endpoint_set_resource(env, jobj, resource)) {
            throw_exception(env, ASSERTION_ERROR, "Failed to update endpoint");
            goto out;
        }
    }

out:
    if (uri) (*env)->ReleaseStringUTFChars(env, juri, uri);

    free(host);
    free(resource);

    if (endpoint) {
        roc_endpoint_deallocate(endpoint);
    }
}

JNIEXPORT jstring JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_nativeFormatUri(
    JNIEnv* env, jobject jendpoint) {
    assert(env);

    roc_endpoint* endpoint = NULL;
    jstring juri = NULL;
    char* uri = NULL;
    size_t uri_size = 0;

    if (!jendpoint) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint: must not be null");
        goto out;
    }

    if (!endpoint_unmarshal(env, jendpoint, &endpoint)) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint");
        goto out;
    }

    if (roc_endpoint_get_uri(endpoint, NULL, &uri_size) != 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri");
        goto out;
    }
    uri = (char*) calloc(uri_size + 1, sizeof(char));
    if (roc_endpoint_get_uri(endpoint, uri, &uri_size) != 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri");
        goto out;
    }

    juri = (*env)->NewStringUTF(env, uri);
    assert(juri);

out:
    free(uri);

    if (endpoint) {
        roc_endpoint_deallocate(endpoint);
    }

    return juri;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Endpoint_nativeValidate(
    JNIEnv* env, jobject jendpoint) {
    assert(env);

    roc_endpoint* endpoint = NULL;
    char* uri = NULL;
    size_t uri_size = 0;

    if (!jendpoint) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint: must not be null");
        goto out;
    }

    if (!endpoint_unmarshal(env, jendpoint, &endpoint)) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint");
        goto out;
    }

    if (roc_endpoint_get_uri(endpoint, NULL, &uri_size) != 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri");
        goto out;
    }
    uri = (char*) calloc(uri_size + 1, sizeof(char));
    if (roc_endpoint_get_uri(endpoint, uri, &uri_size) != 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid endpoint uri");
        goto out;
    }

out:
    free(uri);

    if (endpoint) {
        roc_endpoint_deallocate(endpoint);
    }
}
