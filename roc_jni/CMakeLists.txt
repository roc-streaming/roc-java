cmake_minimum_required(VERSION 3.4.1)

project(RocJni)

set(CMAKE_CXX_STANDARD 11)

if(APPLE)
    # on macOs 10.14+ /usr/local/include and /usr/local/lib are not in SDK
    # compiler and linker PATH so let's add them explicitly
    include_directories(SYSTEM /usr/local/include)
    link_directories(/usr/local/lib)
endif()

add_library(roc_jni SHARED
        src/main/cpp/channel_set.cpp
        src/main/cpp/clock_source.cpp
        src/main/cpp/common.cpp
        src/main/cpp/context.cpp
        src/main/cpp/endpoint.cpp
        src/main/cpp/fec_encoding.cpp
        src/main/cpp/frame_encoding.cpp
        src/main/cpp/logger.cpp
        src/main/cpp/packet_encoding.cpp
        src/main/cpp/protocol.cpp
        src/main/cpp/receiver.cpp
        src/main/cpp/resampler_backend.cpp
        src/main/cpp/resampler_profile.cpp
        src/main/cpp/sender.cpp
        )

if(NOT ANDROID)
    if (NOT CMAKE_CROSSCOMPILING)
        find_package(JNI REQUIRED)
    endif()
    # include jni headers
    target_include_directories(roc_jni SYSTEM PUBLIC ${JNI_INCLUDE_DIRS})
endif()

target_include_directories(roc_jni
                    PRIVATE src/main/headers/
                    PUBLIC src/main/public/
                    )

if(ANDROID)
    # include libroc headers
    target_include_directories(roc_jni SYSTEM PRIVATE
                            ${ROC_BASE_DIR}/include/${ANDROID_ABI})

    # link libroc shared library
    add_library(lib_roc SHARED IMPORTED)
    set_target_properties(lib_roc PROPERTIES IMPORTED_LOCATION
                            ${ROC_BASE_DIR}/lib/${ANDROID_ABI}/libroc.so)
    target_link_libraries(roc_jni lib_roc)
else()
    # include libroc headers
    if(DEFINED ROC_INCLUDE_PATH AND NOT ROC_INCLUDE_PATH STREQUAL "")
        target_include_directories(roc_jni SYSTEM PRIVATE
                                    ${ROC_INCLUDE_PATH})
    elseif(DEFINED ENV{ROC_INCLUDE_PATH}
            AND NOT ENV{ROC_INCLUDE_PATH} STREQUAL "")
        target_include_directories(roc_jni SYSTEM PRIVATE
                                    $ENV{ROC_INCLUDE_PATH})
    endif()

    # link libroc shared library
    if(DEFINED ROC_LIBRARY_PATH
        AND NOT ROC_LIBRARY_PATH STREQUAL "")
        add_library(lib_roc SHARED IMPORTED)
        set_target_properties(lib_roc PROPERTIES IMPORTED_LOCATION
                ${ROC_LIBRARY_PATH}/libroc${CMAKE_SHARED_LIBRARY_SUFFIX})
        target_link_libraries(roc_jni lib_roc)
    elseif(DEFINED ENV{ROC_LIBRARY_PATH}
        AND NOT ENV{ROC_LIBRARY_PATH} STREQUAL "")
        add_library(lib_roc SHARED IMPORTED)
        set_target_properties(lib_roc PROPERTIES IMPORTED_LOCATION
                $ENV{ROC_LIBRARY_PATH}/libroc${CMAKE_SHARED_LIBRARY_SUFFIX})
        target_link_libraries(roc_jni lib_roc)
    else()
        target_link_libraries(roc_jni -lroc)
    endif()
endif()
