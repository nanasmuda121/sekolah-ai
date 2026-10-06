cmake_minimum_required(VERSION 3.22.1)
project(nanas_ai)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -flto -DANDROID")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -flto -DANDROID")

add_library(nanas_ai SHARED
    nanas-ai.cpp
)

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(nanas_ai
    ${log-lib}
    ${android-lib}
)
