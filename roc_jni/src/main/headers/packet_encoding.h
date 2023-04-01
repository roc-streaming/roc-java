#pragma once

#include <jni.h>

#include "common.h"

#include <roc/config.h>

#define PACKET_ENCODING_CLASS PACKAGE_BASE_NAME "/PacketEncoding"

roc_packet_encoding get_packet_encoding(JNIEnv* env, jobject jpacket_encoding);
