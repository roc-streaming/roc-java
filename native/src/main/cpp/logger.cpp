#include "com_rocproject_roc_log_Logger.h"
#include "common.h"

#include <cassert>
#include <mutex>

#include <roc/log.h>

#define LOG_LEVEL_CLASS             "com/rocproject/roc/log/LogLevel"
#define LOG_HANDLER_CLASS           "com/rocproject/roc/log/LogHandler"

static struct {
    JavaVM*     vm;
    jobject     callback;
    jmethodID   methID;
    std::mutex  mutex;
} handler_args = {0};

static const char* logLevelMapping(roc_log_level level) {
    const char* ret = "ROC_LOG_ERROR";
    switch (level) {
        case ROC_LOG_NONE:
            ret = "ROC_LOG_NONE";
            break;
        case ROC_LOG_ERROR:
            ret = "ROC_LOG_ERROR";
            break;
        case ROC_LOG_INFO:
            ret = "ROC_LOG_INFO";
            break;
        case ROC_LOG_DEBUG:
            ret = "ROC_LOG_DEBUG";
            break;
        case ROC_LOG_TRACE:
            ret = "ROC_LOG_TRACE";
            break;
        default: break;
    }
    return ret;
};

void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv* env;

    vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION);

    handler_args.mutex.lock();
    if (handler_args.callback != NULL) {
        env->DeleteGlobalRef(handler_args.callback);
    }
    handler_args.mutex.unlock();
}

void logger_handler(roc_log_level level, const char* component, const char* message) {
    JNIEnv*     env;
    jclass      logLevelClass;
    jfieldID    field;
    jobject     levelObj;
    jstring     jcomp;
    jstring     jmess;

    handler_args.mutex.lock();
    if(handler_args.vm == NULL || handler_args.callback == NULL || handler_args.methID == NULL) {
        handler_args.mutex.unlock();
        return;
    }

    handler_args.vm->AttachCurrentThread((void**) &env, 0);

    logLevelClass = env->FindClass(LOG_LEVEL_CLASS);

    field = env->GetStaticFieldID(logLevelClass, logLevelMapping(level), "L" LOG_LEVEL_CLASS ";");
    levelObj = env->GetStaticObjectField(logLevelClass, field);

    jcomp = env->NewStringUTF(component);
    jmess = env->NewStringUTF(message);

    env->CallVoidMethod(handler_args.callback, handler_args.methID, levelObj, jcomp, jmess);
    handler_args.mutex.unlock();

    handler_args.vm->DetachCurrentThread();
}

/*
 * Class:     com_rocproject_roc_log_Logger
 * Method:    setLevel
 * Signature: (Lcom/rocproject/roc/log/LogLevel;)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_log_Logger_setLevel(JNIEnv *env, jclass clazz, jobject jlevel) {
    jclass          logLevelClass;
    roc_log_level   level;

    if (jlevel == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "no logger level provided");
        return;
    }

    logLevelClass = env->FindClass(LOG_LEVEL_CLASS);
    assert(logLevelClass);

    level = (roc_log_level) get_enum_value(env, logLevelClass, jlevel);
    roc_log_set_level(level);
}

/*
 * Class:     com_rocproject_roc_log_Logger
 * Method:    setCallback
 * Signature: (Lcom/rocproject/roc/log/LogHandler;)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_log_Logger_setCallback(JNIEnv *env, jclass clazz, jobject jhandler) {
    jclass      logHandlerClass;
    jmethodID   tmpMethodID;

    if (jhandler == NULL) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "no logger callback provided");
        return;
    }

    logHandlerClass = env->GetObjectClass(jhandler);
    tmpMethodID = (jmethodID) env->GetMethodID(logHandlerClass, "log", "(L" LOG_LEVEL_CLASS ";Ljava/lang/String;Ljava/lang/String;)V");
    if (tmpMethodID == NULL)
        return;

    handler_args.mutex.lock();
    env->GetJavaVM(&handler_args.vm);
    if (handler_args.callback != NULL) {
        env->DeleteGlobalRef(handler_args.callback);
    }

    handler_args.callback = (jobject) env->NewGlobalRef(jhandler);
    handler_args.methID = tmpMethodID;
    handler_args.mutex.unlock();

    roc_log_set_handler(logger_handler);
}