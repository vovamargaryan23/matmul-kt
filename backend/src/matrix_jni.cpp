#include "matrix_jni.h"

JNIEXPORT jlong JNICALL Java_com_matmul_matrix_Matrix_nativeCreate(JNIEnv* env, jobject, jint rows, jint cols, jdoubleArray data) {
    jdouble* arr = nullptr;
    jsize len = 0;
    if (data != nullptr) {
        len = env->GetArrayLength(data);
        arr = env->GetDoubleArrayElements(data, nullptr);
    }
    MatrixHandle* h = matrix_create((size_t)rows, (size_t)cols, arr);
    if (arr) env->ReleaseDoubleArrayElements(data, arr, 0);
    return reinterpret_cast<jlong>(h);
}

JNIEXPORT jlong JNICALL Java_com_matmul_matrix_Matrix_nativeMultiply(JNIEnv*, jobject, jlong a, jlong b) {
    MatrixHandle* ma = reinterpret_cast<MatrixHandle*>(a);
    MatrixHandle* mb = reinterpret_cast<MatrixHandle*>(b);
    MatrixHandle* result = matrix_multiply(ma, mb);
    return reinterpret_cast<jlong>(result);
}
JNIEXPORT void JNICALL Java_com_matmul_matrix_Matrix_nativeFree(JNIEnv*, jobject, jlong handle) {
    MatrixHandle* ma = reinterpret_cast<MatrixHandle*>(handle);
    matrix_destroy(ma);
}
JNIEXPORT jint JNICALL Java_com_matmul_matrix_Matrix_nativeRows(JNIEnv*, jobject, jlong handle) {
    MatrixHandle *ma = reinterpret_cast<MatrixHandle*>(handle);
    return matrix_get_rows(ma);
}
JNIEXPORT jint JNICALL Java_com_matmul_matrix_Matrix_nativeCols(JNIEnv*, jobject, jlong handle) {
    MatrixHandle *ma = reinterpret_cast<MatrixHandle*>(handle);
    return matrix_get_cols(ma);
}
JNIEXPORT jint JNICALL Java_com_matmul_matrix_Matrix_nativeCopyData(JNIEnv* env, jobject, jlong handle, jdoubleArray out) {
    MatrixHandle* m = reinterpret_cast<MatrixHandle*>(handle);
    jsize len = env->GetArrayLength(out);
    jdouble* arr = env->GetDoubleArrayElements(out, nullptr);
    int rc = matrix_copy_data(m, arr, len);
    env->ReleaseDoubleArrayElements(out, arr, 0);
    return rc;
}
JNIEXPORT jstring JNICALL Java_com_matmul_matrix_Matrix_nativeLastError(JNIEnv* env, jobject) {
    const char* msg = matrix_last_error();
    if (!msg) return nullptr;
    return env->NewStringUTF(msg);
}