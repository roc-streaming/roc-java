#include "common.h"

#include <limits.h>

int get_boolean_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error) {
    jfieldID attrId = (*env)->GetFieldID(env, clazz, attr_name, "Z");
    assert(attrId != NULL);
    return (*env)->GetBooleanField(env, obj, attrId) == JNI_TRUE;
}

int get_int_field_value(JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error) {
    jfieldID attrId = (*env)->GetFieldID(env, clazz, attr_name, "I");
    assert(attrId != NULL);
    jint ret = (*env)->GetIntField(env, obj, attrId);
    if (ret < INT_MIN || ret > INT_MAX) {
        *error = -1;
        return 0;
    }
    return (int) ret;
}

unsigned int get_uint_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error) {
    jfieldID attrId = (*env)->GetFieldID(env, clazz, attr_name, "I");
    assert(attrId != NULL);
    jint ret = (*env)->GetIntField(env, obj, attrId);
    if (ret < 0 || (unsigned int) ret > UINT_MAX) {
        *error = -1;
        return 0;
    }
    return (unsigned int) ret;
}

long long get_llong_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error) {
    jfieldID attrId = (*env)->GetFieldID(env, clazz, attr_name, "J");
    assert(attrId != NULL);
    jlong ret = (*env)->GetLongField(env, obj, attrId);
    assert(sizeof(long long) == sizeof(jlong));
    return (long long) ret;
}

unsigned long long get_ullong_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error) {
    jfieldID attrId = (*env)->GetFieldID(env, clazz, attr_name, "J");
    assert(attrId != NULL);
    jlong ret = (*env)->GetLongField(env, obj, attrId);
    if (ret < 0) {
        *error = -1;
        return 0LL;
    }
    return (unsigned long long) ret;
}

int get_enum_value(JNIEnv* env, jclass clazz, jobject enumObj) {
    if (enumObj != NULL) {
        jfieldID attrId = (*env)->GetFieldID(env, clazz, "value", "I");
        return (*env)->GetIntField(env, enumObj, attrId);
    }
    return 0;
}

jobject get_object_field(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, const char* attr_class_name) {
    jfieldID attrId = (*env)->GetFieldID(env, clazz, attr_name, attr_class_name);
    assert(attrId != NULL);
    return (*env)->GetObjectField(env, obj, attrId);
}
