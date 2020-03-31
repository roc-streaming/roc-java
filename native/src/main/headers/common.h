#include <jni.h>

#ifndef COMMON_H_
#define COMMON_H_
#ifdef __cplusplus
extern "C" {
#endif

#define JNI_VERSION                 JNI_VERSION_1_8

int get_int_field_value(JNIEnv *env, jclass clazz, jobject obj, const char* attr_name);
void* get_native_pointer(JNIEnv *env, jclass clazz, jobject native_obj);
void set_native_pointer(JNIEnv *env, jclass clazz, jobject native_obj, void* ptr);
void set_int_field_value(JNIEnv *env, jclass clazz, jobject obj, const char* attr_name, int value);
int get_long_field_value(JNIEnv *env, jclass clazz, jobject obj, const char* attr_name);
int get_enum_value(JNIEnv *env, jclass clazz, jobject enumObj);
jobject get_object_field(JNIEnv *env, jclass clazz, jobject obj, const char* attr_name, const char* attr_class_name);

#ifdef __cplusplus
}
#endif
#endif /* COMMON_H_ */