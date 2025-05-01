#include "org_rocstreaming_roctoolkit_RocReceiver.h"

#include "endpoint.h"
#include "exceptions.h"
#include "helpers.h"
#include "interface_config.h"
#include "receiver_config.h"

#include <roc/receiver.h>

#include <assert.h>
#include <string.h>

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeOpen(
    JNIEnv* env, jclass jclass, jlong jcontext, jobject jconfig) {
    assert(env);

    roc_context* context = (roc_context*) jcontext;
    roc_receiver_config receiver_config = {};
    roc_receiver* receiver = NULL;

    if (!jcontext) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocContext: must not be null");
        goto out;
    }

    if (!jconfig) {
        throw_exception(
            env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocReceiverConfig: must not be null");
        goto out;
    }

    if (!receiver_config_unmarshal(env, jconfig, &receiver_config)) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocReceiverConfig");
        goto out;
    }

    if (roc_receiver_open(context, &receiver_config, &receiver) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to open RocReceiver");
        goto out;
    }

    if (!receiver) {
        throw_exception(env, ASSERTION_ERROR, "RocReceiver is null");
        goto out;
    }

out:
    return (jlong) receiver;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeClose(
    JNIEnv* env, jclass jclass, jlong jreceiver) {
    assert(env);

    roc_receiver* receiver = (roc_receiver*) jreceiver;

    if (!jreceiver) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocReceiver: must not be null");
        goto out;
    }

    if (roc_receiver_close(receiver) != 0) {
        throw_exception(env, ASSERTION_ERROR, "Failed to close RocReceiver");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeConfigure(
    JNIEnv* env, jobject jobj, jlong jreceiver, jint jslot, jint jinterface, jobject jconfig) {
    assert(env);

    roc_receiver* receiver = (roc_receiver*) jreceiver;
    roc_interface_config interface_config = {};

    if (!jreceiver) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocReceiver: must not be null");
        goto out;
    }

    if (!jconfig) {
        throw_exception(
            env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid InterfaceConfig: must not be null");
        goto out;
    }

    if (!interface_config_unmarshal(env, jconfig, &interface_config)) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid InterfaceConfig");
        goto out;
    }

    if (roc_receiver_configure(
            receiver, (roc_slot) jslot, (roc_interface) jinterface, &interface_config)
        != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to configure RocReceiver interface");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeBind(
    JNIEnv* env, jobject jobj, jlong jreceiver, jint jslot, jint jinterface, jobject jendpoint) {
    assert(env);

    roc_receiver* receiver = (roc_receiver*) jreceiver;
    roc_endpoint* endpoint = NULL;
    int port = 0;

    if (!jreceiver) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocReceiver: must not be null");
        goto out;
    }

    if (!jendpoint) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid Endpoint: must not be null");
        goto out;
    }

    if (!endpoint_unmarshal(env, jendpoint, &endpoint)) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid Endpoint");
        goto out;
    }

    if (roc_receiver_bind(receiver, (roc_slot) jslot, (roc_interface) jinterface, endpoint) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to bind RocReceiver endpoint");
        goto out;
    }

    if (roc_endpoint_get_port(endpoint, &port) != 0) {
        throw_exception(env, ASSERTION_ERROR, "Failed to read RocReceiver endpoint");
        goto out;
    }

    if (!endpoint_set_port(env, jendpoint, port)) {
        throw_exception(env, ASSERTION_ERROR, "Failed to write RocReceiver endpoint");
        goto out;
    }

out:
    if (endpoint) {
        roc_endpoint_deallocate(endpoint);
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeUnlink(
    JNIEnv* env, jobject jobj, jlong jreceiver, jint jslot) {
    assert(env);

    roc_receiver* receiver = (roc_receiver*) jreceiver;

    if (!jreceiver) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocReceiver: must not be null");
        goto out;
    }

    if (roc_receiver_unlink(receiver, (roc_slot) jslot) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to unlink RocReceiver slot");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeReadFloats(
    JNIEnv* env, jobject jobj, jlong jreceiver, jfloatArray jsamples) {
    assert(env);

    roc_receiver* receiver = (roc_receiver*) jreceiver;
    jfloat* samples = NULL;
    jsize samples_count = 0;
    roc_frame frame = {};

    if (!jreceiver) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocReceiver: must not be null");
        goto out;
    }

    if (!jsamples) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid samples array: must not be null");
        goto out;
    }

    samples = (*env)->GetFloatArrayElements(env, jsamples, 0);
    samples_count = (*env)->GetArrayLength(env, jsamples);

    memset(&frame, 0, sizeof(frame));
    frame.samples = samples;
    frame.samples_size = samples_count * sizeof(float);

    if (roc_receiver_read(receiver, &frame) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to read frame from RocReceiver");
        goto out;
    }

out:
    if (samples) {
        (*env)->ReleaseFloatArrayElements(env, jsamples, samples, 0);
    }
}
