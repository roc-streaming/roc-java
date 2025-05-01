#include "org_rocstreaming_roctoolkit_RocContext.h"

#include "context_config.h"
#include "exceptions.h"
#include "helpers.h"
#include "media_encoding.h"
#include "package.h"

#include <roc/context.h>

#include <assert.h>

JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_RocContext_nativeOpen(
    JNIEnv* env, jclass jclass, jobject jconfig) {
    assert(env);

    roc_context_config context_config = {};
    roc_context* context = NULL;

    if (!jconfig) {
        throw_exception(
            env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocContextConfig: must not be null");
        goto out;
    }

    if (!context_config_unmarshal(env, jconfig, &context_config)) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocContextConfig");
        goto out;
    }

    if (roc_context_open(&context_config, &context) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to open RocContext");
        goto out;
    }

    if (!context) {
        throw_exception(env, ASSERTION_ERROR, "RocContext is null");
        goto out;
    }

out:
    return (jlong) context;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocContext_nativeClose(
    JNIEnv* env, jclass jclass, jlong jcontext) {
    assert(env);

    roc_context* context = (roc_context*) jcontext;

    if (!jcontext) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocContext: must not be null");
        goto out;
    }

    if (roc_context_close(context) != 0) {
        throw_exception(env, ILLEGAL_STATE_EXCEPTION,
            "Can't close RocContext before closing associated RocSender/RocReceiver(s)");
        goto out;
    }

out:
    return;
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocContext_nativeRegisterEncoding(
    JNIEnv* env, jclass jclass, jlong jcontext, jint jencoding_id, jobject jencoding) {
    assert(env);

    roc_context* context = (roc_context*) jcontext;
    roc_media_encoding encoding = {};

    if (!jcontext) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocContext: must not be null");
        goto out;
    }

    if (!jencoding) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid MediaEncoding: must not be null");
        goto out;
    }

    if (!media_encoding_unmarshal(env, jencoding, &encoding)) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid MediaEncoding");
        goto out;
    }

    if (roc_context_register_encoding(context, (int) jencoding_id, &encoding) != 0) {
        throw_exception(env, ROC_EXCEPTION, "Failed to register MediaEncoding");
        goto out;
    }

out:
    return;
}
