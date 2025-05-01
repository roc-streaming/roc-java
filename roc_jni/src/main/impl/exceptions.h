#pragma once

#include "package.h"

// User called us with invalid arguments.
// Indicates bug in caller's code.
// Unchecked exception.
#define ILLEGAL_ARGUMENT_EXCEPTION "java/lang/IllegalArgumentException"

// User called us before fulfilling prerequisites.
// Indicates bug in caller's code.
// Unchecked exception.
#define ILLEGAL_STATE_EXCEPTION "java/lang/IllegalStateException"

// Unexpected internal failure: class lookup failure, allocation failure, etc.
// Indicates bug in JNI code.
// Unchecked exception.
#define ASSERTION_ERROR "java/lang/AssertionError"

// Generic system failure caused by external means.
// Indicates condition to be handled in caller's code.
// Checked exception.
#define ROC_EXCEPTION PACKAGE_NAME "/RocException"
