#ifndef MATMUL_MATRIX_H
#define MATMUL_MATRIX_H

#include <cstddef>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct MatrixHandle MatrixHandle;

MatrixHandle* matrix_create(size_t rows, size_t cols, const double* data);
MatrixHandle* matrix_multiply(const MatrixHandle* matrix1, const MatrixHandle* matrix2);
size_t matrix_get_rows(const MatrixHandle* matrix);
size_t matrix_get_cols(const MatrixHandle* matrix);
void matrix_destroy(MatrixHandle* matrix);

const char* matrix_last_error();

int matrix_copy_data(const MatrixHandle* m_handle, double* outBuffer, size_t outLen);

#ifdef __cplusplus
}
#endif

#endif