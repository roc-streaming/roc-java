#include "org_rocstreaming_roctoolkit_RocSender.h"

#include "endpoint.h"
#include "exceptions.h"
#include "helpers.h"
#include "interface_config.h"
#include "sender_config.h"

#include <roc/sender.h>

#include <assert.h>
#include <string.h>

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeOpen(
    JNIEnv* env, jclass jclass, jlong jcontext, jobject jconfig) {
    assert(env);

    roc_context* context = (roc_context*) jcontext;
    roc_sender_config sender_config = {};
    roc_sender* sender = NULL;

    if (!jcontext) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocContext: must not be null");
        goto out;
    }

    if (!jconfig) {
        throw_exception(
            env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocSenderConfig: must not be null");
        goto out;
    }

    if (!sender_config_unmarshal(env, jconfig, &sender_config)) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocSenderConfig");
        goto out;
    }

    if (roc_sender_open(context, &sender_config, &sender) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to open RocSender");
        goto out;
    }

    if (!sender) {
        throw_exception(env, ASSERTION_ERROR, "RocSender is null");
        goto out;
    }

out:
    return (jlong) sender;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeClose(
    JNIEnv* env, jclass jclass, jlong jsender) {
    assert(env);

    roc_sender* sender = (roc_sender*) jsender;

    if (!jsender) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocSender: must not be null");
        goto out;
    }

    if (roc_sender_close(sender) != 0) {
        throw_exception(env, ASSERTION_ERROR, "Failed to close RocSender");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeConfigure(
    JNIEnv* env, jobject jobj, jlong jsender, jint jslot, jint jinterface, jobject jconfig) {
    assert(env);

    roc_sender* sender = (roc_sender*) jsender;
    roc_interface_config interface_config = {};

    if (!jsender) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocSender: must not be null");
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

    if (roc_sender_configure(
            sender, (roc_slot) jslot, (roc_interface) jinterface, &interface_config)
        != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to configure RocSender interface");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeConnect(
    JNIEnv* env, jobject jobj, jlong jsender, jint jslot, jint jinterface, jobject jendpoint) {
    assert(env);

    roc_sender* sender = (roc_sender*) jsender;
    roc_endpoint* endpoint = NULL;

    if (!jsender) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocSender: must not be null");
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

    if (roc_sender_connect(sender, (roc_slot) jslot, (roc_interface) jinterface, endpoint) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to connect RocSender endpoint");
        goto out;
    }

out:
    if (endpoint) {
        roc_endpoint_deallocate(endpoint);
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeUnlink(
    JNIEnv* env, jobject jobj, jlong jsender, jint jslot) {
    assert(env);

    roc_sender* sender = (roc_sender*) jsender;

    if (!jsender) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocSender: must not be null");
        goto out;
    }

    if (roc_sender_unlink(sender, (roc_slot) jslot) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to unlink RocSender slot");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeWriteFloats(
    JNIEnv* env, jobject jobj, jlong jsender, jfloatArray jsamples) {
    assert(env);

    roc_sender* sender = (roc_sender*) jsender;
    jfloat* samples = NULL;
    jsize samples_count = 0;
    roc_frame frame = {};

    if (!jsender) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocSender: must not be null");
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

    if (roc_sender_write(sender, &frame) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to write frame to RocSender");
        goto out;
    }

out:
    if (samples) {
        (*env)->ReleaseFloatArrayElements(env, jsamples, samples, 0);
    }
}
