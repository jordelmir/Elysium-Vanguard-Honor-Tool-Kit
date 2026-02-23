cmake_minimum_required(VERSION 3.22.1)
project("honormagictoolkit")

add_library(exploit_bridge SHARED exploit_bridge.cpp)
add_executable(esm_engine esm_engine.cpp)

find_library(log-lib log)

target_link_libraries(exploit_bridge ${log-lib})
target_link_libraries(esm_engine ${log-lib} android)
