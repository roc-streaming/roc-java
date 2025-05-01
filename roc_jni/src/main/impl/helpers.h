#pragma once

#include "platform.h"

#include <jni.h>

#include <stdbool.h>
#include <stdlib.h>

#define JNI_VERSION JNI_VERSION_1_6

ATTR_PRINTF(3, 4)
void throw_exception(JNIEnv* env, const char* exception, const char* message, ...);

ATTR_NODISCARD jclass find_class(JNIEnv* env, const char* class_name);

ATTR_NODISCARD jmethodID find_method(JNIEnv* env, jclass jclass, const char* class_name,
    const char* method_name, const char* method_sig);

ATTR_NODISCARD jfieldID find_field(JNIEnv* env, jclass jclass, const char* class_name,
    const char* field_name, const char* field_type);

ATTR_NODISCARD jobject find_enum_constant(
    JNIEnv* env, jclass jclass, const char* enum_class, int enum_value);

ATTR_NODISCARD bool read_bool_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, int* result);

ATTR_NODISCARD bool read_int_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, int* result);

ATTR_NODISCARD bool read_uint_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, unsigned int* result);

ATTR_NODISCARD bool read_long_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, long long* result);

ATTR_NODISCARD bool read_ulong_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, unsigned long long* result);

ATTR_NODISCARD bool read_signed_duration_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, long long* result);

ATTR_NODISCARD bool read_unsigned_duration_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, unsigned long long* result);

ATTR_NODISCARD bool read_object_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, const char* field_type, jobject* result);

ATTR_NODISCARD bool read_string_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, char* buf, size_t bufsz);

ATTR_NODISCARD bool read_enum_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, const char* field_type, int* result);
