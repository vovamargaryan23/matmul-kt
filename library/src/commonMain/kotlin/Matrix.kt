package com.matmul.matrix

expect class Matrix internal constructor(nativeHandle: Long) : AutoCloseable {
    constructor(rows: Int, cols: Int, data: DoubleArray?)
    val rows: Int
    val cols: Int
    fun multiply(other: Matrix): Matrix
    fun toDoubleArray(): DoubleArray
    override fun close()
}
