cmake_minimum_required(VERSION 3.10)
project(matmul_native LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_library(matrix SHARED src/matrix.cpp)
target_include_directories(matrix PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/include)

# We only need JNI
set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)

find_package(Java REQUIRED)
find_package(JNI REQUIRED)
if(JNI_FOUND)
    add_library(matrix_jni SHARED src/matrix_jni.cpp)
    target_include_directories(matrix_jni PRIVATE ${JNI_INCLUDE_DIRS} ${CMAKE_CURRENT_SOURCE_DIR}/include)
    target_link_libraries(matrix_jni PRIVATE matrix)
endif ()