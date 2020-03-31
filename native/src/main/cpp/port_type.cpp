#include "com_rocproject_roc_config_PortType.h"

#include <roc/config.h>

JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_PortType_getRocPortAudioSource(JNIEnv *, jclass) {
    return ROC_PORT_AUDIO_SOURCE;
}

/*
 * Class:     com_PortType
 * Method:    getRocPortAudioRepair
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_rocproject_roc_config_PortType_getRocPortAudioRepair(JNIEnv *, jclass) {
  return ROC_PORT_AUDIO_REPAIR;
}