# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(ROC_JNI_BASE_DIR "../../roc_jni")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             roc_jni

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        ${ROC_JNI_BASE_DIR}/src/main/cpp/address.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/channel_set.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/common.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/context.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/family.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/fec_code.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/frame_encoding.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/logger.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/log_level.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/packet_encoding.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/port_type.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/protocol.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/receiver.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/resampler_profile.cpp
        ${ROC_JNI_BASE_DIR}/src/main/cpp/sender.cpp
        )

include_directories(${ROC_JNI_BASE_DIR}/src/main/headers/
                    ${ROC_JNI_BASE_DIR}/src/main/public/
                    )

target_include_directories(roc_jni PRIVATE
        ${ROC_BASE_DIR}/include/${ANDROID_ABI}
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib
              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
add_library(lib_roc SHARED IMPORTED)
set_target_properties(lib_roc PROPERTIES IMPORTED_LOCATION
        ${ROC_BASE_DIR}/lib/${ANDROID_ABI}/libroc.so)

target_link_libraries( # Specifies the target library.
                       roc_jni

                        lib_roc
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )