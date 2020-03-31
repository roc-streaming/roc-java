/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_rocproject_roc_sender_Sender */

#ifndef _Included_com_rocproject_roc_sender_Sender
#define _Included_com_rocproject_roc_sender_Sender
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_rocproject_roc_sender_Sender
 * Method:    bind
 * Signature: (Lcom/rocproject/roc/address/Address;)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_bind
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_rocproject_roc_sender_Sender
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_close
  (JNIEnv *, jobject);

/*
 * Class:     com_rocproject_roc_sender_Sender
 * Method:    senderOpen
 * Signature: (Lcom/rocproject/roc/context/Context;Lcom/rocproject/roc/config/SenderConfig;)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_senderOpen
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     com_rocproject_roc_sender_Sender
 * Method:    connect
 * Signature: (IILcom/rocproject/roc/address/Address;)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_connect
  (JNIEnv *, jobject, jint, jint, jobject);

/*
 * Class:     com_rocproject_roc_sender_Sender
 * Method:    writeFloat
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_writeFloat
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     com_rocproject_roc_sender_Sender
 * Method:    writeFloats
 * Signature: ([F)V
 */
JNIEXPORT void JNICALL Java_com_rocproject_roc_sender_Sender_writeFloats
  (JNIEnv *, jobject, jfloatArray);

#ifdef __cplusplus
}
#endif
#endif
