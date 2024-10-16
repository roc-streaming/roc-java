#include "org_rocstreaming_roctoolkit_RocSender.h"

#include "common.h"
#include "endpoint.h"
#include "interface_config.h"
#include "sender_config.h"

#include <roc/frame.h>
#include <roc/sender.h>

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeOpen(
    JNIEnv* env, jclass senderClass, jlong contextPtr, jobject jconfig) {
    roc_context* context = NULL;
    roc_sender_config config = {};
    roc_sender* sender = NULL;

    context = (roc_context*) contextPtr;

    if (sender_config_unmarshal(env, &config, jconfig) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad config argument");
        goto out;
    }

    if ((roc_sender_open(context, &config, &sender)) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error opening sender");
        sender = NULL;
        goto out;
    }

out:
    return (jlong) sender;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeClose(
    JNIEnv* env, jclass senderClass, jlong senderPtr) {

    roc_sender* sender = (roc_sender*) senderPtr;

    if (roc_sender_close(sender) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error closing sender");
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeConfigure(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jint slot, jint interface, jobject jconfig) {
    roc_sender* sender = NULL;
    roc_interface_config config = {};

    sender = (roc_sender*) senderPtr;

    if (jconfig == NULL) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad config argument");
        goto out;
    }

    if (interface_config_unmarshal(env, &config, jconfig) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error unmarshalling config");
        goto out;
    }

    if (roc_sender_configure(sender, (roc_slot) slot, (roc_interface) interface, &config) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error configuring sender");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeConnect(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jint slot, jint interface, jobject jendpoint) {
    roc_sender* sender = NULL;
    roc_endpoint* endpoint = NULL;

    sender = (roc_sender*) senderPtr;

    if (jendpoint == NULL) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad endpoint argument");
        goto out;
    }

    if (endpoint_unmarshal(env, &endpoint, jendpoint) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error unmarshalling endpoint");
        goto out;
    }

    if (roc_sender_connect(sender, (roc_slot) slot, (roc_interface) interface, endpoint) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error connecting sender");
        goto out;
    }

out:
    if (endpoint != NULL) {
        roc_endpoint_deallocate(endpoint);
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeUnlink(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jint slot) {
    roc_sender* sender = NULL;

    sender = (roc_sender*) senderPtr;

    if (roc_sender_unlink(sender, (roc_slot) slot) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error unlinking slot");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_nativeWriteFloats(
    JNIEnv* env, jobject thisObj, jlong senderPtr, jfloatArray jsamples) {
    roc_sender* sender = NULL;
    jfloat* samples = NULL;
    jsize len = 0;
    roc_frame frame = {};

    sender = (roc_sender*) senderPtr;

    if (jsamples == NULL) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad samples argument");
        goto out;
    }
    samples = (*env)->GetFloatArrayElements(env, jsamples, 0);
    len = (*env)->GetArrayLength(env, jsamples);
    assert(samples != NULL);

    memset(&frame, 0, sizeof(frame));
    frame.samples = samples;
    frame.samples_size = len * sizeof(float);

    if (roc_sender_write(sender, &frame) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error writing frame");
        goto out;
    }

out:
    if (samples != NULL) (*env)->ReleaseFloatArrayElements(env, jsamples, samples, 0);
}
