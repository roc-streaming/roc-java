#include "org_rocstreaming_roctoolkit_RocReceiver.h"

#include "common.h"
#include "endpoint.h"
#include "interface_config.h"
#include "receiver_config.h"

#include <roc/receiver.h>

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeOpen(
    JNIEnv* env, jclass receiverClass, jlong contextPtr, jobject jconfig) {
    roc_context* context = NULL;
    roc_receiver_config receiverConfig = {};
    roc_receiver* receiver = NULL;

    context = (roc_context*) contextPtr;

    if (receiver_config_unmarshal(env, &receiverConfig, jconfig) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad config argument");
        goto out;
    }

    if ((roc_receiver_open(context, &receiverConfig, &receiver)) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error opening receiver");
        receiver = NULL;
        goto out;
    }

out:
    return (jlong) receiver;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeClose(
    JNIEnv* env, jclass receiverClass, jlong receiverPtr) {

    roc_receiver* receiver = (roc_receiver*) receiverPtr;

    if (roc_receiver_close(receiver) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error closing receiver");
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeConfigure(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jint slot, jint interface, jobject jconfig) {
    roc_receiver* receiver = NULL;
    roc_interface_config config = {};

    receiver = (roc_receiver*) receiverPtr;

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

    if (roc_receiver_configure(receiver, (roc_slot) slot, (roc_interface) interface, &config)
        != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error configuring receiver");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeBind(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jint slot, jint interface, jobject jendpoint) {
    roc_receiver* receiver = NULL;
    roc_endpoint* endpoint = NULL;
    int port = 0;

    receiver = (roc_receiver*) receiverPtr;

    if (endpoint_unmarshal(env, &endpoint, jendpoint) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Bad endpoint argument");
        goto out;
    }

    if (roc_receiver_bind(receiver, (roc_slot) slot, (roc_interface) interface, endpoint) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error binding receiver");
        goto out;
    }

    if (roc_endpoint_get_port(endpoint, &port) == 0) {
        endpoint_set_port(env, jendpoint, port);
    } else {
        endpoint_set_port(env, jendpoint, -1);
    }

out:
    if (endpoint != NULL) {
        roc_endpoint_deallocate(endpoint);
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeUnlink(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jint slot) {
    roc_receiver* receiver = NULL;

    receiver = (roc_receiver*) receiverPtr;

    if (roc_receiver_unlink(receiver, (roc_slot) slot) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error unlinking slot");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocReceiver_nativeReadFloats(
    JNIEnv* env, jobject thisObj, jlong receiverPtr, jfloatArray jsamples) {
    roc_receiver* receiver = NULL;
    jfloat* samples = NULL;
    jsize len = 0;
    roc_frame frame = {};

    receiver = (roc_receiver*) receiverPtr;

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

    if (roc_receiver_read(receiver, &frame) != 0) {
        jclass exceptionClass = (*env)->FindClass(env, IO_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "Error reading frame");
        goto out;
    }

out:
    if (samples != NULL) (*env)->ReleaseFloatArrayElements(env, jsamples, samples, 0);
}
