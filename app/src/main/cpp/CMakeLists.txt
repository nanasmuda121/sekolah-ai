cmake_minimum_required(VERSION 3.22.1)
project(nanas_ai)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_C_STANDARD 11)

# Options for llama.cpp mobile build
set(GGML_NATIVE OFF CACHE BOOL "" FORCE)
set(GGML_OPENMP OFF CACHE BOOL "" FORCE)
set(GGML_LLAMAFILE OFF CACHE BOOL "" FORCE)
set(LLAMA_OPENSSL OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_SERVER OFF CACHE BOOL "" FORCE)
set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)

add_subdirectory(llama ${CMAKE_CURRENT_BINARY_DIR}/llama EXCLUDE_FROM_ALL)

add_library(nanas_ai SHARED
    nanas-ai.cpp
)

target_include_directories(nanas_ai PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/llama/include
    ${CMAKE_CURRENT_SOURCE_DIR}/llama/ggml/include
)

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(nanas_ai
    llama
    ${log-lib}
    ${android-lib}
)
