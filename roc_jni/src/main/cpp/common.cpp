#include "common.h"

#include <cstring>
#include <cassert>

int get_enum_value(JNIEnv *env, jclass clazz, jobject enumObj) {
    if (enumObj != NULL) {
        jfieldID attrId = env->GetFieldID(clazz, "value", "I");
        return env->GetIntField(enumObj, attrId);
    }
    return 0;
}

jobject get_object_field(JNIEnv *env, jclass clazz, jobject obj, const char* attr_name, const char* attr_class_name) {
    jfieldID attrId = env->GetFieldID(clazz, attr_name, attr_class_name);
    assert(attrId != NULL);
    return env->GetObjectField(obj, attrId);
}

int get_int_field_value(JNIEnv *env, jclass clazz, jobject obj, const char* attr_name) {
    jfieldID attrId = env->GetFieldID(clazz, attr_name, "I");
    assert(attrId != NULL);
    return env->GetIntField(obj, attrId);
}

int get_long_field_value(JNIEnv *env, jclass clazz, jobject obj, const char* attr_name) {
    jfieldID attrId = env->GetFieldID(clazz, attr_name, "J");
    assert(attrId != NULL);
    return env->GetLongField(obj, attrId);
}

void* get_native_pointer(JNIEnv *env, jclass clazz, jobject native_obj) {
    jfieldID attrId = env->GetFieldID(clazz, "ptr", "J");
    assert(attrId != NULL);
    return (void*) env->GetLongField(native_obj, attrId);
}

void set_native_pointer(JNIEnv *env, jclass clazz, jobject native_obj, void* ptr) {
    jfieldID attrId = env->GetFieldID(clazz, "ptr", "J");
    assert(attrId != NULL);
    env->SetLongField(native_obj, attrId, (jlong) ptr);
}
