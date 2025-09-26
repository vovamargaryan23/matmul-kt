package com.matmul.matrix

expect class Matrix constructor(rows: Int, cols: Int, data: DoubleArray?) : AutoCloseable {
    val rows: Int
    val cols: Int
    fun multiply(other: Matrix): Matrix
    fun toDoubleArray(): DoubleArray
    override fun close()
}
