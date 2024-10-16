#include "common.h"

#include <limits.h>

int get_boolean_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attrName, int* error) {
    assert(env != NULL);
    assert(clazz != NULL);

    jfieldID attrId = (*env)->GetFieldID(env, clazz, attrName, "Z");
    assert(attrId != NULL);

    return (*env)->GetBooleanField(env, obj, attrId) == JNI_TRUE;
}

int get_int_field_value(JNIEnv* env, jclass clazz, jobject obj, const char* attrName, int* error) {
    assert(env != NULL);
    assert(clazz != NULL);
    assert(attrName != NULL);
    assert(error != NULL);

    jfieldID attrId = (*env)->GetFieldID(env, clazz, attrName, "I");
    assert(attrId != NULL);

    jint ret = (*env)->GetIntField(env, obj, attrId);
    if (ret < INT_MIN || ret > INT_MAX) {
        *error = -1;
        return 0;
    }

    return (int) ret;
}

unsigned int get_uint_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attrName, int* error) {
    assert(env != NULL);
    assert(clazz != NULL);
    assert(attrName != NULL);
    assert(error != NULL);

    jfieldID attrId = (*env)->GetFieldID(env, clazz, attrName, "I");
    assert(attrId != NULL);

    jint ret = (*env)->GetIntField(env, obj, attrId);
    if (ret < 0 || (unsigned int) ret > UINT_MAX) {
        *error = -1;
        return 0;
    }

    return (unsigned int) ret;
}

long long get_llong_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attrName, int* error) {
    assert(env != NULL);
    assert(clazz != NULL);
    assert(attrName != NULL);
    assert(error != NULL);

    jfieldID attrId = (*env)->GetFieldID(env, clazz, attrName, "J");
    assert(attrId != NULL);

    jlong ret = (*env)->GetLongField(env, obj, attrId);
    assert(sizeof(long long) == sizeof(jlong));

    return (long long) ret;
}

unsigned long long get_ullong_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attrName, int* error) {
    assert(env != NULL);
    assert(clazz != NULL);
    assert(attrName != NULL);
    assert(error != NULL);

    jfieldID attrId = (*env)->GetFieldID(env, clazz, attrName, "J");
    assert(attrId != NULL);

    jlong ret = (*env)->GetLongField(env, obj, attrId);
    if (ret < 0) {
        *error = -1;
        return 0LL;
    }

    return (unsigned long long) ret;
}

long long get_duration_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attrName, int* error) {
    assert(env != NULL);
    assert(clazz != NULL);
    assert(attrName != NULL);
    assert(error != NULL);

    jfieldID attrId = (*env)->GetFieldID(env, clazz, attrName, "Ljava/time/Duration;");
    assert(attrId != NULL);

    jobject durationObj = (*env)->GetObjectField(env, obj, attrId);
    if (durationObj == NULL) {
        return 0;
    }

    jclass durationClass = (*env)->FindClass(env, "java/time/Duration");
    assert(durationClass != NULL);

    jmethodID toNanosMethodId = (*env)->GetMethodID(env, durationClass, "toNanos", "()J");
    assert(toNanosMethodId != NULL);

    jlong ret = (*env)->CallLongMethod(env, durationObj, toNanosMethodId);
    return (long long) ret;
}

int get_enum_value(JNIEnv* env, jclass clazz, jobject enumObj) {
    assert(env != NULL);
    assert(clazz != NULL);

    if (enumObj != NULL) {
        jfieldID attrId = (*env)->GetFieldID(env, clazz, "value", "I");
        return (*env)->GetIntField(env, enumObj, attrId);
    }

    return 0;
}

jobject get_object_field(
    JNIEnv* env, jclass clazz, jobject obj, const char* attrName, const char* attrClassName) {
    assert(env != NULL);
    assert(clazz != NULL);

    jfieldID attrId = (*env)->GetFieldID(env, clazz, attrName, attrClassName);
    assert(attrId != NULL);

    return (*env)->GetObjectField(env, obj, attrId);
}
