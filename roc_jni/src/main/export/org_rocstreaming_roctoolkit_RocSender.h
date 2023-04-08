/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_rocstreaming_roctoolkit_RocSender */

#ifndef _Included_org_rocstreaming_roctoolkit_RocSender
#define _Included_org_rocstreaming_roctoolkit_RocSender
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_rocstreaming_roctoolkit_RocSender
 * Method:    open
 * Signature: (JLorg/rocstreaming/roctoolkit/RocSenderConfig;)J
 */
JNIEXPORT jlong JNICALL Java_org_rocstreaming_roctoolkit_RocSender_open
  (JNIEnv *, jclass, jlong, jobject);

/*
 * Class:     org_rocstreaming_roctoolkit_RocSender
 * Method:    setOutgoingAddress
 * Signature: (JIILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_setOutgoingAddress
  (JNIEnv *, jobject, jlong, jint, jint, jstring);

/*
 * Class:     org_rocstreaming_roctoolkit_RocSender
 * Method:    connect
 * Signature: (JIILorg/rocstreaming/roctoolkit/Endpoint;)V
 */
JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_connect
  (JNIEnv *, jobject, jlong, jint, jint, jobject);

/*
 * Class:     org_rocstreaming_roctoolkit_RocSender
 * Method:    writeFloats
 * Signature: (J[F)V
 */
JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_writeFloats
  (JNIEnv *, jobject, jlong, jfloatArray);

/*
 * Class:     org_rocstreaming_roctoolkit_RocSender
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_rocstreaming_roctoolkit_RocSender_close
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif
