package com.matmul.matrix

actual class Matrix internal actual constructor(var nativeHandle: Long) : AutoCloseable {
    init {
        NativeLoader.load()
    }
    actual constructor(rows: Int, cols: Int, data: DoubleArray?) : this(0L) {
        val arr = data ?: DoubleArray(0)
        val h = nativeCreate(rows, cols, arr)
        if(h == 0L) throw RuntimeException(nativeLastError() ?: "native create failed!")
        nativeHandle = h
    }
    private external fun nativeRows(handle: Long): Int
    private external fun nativeCols(handle: Long): Int
    private external fun nativeMultiply(a: Long, b: Long): Long
    private external fun nativeCopyData(handle: Long, out: DoubleArray): Int
    private external fun nativeFree(handle: Long): Int
    private external fun nativeLastError(): String?
    private external fun nativeCreate(rows: Int, cols: Int, data: DoubleArray?): Long

    actual val rows: Int
        get() = nativeRows(nativeHandle)
    actual val cols: Int
        get() = nativeCols(nativeHandle)

    actual fun multiply(other: Matrix): Matrix {
        val r = nativeMultiply(nativeHandle, other.nativeHandle)
        if(r == 0L) throw IllegalArgumentException(nativeLastError() ?: "native multiply failed!")
        return Matrix(r)
    }

    actual fun toDoubleArray(): DoubleArray {
        val out = DoubleArray(rows * cols)
        val rc = nativeCopyData(nativeHandle, out)
        if(rc != 0) throw RuntimeException(nativeLastError() ?: "native copy failed!")
        return out
    }
    actual override fun close() {
        nativeFree(nativeHandle)
    }
}

