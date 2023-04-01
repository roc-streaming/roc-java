#include <jni.h>

#ifndef CLOCK_SOURCE_H_
#define CLOCK_SOURCE_H_

#include "common.h"

#include <roc/config.h>

#define CLOCK_SOURCE_CLASS PACKAGE_BASE_NAME "/ClockSource"

roc_clock_source get_clock_source(JNIEnv* env, jobject jclock_source);

#endif /* CLOCK_SOURCE_H_ */
