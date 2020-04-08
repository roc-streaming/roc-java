#include "com_github_rocproject_roc_PortType.h"

#include <roc/config.h>

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_PortType_getRocPortAudioSource(JNIEnv *, jclass) {
    return ROC_PORT_AUDIO_SOURCE;
}

JNIEXPORT jint JNICALL Java_com_github_rocproject_roc_PortType_getRocPortAudioRepair(JNIEnv *, jclass) {
  return ROC_PORT_AUDIO_REPAIR;
}