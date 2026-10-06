cmake_minimum_required(VERSION 3.22.1)
project(nanas_ai)

set(CMAKE_CXX_STANDARD 17)

add_library(nanas_ai SHARED
    nanas-ai.cpp
)

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(nanas_ai
    ${log-lib}
    ${android-lib}
)
