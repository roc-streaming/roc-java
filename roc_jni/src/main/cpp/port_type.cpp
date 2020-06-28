#include "org_rocstreaming_roctoolkit_PortType.h"

#include <roc/config.h>

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_PortType_getRocPortAudioSource(JNIEnv *, jclass) {
    return ROC_PORT_AUDIO_SOURCE;
}

JNIEXPORT jint JNICALL Java_org_rocstreaming_roctoolkit_PortType_getRocPortAudioRepair(JNIEnv *, jclass) {
  return ROC_PORT_AUDIO_REPAIR;
}