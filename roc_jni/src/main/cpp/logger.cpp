#include "com_github_rocproject_roc_Logger.h"
#include "common.h"

#include <cassert>
#include <mutex>

#include <roc/log.h>

#define LOG_LEVEL_CLASS             PACKAGE_BASE_NAME "/LogLevel"
#define LOG_HANDLER_CLASS           PACKAGE_BASE_NAME "/LogHandler"

static struct {
    JavaVM*     vm;
    jobject     callback;
    jmethodID   methID;
    std::mutex  mutex;
} handler_args = {0};

static const char* logLevelMapping(roc_log_level level) {
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
        default: break;
    }
    return ret;
};

void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv* env;

    vm->GetEnv((void**) &env, JNI_VERSION);

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

#ifdef __ANDROID__
    handler_args.vm->AttachCurrentThread(&env, 0);
#else
    handler_args.vm->AttachCurrentThread((void**) &env, 0);
#endif

    logLevelClass = env->FindClass(LOG_LEVEL_CLASS);

    field = env->GetStaticFieldID(logLevelClass, logLevelMapping(level), "L" LOG_LEVEL_CLASS ";");
    levelObj = env->GetStaticObjectField(logLevelClass, field);

    jcomp = env->NewStringUTF(component);
    jmess = env->NewStringUTF(message);

    env->CallVoidMethod(handler_args.callback, handler_args.methID, levelObj, jcomp, jmess);

    handler_args.vm->DetachCurrentThread();

    handler_args.mutex.unlock();
}

JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Logger_setLevel(JNIEnv *env, jclass clazz, jobject jlevel) {
    jclass          logLevelClass;
    roc_log_level   level;

    if (jlevel == NULL) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "no logger level provided");
        return;
    }

    logLevelClass = env->FindClass(LOG_LEVEL_CLASS);
    assert(logLevelClass);

    level = (roc_log_level) get_enum_value(env, logLevelClass, jlevel);
    roc_log_set_level(level);
}

JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Logger_setCallback(JNIEnv *env, jclass clazz, jobject jhandler) {
    jclass      logHandlerClass;
    jmethodID   tmpMethodID;

    if (jhandler == NULL) { // reset default callback (write to stderr)
        roc_log_set_handler(NULL);
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