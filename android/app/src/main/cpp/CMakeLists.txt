cmake_minimum_required(VERSION 3.22.1)
project(native_ai)

# Kompilasi C++17 dengan optimasi ukuran & kecepatan ARM CPU
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -flto")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -flto")

# Native C++ JNI bridge
add_library(native_ai SHARED
    native-ai.cpp
)

target_include_directories(native_ai PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
)

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(native_ai
    ${log-lib}
    ${android-lib}
)
