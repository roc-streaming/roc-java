#include "helpers.h"
#include "exceptions.h"

#include <assert.h>
#include <limits.h>
#include <stdarg.h>
#include <stdio.h>
#include <string.h>

void throw_exception(JNIEnv* env, const char* exception, const char* message, ...) {
    assert(env);
    assert(exception);
    assert(message);

    // Another exception is pending, ignore subsequent exceptions during this native call.
    if ((*env)->ExceptionCheck(env)) return;

    char text[1024] = {};
    va_list args;
    va_start(args, message);
    vsnprintf(text, sizeof(text) - 1, message, args);
    va_end(args);

    jclass jclass = (*env)->FindClass(env, exception);
    assert(jclass);

    (*env)->ThrowNew(env, jclass, text);
}

jclass find_class(JNIEnv* env, const char* class_name) {
    assert(env);
    assert(class_name);

    jclass result = (*env)->FindClass(env, class_name);
    if (!result) {
        return NULL;
    }

    return result;
}

jmethodID find_method(JNIEnv* env, jclass jclass, const char* class_name, const char* method_name,
    const char* method_sig) {
    assert(env);
    assert(jclass);
    assert(class_name);
    assert(method_name);
    assert(method_sig);

    jmethodID result = (*env)->GetMethodID(env, jclass, method_name, method_sig);
    if (!result) {
        return NULL;
    }

    return result;
}

jfieldID find_field(JNIEnv* env, jclass jclass, const char* class_name, const char* field_name,
    const char* field_type) {
    assert(env);
    assert(jclass);
    assert(class_name);
    assert(field_name);
    assert(field_type);

    char field_sig[256] = {};
    if (strlen(field_type) == 1) {
        strncpy(field_sig, field_type, sizeof(field_sig) - 1);
    } else {
        snprintf(field_sig, sizeof(field_sig) - 1, "L%s;", field_type);
    }

    jfieldID result = (*env)->GetFieldID(env, jclass, field_name, field_sig);
    if (!result) {
        return NULL;
    }

    return result;
}

jobject find_enum_constant(JNIEnv* env, jclass jclass, const char* enum_class, int enum_value) {
    assert(env);
    assert(jclass);
    assert(enum_class);
    assert(enum_value);

    jfieldID value_field = (*env)->GetFieldID(env, jclass, "value", "I");
    if (!value_field) {
        return NULL;
    }

    char method_sig[256] = {};
    snprintf(method_sig, sizeof(method_sig) - 1, "()[L%s;", enum_class);

    jmethodID values_method = (*env)->GetStaticMethodID(env, jclass, "values", method_sig);
    if (!values_method) {
        return NULL;
    }

    jobjectArray enum_values_array
        = (jobjectArray) (*env)->CallStaticObjectMethod(env, jclass, values_method);
    if ((*env)->ExceptionCheck(env)) {
        return NULL;
    }
    if (!enum_values_array) {
        throw_exception(env, ASSERTION_ERROR, "%s.values() returned null", enum_class);
        return NULL;
    }

    jsize enum_values_count = (*env)->GetArrayLength(env, enum_values_array);

    jobject result = NULL;

    for (jsize i = 0; i < enum_values_count; i++) {
        jobject value_object = (*env)->GetObjectArrayElement(env, enum_values_array, i);
        if (!value_object) {
            continue;
        }

        jint value_code = (*env)->GetIntField(env, value_object, value_field);
        if (value_code == enum_value) {
            result = value_object;
            break;
        }

        (*env)->DeleteLocalRef(env, value_object);
    }

    (*env)->DeleteLocalRef(env, enum_values_array);

    if (!result) {
        throw_exception(env, ASSERTION_ERROR, "Missing constant for value %d in %s enum",
            enum_value, enum_class);
        return NULL;
    }

    return result;
}

bool read_bool_field(JNIEnv* env, jclass jobj_class, jobject jobj, const char* class_name,
    const char* field_name, int* result) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(class_name);
    assert(field_name);
    assert(result);

    jfieldID jfid = find_field(env, jobj_class, class_name, field_name, "Z");
    if (!jfid) {
        return false;
    }

    *result = (*env)->GetBooleanField(env, jobj, jfid) == JNI_TRUE;
    return true;
}

bool read_int_field(JNIEnv* env, jclass jobj_class, jobject jobj, const char* class_name,
    const char* field_name, int* result) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(class_name);
    assert(field_name);
    assert(result);

    jfieldID jfid = find_field(env, jobj_class, class_name, field_name, "I");
    if (!jfid) {
        return false;
    }

    *result = (int) (*env)->GetIntField(env, jobj, jfid);
    return true;
}

bool read_uint_field(JNIEnv* env, jclass jobj_class, jobject jobj, const char* class_name,
    const char* field_name, unsigned int* result) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(class_name);
    assert(field_name);
    assert(result);

    jfieldID jfid = find_field(env, jobj_class, class_name, field_name, "I");
    if (!jfid) {
        return false;
    }

    jint jfval = (*env)->GetIntField(env, jobj, jfid);
    if (jfval < 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid %s.%s: most not be negative",
            class_name, field_name);
        return false;
    }

    *result = (unsigned int) jfval;
    return true;
}

bool read_long_field(JNIEnv* env, jclass jobj_class, jobject jobj, const char* class_name,
    const char* field_name, long long* result) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(class_name);
    assert(field_name);
    assert(result);

    jfieldID jfid = find_field(env, jobj_class, class_name, field_name, "J");
    if (!jfid) {
        return false;
    }

    *result = (long long) (*env)->GetLongField(env, jobj, jfid);
    return true;
}

bool read_ulong_field(JNIEnv* env, jclass jobj_class, jobject jobj, const char* class_name,
    const char* field_name, unsigned long long* result) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(class_name);
    assert(field_name);
    assert(result);

    jfieldID jfid = find_field(env, jobj_class, class_name, field_name, "J");
    if (!jfid) {
        return false;
    }

    jlong jfval = (*env)->GetIntField(env, jobj, jfid);
    if (jfval < 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid %s.%s: must not be negative",
            class_name, field_name);
        return false;
    }

    *result = (unsigned long long) jfval;
    return true;
}

bool read_signed_duration_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, long long* result) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(class_name);
    assert(field_name);
    assert(result);

    jfieldID jfid = find_field(env, jobj_class, class_name, field_name, "java/time/Duration");
    if (!jfid) {
        return false;
    }

    jclass jduration_class = find_class(env, "java/time/Duration");
    if (!jduration_class) {
        return false;
    }

    jmethodID jto_nanos_method = find_method(env, jduration_class, "Duration", "toNanos", "()J");
    if (!jto_nanos_method) {
        return false;
    }

    jobject duration_obj = (*env)->GetObjectField(env, jobj, jfid);
    if (!duration_obj) {
        // treat null as zero value
        *result = 0;
        return true;
    }

    jlong jfval = (*env)->CallLongMethod(env, duration_obj, jto_nanos_method);
    if ((*env)->ExceptionCheck(env)) {
        return false;
    }

    *result = (long long) jfval;
    return true;
}

bool read_unsigned_duration_field(JNIEnv* env, jclass jobj_class, jobject jobj,
    const char* class_name, const char* field_name, unsigned long long* result) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(class_name);
    assert(field_name);
    assert(result);

    jfieldID jfid = find_field(env, jobj_class, class_name, field_name, "java/time/Duration");
    if (!jfid) {
        return false;
    }

    jclass jduration_class = find_class(env, "java/time/Duration");
    if (!jduration_class) {
        return false;
    }

    jmethodID jto_nanos_method = find_method(env, jduration_class, "Duration", "toNanos", "()J");
    if (!jto_nanos_method) {
        return false;
    }

    jobject duration_obj = (*env)->GetObjectField(env, jobj, jfid);
    if (!duration_obj) {
        // treat null as zero value
        *result = 0;
        return true;
    }

    jlong jfval = (*env)->CallLongMethod(env, duration_obj, jto_nanos_method);
    if ((*env)->ExceptionCheck(env)) {
        return false;
    }
    if (jfval < 0) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid %s.%s: must not be negative",
            class_name, field_name);
        return false;
    }

    *result = (unsigned long long) jfval;
    return true;
}

bool read_object_field(JNIEnv* env, jclass jobj_class, jobject jobj, const char* class_name,
    const char* field_name, const char* field_type, jobject* result) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(class_name);
    assert(field_name);
    assert(field_type);

    jfieldID jfid = find_field(env, jobj_class, class_name, field_name, field_type);
    if (!jfid) {
        return false;
    }

    *result = (*env)->GetObjectField(env, jobj, jfid);
    return true;
}

bool read_string_field(JNIEnv* env, jclass jobj_class, jobject jobj, const char* class_name,
    const char* field_name, char* buf, size_t bufsz) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(field_name);
    assert(buf);
    assert(bufsz > 1);

    jfieldID jfid = find_field(env, jobj_class, class_name, field_name, "java/lang/String");
    if (!jfid) {
        return false;
    }

    jstring jfval = (*env)->GetObjectField(env, jobj, jfid);
    if (!jfval) {
        // treat null as empty string
        buf[0] = '\0';
        return true;
    }

    const char* str = (*env)->GetStringUTFChars(env, jfval, 0);
    if (!str) {
        throw_exception(env, ASSERTION_ERROR, "GetStringUTFChars() failed");
        return false;
    }
    if (strlen(str) >= bufsz) {
        throw_exception(env, ILLEGAL_ARGUMENT_EXCEPTION, "Invalid %s.%s: must not exceed %lu bytes",
            class_name, field_name, (unsigned long) bufsz - 1);
        (*env)->ReleaseStringUTFChars(env, jfval, str);
        return false;
    }

    strcpy(buf, str);
    (*env)->ReleaseStringUTFChars(env, jfval, str);
    return true;
}

bool read_enum_field(JNIEnv* env, jclass jobj_class, jobject jobj, const char* class_name,
    const char* field_name, const char* field_type, int* result) {
    assert(env);
    assert(jobj_class);
    assert(jobj);
    assert(class_name);
    assert(field_name);
    assert(field_type);
    assert(result);

    jfieldID jobject_fid = find_field(env, jobj_class, class_name, field_name, field_type);
    if (!jobject_fid) {
        return false;
    }

    jclass jenum_class = find_class(env, field_type);
    if (!jenum_class) {
        return false;
    }

    jfieldID jenum_value_fid = find_field(env, jenum_class, field_type, "value", "I");
    if (!jenum_value_fid) {
        return false;
    }

    jobject jenum_object = (*env)->GetObjectField(env, jobj, jobject_fid);
    if (!jenum_object) {
        // treat null as zero value
        *result = 0;
        return true;
    }

    *result = (int) (*env)->GetIntField(env, jenum_object, jenum_value_fid);
    return true;
}
