#pragma once

#include <jni.h>

#include <assert.h>
#include <string.h>

#define JNI_VERSION JNI_VERSION_1_6

#define PACKAGE_BASE_NAME "org/rocstreaming/roctoolkit"
#define EXCEPTION "java/lang/Exception"
#define ILLEGAL_ARGUMENTS_EXCEPTION "java/lang/IllegalArgumentException"
#define IO_EXCEPTION "java/io/IOException"

int get_boolean_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error);

int get_int_field_value(JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error);
unsigned int get_uint_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error);

long long get_llong_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error);
unsigned long long get_ullong_field_value(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int* error);

void set_int_field_value(JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, int value);

int get_enum_value(JNIEnv* env, jclass clazz, jobject enumObj);
jobject get_object_field(
    JNIEnv* env, jclass clazz, jobject obj, const char* attr_name, const char* attr_class_name);
