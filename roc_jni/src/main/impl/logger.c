#include "org_rocstreaming_roctoolkit_Logger.h"

#include "common.h"

#include <roc/log.h>

#include <pthread.h>

#define LOG_LEVEL_CLASS PACKAGE_BASE_NAME "/LogLevel"

static pthread_mutex_t logMutex = PTHREAD_MUTEX_INITIALIZER;

static struct {
    JavaVM* vm;
    jclass levelClass;
    jobject handlerObject;
    jmethodID handlerMethod;
} logState = { 0 };

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

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jclass levelClass = NULL;
    jint result = JNI_VERSION;

    pthread_mutex_lock(&logMutex);

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION) != JNI_OK) {
        result = JNI_ERR;
        goto out;
    }

    assert(env != NULL);

    levelClass = (*env)->FindClass(env, LOG_LEVEL_CLASS);
    assert(levelClass != NULL);

    logState.levelClass = (jclass) (*env)->NewGlobalRef(env, levelClass);
    assert(logState.levelClass != NULL);

out:
    pthread_mutex_unlock(&logMutex);

    return result;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;

    pthread_mutex_lock(&logMutex);

    (*logState.vm)->GetEnv(vm, (void**) &env, JNI_VERSION);

    assert(env != NULL);
    (*env)->DeleteGlobalRef(env, logState.levelClass);

    logState.vm = NULL;
    logState.handlerObject = NULL;
    logState.handlerMethod = NULL;

    pthread_mutex_unlock(&logMutex);
}

static void logger_handler(const roc_log_message* message, void* argument) {
    JNIEnv* env = NULL;
    jfieldID levelFieldID = NULL;
    jobject msgLevel = NULL;
    jstring msgModule = NULL;
    jstring msgText = NULL;
    jint result = 0;
    int detach = 0;

    pthread_mutex_lock(&logMutex);

    if (logState.vm == NULL || logState.handlerObject == NULL || logState.handlerMethod == NULL) {
        goto out;
    }

    // check if it is needed to attach current thread
    if ((result = (*logState.vm)->GetEnv(logState.vm, (void**) &env, JNI_VERSION))
        == JNI_EDETACHED) {
#ifdef __ANDROID__
        if ((*logState.vm)->AttachCurrentThread(logState.vm, &env, 0) == JNI_OK)
#else
        if ((*logState.vm)->AttachCurrentThread(logState.vm, (void**) &env, 0) == JNI_OK)
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

    levelFieldID = (*env)->GetStaticFieldID(
        env, logState.levelClass, logLevelMapping(message->level), "L" LOG_LEVEL_CLASS ";");
    assert(levelFieldID != NULL);

    msgLevel = (*env)->GetStaticObjectField(env, logState.levelClass, levelFieldID);
    assert(msgLevel != NULL);

    msgModule = (*env)->NewStringUTF(env, message->module);
    assert(msgModule != NULL);

    msgText = (*env)->NewStringUTF(env, message->text);
    assert(msgText != NULL);

    (*env)->CallVoidMethod(
        env, logState.handlerObject, logState.handlerMethod, msgLevel, msgModule, msgText);

    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }

out:
    if (detach) {
        (*logState.vm)->DetachCurrentThread(logState.vm);
    }

    pthread_mutex_unlock(&logMutex);
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Logger_setLevel(
    JNIEnv* env, jclass clazz, jobject jlevel) {
    roc_log_level level = (roc_log_level) 0;
    int success = 0;

    pthread_mutex_lock(&logMutex);

    if (jlevel == NULL) {
        jclass exceptionClass = (*env)->FindClass(env, ILLEGAL_ARGUMENTS_EXCEPTION);
        (*env)->ThrowNew(env, exceptionClass, "no logger level provided");
        goto out;
    }

    level = (roc_log_level) get_enum_value(env, logState.levelClass, jlevel);
    success = 1;

out:
    pthread_mutex_unlock(&logMutex);

    if (success) {
        roc_log_set_level(level);
    }
}

JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_Logger_setCallbackNative(
    JNIEnv* env, jclass clazz, jobject jhandler) {
    jclass handlerClass = NULL;
    jmethodID handlerMethod = NULL;
    int success = 0;

    pthread_mutex_lock(&logMutex);

    if (jhandler == NULL) {
        roc_log_set_handler(NULL, NULL);
        goto out;
    }

    handlerClass = (*env)->GetObjectClass(env, jhandler);
    assert(handlerClass != NULL);

    handlerMethod = (jmethodID) (*env)->GetMethodID(
        env, handlerClass, "log", "(L" LOG_LEVEL_CLASS ";Ljava/lang/String;Ljava/lang/String;)V");
    assert(handlerMethod != NULL);

    if (logState.vm == NULL) {
        (*env)->GetJavaVM(env, &logState.vm);
    }
    if (logState.handlerObject != NULL) {
        (*env)->DeleteGlobalRef(env, logState.handlerObject);
    }
    logState.handlerObject = (jobject) (*env)->NewGlobalRef(env, jhandler);
    logState.handlerMethod = handlerMethod;

    success = 1;

out:
    pthread_mutex_unlock(&logMutex);

    if (success) {
        roc_log_set_handler(logger_handler, NULL);
    }
}
