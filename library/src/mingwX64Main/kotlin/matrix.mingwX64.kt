package com.matmul.matrix

import kotlinx.cinterop.*

actual class Matrix @OptIn(ExperimentalForeignApi::class)
private constructor(
    private var handle: CPointer<cnames.structs.MatrixHandle>? = null
) : AutoCloseable {
    @OptIn(ExperimentalForeignApi::class)
    actual constructor(rows: Int, cols: Int, data: DoubleArray?) : this() {
        memScoped {
            val arr = data ?: DoubleArray(rows * cols) { 0.0 }
            val ptr = arr.usePinned { pinned ->
                matrix_create(
                    rows.convert(),
                    cols.convert(),
                    pinned.addressOf(0)
                )
            }
            if (ptr == null) {
                val err = matrix_last_error()?.toKString() ?: "Unknown error"
                throw RuntimeException("matrix_create failed: $err")
            }
            handle = ptr
        }
    }
    @OptIn(ExperimentalForeignApi::class)
    actual val rows: Int
        get() = matrix_get_rows(handle).toInt()

    @OptIn(ExperimentalForeignApi::class)
    actual val cols: Int
        get() = matrix_get_cols(handle).toInt()

    @OptIn(ExperimentalForeignApi::class)
    actual fun multiply(other: Matrix): Matrix {
        val result = matrix_multiply(handle, other.handle)
        if (result == null) {
            val err = matrix_last_error()?.toKString() ?: "Unknown error"
            throw IllegalArgumentException("matrix_multiply failed: $err")
        }
        val mat = Matrix(1, 1, null) // dummy
        mat.handle = result as CPointer<cnames.structs.MatrixHandle>?
        return mat
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun toDoubleArray(): DoubleArray {
        val total = rows * cols
        val out = DoubleArray(total)
        memScoped {
            out.usePinned { pinned ->
                val rc = matrix_copy_data(handle, pinned.addressOf(0), total.convert())
                if (rc != 0) {
                    val err = matrix_last_error()?.toKString() ?: "Unknown error"
                    throw RuntimeException("matrix_copy_data failed: $err")
                }
            }
        }
        return out
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override fun close() {
        handle?.let {
            matrix_destroy(it)
            handle = null
        }
    }

}
