# -*- cmake-tab-width: 4 -*-
cmake_minimum_required(VERSION 3.10)

project(RocJni C)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

if(APPLE)
    # on macOs 10.14+ /usr/local/include and /usr/local/lib are not in SDK
    # compiler and linker PATH so let's add them explicitly
    include_directories(SYSTEM /usr/local/include)
    link_directories(/usr/local/lib)
endif()

add_library(roc_jni SHARED
    src/main/impl/context.c
    src/main/impl/context_config.c
    src/main/impl/endpoint.c
    src/main/impl/helpers.c
    src/main/impl/interface_config.c
    src/main/impl/logger.c
    src/main/impl/media_encoding.c
    src/main/impl/receiver.c
    src/main/impl/receiver_config.c
    src/main/impl/sender.c
    src/main/impl/sender_config.c
)

target_compile_options(roc_jni PRIVATE
    -Wall
    -Wextra
    -Wno-system-headers
    -Wno-unused-but-set-variable
    -Wno-unused-parameter
    # enable assertions (even in release build)
    -UNDEBUG
)

if(NOT ANDROID)
    if (NOT CMAKE_CROSSCOMPILING)
        find_package(JNI REQUIRED)
    endif()
    # include jni headers
    target_include_directories(roc_jni SYSTEM PUBLIC ${JNI_INCLUDE_DIRS})
endif()

target_include_directories(roc_jni
    PRIVATE src/main/impl/
    PUBLIC src/main/export/
)

if(ANDROID)
    # include libroc headers
    target_include_directories(roc_jni SYSTEM PRIVATE
        ${ROC_DIR}/include/${ANDROID_ABI})

    # link libroc shared library
    add_library(lib_roc SHARED IMPORTED)
    set_target_properties(lib_roc PROPERTIES IMPORTED_LOCATION
        ${ROC_DIR}/lib/${ANDROID_ABI}/libroc.so)
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

add_custom_command(
    COMMENT "Copying compile_commands.json to project root"
    DEPENDS roc_jni
    OUTPUT ${PROJECT_SOURCE_DIR}/compile_commands.json
    COMMAND ${CMAKE_COMMAND} -E copy
        ${CMAKE_CURRENT_BINARY_DIR}/compile_commands.json
        ${PROJECT_SOURCE_DIR}/compile_commands.json
)
add_custom_target(compile_commands ALL
    DEPENDS ${PROJECT_SOURCE_DIR}/compile_commands.json
)
