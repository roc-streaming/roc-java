#include "com_rocproject_roc_address_Address.h"
#include "common.h"
#include "address.h"

#include <cassert>
#include <cstring>

#define ADDRESS_CLASS               "com/rocproject/roc/address/Address"
#define FAMILY_CLASS                "com/rocproject/roc/address/Family"

roc_family address_get_family(JNIEnv *env, jobject address) {
    jclass       addressClass;
    jclass       familyClass;
    jobject      tempObject;

    addressClass = env->FindClass(ADDRESS_CLASS);
    assert(addressClass != NULL);

    familyClass = env->FindClass(FAMILY_CLASS);
    assert(familyClass != NULL);

    tempObject = get_object_field(env, addressClass, address, "family", "L" FAMILY_CLASS ";");
    return (roc_family) get_enum_value(env, familyClass, tempObject);
}

int address_unmarshall(JNIEnv *env, roc_address* address, jobject jaddress) {
    jclass      addressClass;
    jclass      familyClass;
    jobject     tempObject;
    jstring     jstr;
    const char* ip;
    int         port;

    if (jaddress == NULL)
        return -1;

    addressClass = env->FindClass(ADDRESS_CLASS);
    assert(addressClass != NULL);

    familyClass  = env->FindClass(FAMILY_CLASS);
    assert(familyClass != NULL);

    memset(address, 0, sizeof(roc_address));

    tempObject = get_object_field(env, addressClass, jaddress, "family", "L" FAMILY_CLASS ";");
    roc_family family = (roc_family) get_enum_value(env, familyClass, tempObject);

    jstr = (jstring) get_object_field(env, addressClass, jaddress, "ip", "Ljava/lang/String;");
    ip = env->GetStringUTFChars(jstr, 0);
    assert(ip != NULL);

    port = get_int_field_value(env, addressClass, jaddress, "port");

    if (roc_address_init(address, family, ip, port) != 0) {
        env->ReleaseStringUTFChars(jstr, ip);
        return -1;
    }
    env->ReleaseStringUTFChars(jstr, ip);
    return 0;
}

void address_set_port(JNIEnv *env, jobject address, int port) {
    jclass      addressClass;
    jfieldID    attrId;

    addressClass = env->FindClass(ADDRESS_CLASS);
    assert(addressClass != NULL);

    attrId = env->GetFieldID(addressClass, "port", "I");
    assert(attrId != NULL);

    env->SetIntField(address, attrId, port);
}

void address_set_roc_family(JNIEnv *env, jobject address, roc_family family) {
    jclass      addressClass;
    jclass      familyClass;
    jfieldID    attrId;
    jfieldID    familyField;
    const char* familyValue;
    jobject     familyObj;

    addressClass = env->FindClass(ADDRESS_CLASS);
    assert(addressClass != NULL);

    familyClass = env->FindClass(FAMILY_CLASS);
    assert(familyClass != NULL);

    attrId = env->GetFieldID(addressClass, "family", "L" FAMILY_CLASS ";");
    assert(attrId != NULL);

    familyValue = ROC_AF_IPv4 == family ? "ROC_AF_IPv4" : (ROC_AF_IPv6 == family ? "ROC_AF_IPv6" : "ROC_AF_INVALID");
    familyField = env->GetStaticFieldID(familyClass, familyValue, "L" FAMILY_CLASS ";");
    assert(familyField != NULL);

    familyObj = env->GetStaticObjectField(familyClass, familyField);
    env->SetObjectField(address, attrId, familyObj);
}

/*
 * Class:     com_rocproject_roc_address_Address
 * Method:    init
 * Signature: (Lcom/rocproject/roc/address/Family;Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_address_Address_init(JNIEnv *env, jobject thisObj, jobject jfamily, jstring jip, jint port) {
    jclass          familyClass;
    roc_address     address;
    roc_family      family;
    const char*     ip;

    if (jfamily == NULL || jip == NULL || port < 0) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Bad address arguments");
        return;
    }

    familyClass = env->FindClass(FAMILY_CLASS);
    assert(familyClass != NULL);

    memset(&address, 0, sizeof(roc_address));

    family = (roc_family) get_enum_value(env, familyClass, jfamily);

    ip = env->GetStringUTFChars(jip, 0);
    if (roc_address_init(&address, family, ip, port) != 0) {
        env->ReleaseStringUTFChars(jip, ip);
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Bad address arguments");
        return;
    }
    env->ReleaseStringUTFChars(jip, ip);

    if (ROC_AF_AUTO == family) { // set family
        family = roc_address_family(&address);
        address_set_roc_family(env, thisObj, family);
    }
}