#include "org_rocstreaming_roctoolkit_Logger.h"

#include "common.h"

#include <roc/log.h>

#include <pthread.h>

#define LOG_LEVEL_CLASS PACKAGE_BASE_NAME "/LogLevel"

static pthread_mutex_t handler_mutex = PTHREAD_MUTEX_INITIALIZER;

static struct {
    JavaVM* vm;
    jobject object;
    jmethodID method;
} handler = { 0 };

static const char* msgLevelMapping(roc_log_level level) {
    const char* ret = "ERROR";
    switch (level) {
    case ROC_LOG_NONE:
        ret = "NONE";
        break;
    case ROC_LOG_ERROR:
        ret = "ERROR";
        break;
    case ROC_LOG_INFO:
        ret = "INFO";
        break;
    case ROC_LOG_DEBUG:
        ret = "DEBUG";
        break;
    case ROC_LOG_TRACE:
        ret = "TRACE";
        break;
    default:
        break;
    }
    return ret;
};

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;

    if (vm->GetEnv((void**) &env, JNI_VERSION) != JNI_OK) {
        return JNI_ERR;
    }

    return JNI_VERSION;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved) {
    pthread_mutex_lock(&handler_mutex);

    handler.vm = NULL;
    handler.object = NULL;
    handler.method = NULL;

    pthread_mutex_unlock(&handler_mutex);
}

static void logger_handler(const roc_log_message* message, void* argument) {
    JNIEnv* env = NULL;
    jclass levelClass = NULL;
    jfieldID levelFieldID = NULL;
    jobject msgLevel = NULL;
    jstring msgModule = NULL;
    jstring msgText = NULL;
    jint result = 0;
    int detach = 0;

    pthread_mutex_lock(&handler_mutex);

    if (handler.vm == NULL || handler.object == NULL || handler.method == NULL) {
        goto out;
    }

    // check if it is needed to attach current thread
    if ((result = handler.vm->GetEnv((void**) &env, JNI_VERSION)) == JNI_EDETACHED) {
#ifdef __ANDROID__
        if (handler.vm->AttachCurrentThread(&env, 0) == JNI_OK)
#else
        if (handler.vm->AttachCurrentThread((void**) &env, 0) == JNI_OK)
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

    assert(env != NULL);

    levelClass = env->FindClass(LOG_LEVEL_CLASS);
    assert(levelClass != NULL);

    levelFieldID = env->GetStaticFieldID(
        levelClass, msgLevelMapping(message->level), "L" LOG_LEVEL_CLASS ";");
    assert(levelFieldID != NULL);

    msgLevel = env->GetStaticObjectField(levelClass, levelFieldID);
    assert(msgLevel != NULL);

    msgModule = env->NewStringUTF(message->module);
    assert(msgModule != NULL);

    msgText = env->NewStringUTF(message->text);
    assert(msgText != NULL);

    env->CallVoidMethod(handler.object, handler.method, msgLevel, msgModule, msgText);

    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

out:
    if (detach) {
        handler.vm->DetachCurrentThread();
    }

    pthread_mutex_unlock(&handler_mutex);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Logger_setLevel(
    JNIEnv* env, jclass clazz, jobject jlevel) {
    jclass levelClass = NULL;
    roc_log_level level = (roc_log_level) 0;

    if (jlevel == NULL) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "no logger level provided");
        return;
    }

    levelClass = env->FindClass(LOG_LEVEL_CLASS);
    assert(levelClass != NULL);

    level = (roc_log_level) get_enum_value(env, levelClass, jlevel);

    roc_log_set_level(level);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Logger_setCallback(
    JNIEnv* env, jclass clazz, jobject jhandler) {
    jclass handlerClass = NULL;
    jmethodID handlerMethod = NULL;

    if (jhandler == NULL) {
        roc_log_set_handler(NULL, NULL);
        return;
    }

    handlerClass = env->GetObjectClass(jhandler);
    assert(handlerClass != NULL);

    handlerMethod = (jmethodID) env->GetMethodID(
        handlerClass, "log", "(L" LOG_LEVEL_CLASS ";Ljava/lang/String;Ljava/lang/String;)V");
    assert(handlerMethod != NULL);

    pthread_mutex_lock(&handler_mutex);

    if (handler.vm == NULL) {
        env->GetJavaVM(&handler.vm);
    }
    if (handler.object != NULL) {
        env->DeleteGlobalRef(handler.object);
    }
    handler.object = (jobject) env->NewGlobalRef(jhandler);
    handler.method = handlerMethod;

    pthread_mutex_unlock(&handler_mutex);

    roc_log_set_handler(logger_handler, NULL);
}
