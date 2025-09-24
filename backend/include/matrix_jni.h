#include <jni.h>
#include "matrix.h"

#ifndef MATRIX_JNI_H
#define MATRIX_JNI_H
#ifdef __cplusplus
extern "C" {
#endif
    JNIEXPORT jlong JNICALL Java_com_matmul_matrix_Matrix_nativeCreate(JNIEnv* env, jobject, jint rows, jint cols, jdoubleArray data);
    JNIEXPORT jlong JNICALL Java_com_matmul_matrix_Matrix_nativeMultiply(JNIEnv*, jobject, jlong a, jlong b);
    JNIEXPORT void JNICALL Java_com_matmul_matrix_Matrix_nativeFree(JNIEnv*, jobject, jlong handle);
    JNIEXPORT jint JNICALL Java_com_matmul_matrix_Matrix_nativeRows(JNIEnv*, jobject, jlong handle);
    JNIEXPORT jint JNICALL Java_com_matmul_matrix_Matrix_nativeCols(JNIEnv*, jobject, jlong handle);
    JNIEXPORT jint JNICALL Java_com_matmul_matrix_Matrix_nativeCopyData(JNIEnv*, jobject, jlong handle, jdoubleArray out);
    JNIEXPORT jstring JNICALL Java_com_matmul_matrix_Matrix_nativeLastError(JNIEnv*, jobject);

#ifdef __cplusplus
}
#endif
#endif