cmake_minimum_required(VERSION 3.4.1)

set(ROC_JNI_BASE_DIR "../../roc_jni")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

add_library(roc_jni SHARED
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

add_library(lib_roc SHARED IMPORTED)
set_target_properties(lib_roc PROPERTIES IMPORTED_LOCATION
        ${ROC_BASE_DIR}/lib/${ANDROID_ABI}/libroc.so)

target_link_libraries(roc_jni lib_roc)