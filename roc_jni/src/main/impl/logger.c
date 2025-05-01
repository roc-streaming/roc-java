#include "org_rocstreaming_roctoolkit_RocLogger.h"

#include "exceptions.h"
#include "helpers.h"
#include "package.h"

#include <roc/log.h>

#include <assert.h>
#include <pthread.h>
#include <string.h>

#define MAX_LEVELS 16

static pthread_mutex_t logger_mutex = PTHREAD_MUTEX_INITIALIZER;

static struct {
    JavaVM* vm;
    jclass level_class;
    jobject level_objects[MAX_LEVELS];
    jclass handler_class;
    jobject handler_object;
    jmethodID handler_method;
} logger_state;

static const char* map_log_level(roc_log_level level) {
    switch (level) {
    case ROC_LOG_NONE:
        return "NONE";
    case ROC_LOG_ERROR:
        return "ERROR";
    case ROC_LOG_INFO:
        return "INFO";
    case ROC_LOG_DEBUG:
        return "DEBUG";
    case ROC_LOG_TRACE:
        return "TRACE";
    default:
        break;
    }
    return NULL;
};

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    assert(vm);

    pthread_mutex_lock(&logger_mutex);

    JNIEnv* env = NULL;
    jint result = JNI_VERSION;
    jclass level_class;

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION) != JNI_OK) {
        result = JNI_ERR;
        goto out;
    }

    level_class = (*env)->FindClass(env, LOG_LEVEL_CLASS);
    if (!level_class) {
        goto out;
    }

    logger_state.level_class = (jclass) (*env)->NewGlobalRef(env, level_class);

    for (int i = 0; i < MAX_LEVELS; i++) {
        const char* level_field_name = map_log_level((roc_log_level) i);
        if (!level_field_name) {
            continue;
        }
        jfieldID level_field_id
            = (*env)->GetStaticFieldID(env, level_class, level_field_name, "L" LOG_LEVEL_CLASS ";");
        if (!level_field_id) {
            continue;
        }
        jobject level_object = (*env)->GetStaticObjectField(env, level_class, level_field_id);
        if (!level_object) {
            continue;
        }
        logger_state.level_objects[i] = (*env)->NewGlobalRef(env, level_object);
    }

out:
    pthread_mutex_unlock(&logger_mutex);

    return result;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved) {
    assert(vm);

    JNIEnv* env = NULL;

    pthread_mutex_lock(&logger_mutex);

    (*logger_state.vm)->GetEnv(vm, (void**) &env, JNI_VERSION);

    if (logger_state.level_class) {
        (*env)->DeleteGlobalRef(env, logger_state.level_class);
    }
    for (int i = 0; i < MAX_LEVELS; i++) {
        if (logger_state.level_objects[i]) {
            (*env)->DeleteGlobalRef(env, logger_state.level_objects[i]);
        }
    }

    if (logger_state.handler_class) {
        (*env)->DeleteGlobalRef(env, logger_state.handler_class);
    }
    if (logger_state.handler_object) {
        (*env)->DeleteGlobalRef(env, logger_state.handler_object);
    }

    memset(&logger_state, 0, sizeof(logger_state));

    pthread_mutex_unlock(&logger_mutex);
}

static void logger_handler(const roc_log_message* message, void* argument) {
    assert(message);

    JNIEnv* env = NULL;
    jint result = 0;
    int detach = 0;

    jobject msg_level = NULL;
    jstring msg_module = NULL;
    jstring msg_text = NULL;

    pthread_mutex_lock(&logger_mutex);

    if (logger_state.vm == NULL || logger_state.level_class == NULL
        || logger_state.handler_class == NULL || logger_state.handler_object == NULL
        || logger_state.handler_method == NULL) {
        goto out;
    }

    // check if it is needed to attach current thread
    if ((result = (*logger_state.vm)->GetEnv(logger_state.vm, (void**) &env, JNI_VERSION))
        == JNI_EDETACHED) {
#ifdef __ANDROID__
        if ((*logger_state.vm)->AttachCurrentThread(logger_state.vm, &env, 0) == JNI_OK)
#else
        if ((*logger_state.vm)->AttachCurrentThread(logger_state.vm, (void**) &env, 0) == JNI_OK)
#endif
            detach = 1;
        else {
            // cannot attach current thread
            goto out;
        }
    } else if (result != JNI_OK) {
        // cannot get env
        goto out;
    }

    assert(env);

    if (message->level < 0 || message->level >= MAX_LEVELS) goto out;
    msg_level = logger_state.level_objects[message->level];
    if (!msg_level) goto out;

    msg_module = (*env)->NewStringUTF(env, message->module);
    if (!msg_module) goto out;

    msg_text = (*env)->NewStringUTF(env, message->text);
    if (!msg_text) goto out;

    (*env)->CallVoidMethod(env, logger_state.handler_object, logger_state.handler_method, msg_level,
        msg_module, msg_text);

out:
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }

    if (detach) {
        (*logger_state.vm)->DetachCurrentThread(logger_state.vm);
    }

    pthread_mutex_unlock(&logger_mutex);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocLogger_nativeSetLevel(
    JNIEnv* env, jclass jlogger_class, jobject jlevel) {
    assert(env);

    jfieldID value_field = NULL;
    roc_log_level level = (roc_log_level) 0;
    bool success = false;

    pthread_mutex_lock(&logger_mutex);

    if (!jlevel) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid RocLogLevel: must not be null");
        goto out;
    }

    value_field = (*env)->GetFieldID(env, logger_state.level_class, "value", "I");
    if (!value_field) {
        goto out;
    }

    level = (roc_log_level) (*env)->GetIntField(env, jlevel, value_field);
    success = true;

out:
    pthread_mutex_unlock(&logger_mutex);

    if (success) {
        roc_log_set_level(level);
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocLogger_nativeSetHandler(
    JNIEnv* env, jclass jlogger_class, jobject jhandler) {
    assert(env);

    jclass handler_class = NULL;
    jmethodID handler_method = NULL;
    bool success = false;

    pthread_mutex_lock(&logger_mutex);

    if (!jhandler) {
        success = true;
        goto out;
    }

    handler_class = (*env)->GetObjectClass(env, jhandler);
    if (!handler_class) {
        goto out;
    }

    handler_method = (jmethodID) (*env)->GetMethodID(
        env, handler_class, "log", "(L" LOG_LEVEL_CLASS ";Ljava/lang/String;Ljava/lang/String;)V");
    if (!handler_method) {
        goto out;
    }

    if (logger_state.vm == NULL) {
        (*env)->GetJavaVM(env, &logger_state.vm);
    }

    if (logger_state.handler_class != NULL) {
        (*env)->DeleteGlobalRef(env, logger_state.handler_class);
    }
    if (logger_state.handler_object != NULL) {
        (*env)->DeleteGlobalRef(env, logger_state.handler_object);
    }

    logger_state.handler_class = (jclass) (*env)->NewGlobalRef(env, handler_class);
    logger_state.handler_object = (*env)->NewGlobalRef(env, jhandler);
    logger_state.handler_method = handler_method;

    success = true;

out:
    pthread_mutex_unlock(&logger_mutex);

    if (success) {
        if (jhandler) {
            roc_log_set_handler(logger_handler, NULL);
        } else {
            roc_log_set_handler(NULL, NULL);
        }
    }
}
