#include "matrix.h"
#include <vector>
#include <cstring>
#include <regex>
#include <string>

thread_local std::string thread_local_error;

static void set_thread_local_error(const char* msg) {
    thread_local_error = msg ? msg : "";
}

const char *matrix_last_error() {
    return thread_local_error.empty() ? nullptr : thread_local_error.c_str();
}


class Matrix {
public:
    size_t r, c;
    std::vector<double> data;
    Matrix(const size_t rows, const size_t cols) : r(rows), c(cols), data(rows * cols) {}
    Matrix(const Matrix&) = delete;
    Matrix& operator=(const Matrix&) = delete;
    Matrix(Matrix&&) = default;
    Matrix& operator=(Matrix&&) = default;
};


MatrixHandle *matrix_create(size_t rows, size_t cols, const double *data) {
    try {
        auto *m = new Matrix(rows, cols);
        if (data && rows * cols > 0) {
            std::memcpy(m->data.data(), data, rows * cols * sizeof(double));
        }
        return reinterpret_cast<MatrixHandle *>(m);
    } catch (const std::exception &e) {
        set_thread_local_error(e.what());
        return nullptr;
    }
}

MatrixHandle *matrix_multiply(const MatrixHandle *matrix1, const MatrixHandle *matrix2) {
    if (!matrix1 || !matrix2) {
        set_thread_local_error("Invalid arguments!");
        return nullptr;
    }

    const auto* a = reinterpret_cast<const Matrix *>(matrix1);
    const auto* b = reinterpret_cast<const Matrix *>(matrix2);
    if (a->c != b->r) {
        set_thread_local_error("A.columns != B.rows");
        return nullptr;
    }
    try {
        auto* out = new Matrix(a->r, b->c);
        for (size_t i = 0; i < a->r; ++i) {
            for (size_t k = 0; k < a->c; ++k) {
                const double aik = a->data[i * a->c + k];
                for (size_t j = 0; j < b->c; ++j) {
                    out->data[i * out->c + j] += aik * b->data[k * b->c + j];
                }
            }
        }
        return reinterpret_cast<MatrixHandle *>(out);
    }catch (std::exception &e) {
        set_thread_local_error(e.what());
        return nullptr;
    }
}

int matrix_copy_data(const MatrixHandle* m_handle, double* outBuffer, size_t outLen) {
    if (!m_handle || !outBuffer) { set_thread_local_error("null arg"); return -1; }
    auto const* m = reinterpret_cast<const Matrix*>(m_handle);
    size_t need = m->r * m->c;
    if (outLen < need) { set_thread_local_error("buffer too small"); return -2; }
    std::memcpy(outBuffer, m->data.data(), need * sizeof(double));
    return 0;
}

size_t matrix_get_rows(const MatrixHandle *matrix) {
    if (!matrix) return 0;
    return reinterpret_cast<const Matrix *>(matrix)->r;
}

size_t matrix_get_cols(const MatrixHandle *matrix) {
    if (!matrix) return 0;
    return reinterpret_cast<const Matrix *>(matrix)->c;
}

void matrix_destroy(MatrixHandle *matrix) {
    if (!matrix) return;
    delete reinterpret_cast<Matrix *>(matrix);
}

