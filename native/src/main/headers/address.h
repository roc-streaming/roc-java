#include <jni.h>

#ifndef ADDRESS_H_
#define ADDRESS_H_
#ifdef __cplusplus
extern "C" {
#endif

#include <roc/address.h>

int address_unmarshall(JNIEnv *env, roc_address* address, jobject jaddress);

void address_set_port(JNIEnv *env, jobject address, int port);

roc_family address_get_family(JNIEnv *env, jobject address);
void address_set_roc_family(JNIEnv *env, jobject address, roc_family family);



#ifdef __cplusplus
}
#endif
#endif /* ADDRESS_H_ */
