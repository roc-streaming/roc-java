#include "org_rocstreaming_roctoolkit_Logger.h"

#include "common.h"

#include <roc/log.h>

#include <mutex>

#define LOG_LEVEL_CLASS PACKAGE_BASE_NAME "/LogLevel"
#define LOG_HANDLER_CLASS PACKAGE_BASE_NAME "/LogHandler"

static struct {
    JavaVM* vm;
    jobject callback;
    jclass logLevelClass;
    jmethodID methID;
    std::mutex mutex;
} handler_args = { 0 };

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
    default:
        break;
    }
    return ret;
};

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jclass tempLocalClassRef = NULL;

    if (vm->GetEnv((void**) &env, JNI_VERSION) != JNI_OK) {
        return JNI_ERR;
    }

    tempLocalClassRef = env->FindClass(LOG_LEVEL_CLASS);
    handler_args.logLevelClass
        = (jclass) env->NewGlobalRef(tempLocalClassRef); // cache LogLevel class
    env->DeleteLocalRef(tempLocalClassRef);
    return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;

    vm->GetEnv((void**) &env, JNI_VERSION);

    handler_args.mutex.lock();
    env->DeleteGlobalRef(handler_args.logLevelClass);
    if (handler_args.callback != NULL) {
        env->DeleteGlobalRef(handler_args.callback);
    }
    handler_args.mutex.unlock();
}

void logger_handler(const roc_log_message* message, void* argument) {
    JNIEnv* env = NULL;
    jfieldID field = NULL;
    jobject levelObj = NULL;
    jstring jmodule = NULL;
    jstring jmess = NULL;

    handler_args.mutex.lock();
    if (handler_args.vm == NULL || handler_args.callback == NULL || handler_args.methID == NULL) {
        handler_args.mutex.unlock();
        return;
    }

    jint res;
    int attached = 0; // know if detaching at the end is necessary
    // check if it is needed to attach current thread
    if ((res = handler_args.vm->GetEnv((void**) &env, JNI_VERSION)) == JNI_EDETACHED) {
#ifdef __ANDROID__
        if (handler_args.vm->AttachCurrentThread(&env, 0) == JNI_OK)
#else
        if (handler_args.vm->AttachCurrentThread((void**) &env, 0) == JNI_OK)
#endif
            attached = 1;
        else {
            // cannot attach current thread
            handler_args.mutex.unlock();
            return;
        }
    } else if (res != JNI_OK) {
        // cannot get env
        handler_args.mutex.unlock();
        return;
    }

    field = env->GetStaticFieldID(
        handler_args.logLevelClass, logLevelMapping(message->level), "L" LOG_LEVEL_CLASS ";");
    levelObj = env->GetStaticObjectField(handler_args.logLevelClass, field);

    jmodule = env->NewStringUTF(message->module);
    jmess = env->NewStringUTF(message->text);

    env->CallVoidMethod(handler_args.callback, handler_args.methID, levelObj, jmodule, jmess);

    if (attached) handler_args.vm->DetachCurrentThread();

    handler_args.mutex.unlock();
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Logger_setLevel(
    JNIEnv* env, jclass clazz, jobject jlevel) {
    jclass logLevelClass = NULL;
    roc_log_level level = (roc_log_level) 0;

    if (jlevel == NULL) {
        jclass exceptionClass = env->FindClass(ILLEGAL_ARGUMENTS_EXCEPTION);
        env->ThrowNew(exceptionClass, "no logger level provided");
        return;
    }

    level = (roc_log_level) get_enum_value(env, handler_args.logLevelClass, jlevel);
    roc_log_set_level(level);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Logger_setCallback(
    JNIEnv* env, jclass clazz, jobject jhandler) {
    jclass logHandlerClass = NULL;
    jmethodID tmpMethodID = NULL;

    if (jhandler == NULL) { // reset default callback (write to stderr)
        roc_log_set_handler(NULL, NULL);
        return;
    }

    logHandlerClass = env->GetObjectClass(jhandler);
    tmpMethodID = (jmethodID) env->GetMethodID(
        logHandlerClass, "log", "(L" LOG_LEVEL_CLASS ";Ljava/lang/String;Ljava/lang/String;)V");
    if (tmpMethodID == NULL) return;

    handler_args.mutex.lock();
    env->GetJavaVM(&handler_args.vm);
    if (handler_args.callback != NULL) {
        env->DeleteGlobalRef(handler_args.callback);
    }

    handler_args.callback = (jobject) env->NewGlobalRef(jhandler);
    handler_args.methID = tmpMethodID;
    handler_args.mutex.unlock();

    roc_log_set_handler(logger_handler, NULL);
}
