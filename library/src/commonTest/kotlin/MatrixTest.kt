package com.matmul.matrix

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MatrixTest {
    @Test
    fun `test matrix get rows and columns`() {
        val matrix = Matrix(3,2,doubleArrayOf(5.0,4.0,6.0,7.0,8.0,9.0))

        assertEquals(3, matrix.rows)
        assertEquals(2, matrix.cols)
    }

    @Test
    fun `test matrix multiplication`() {
        val a = Matrix(2, 2, doubleArrayOf(1.0,2.0,3.0,4.0))
        val b = Matrix(2,2,doubleArrayOf(5.0,4.0,6.0,7.0))

        val actual = a.multiply(b).toDoubleArray()
        val expected = doubleArrayOf(17.0,18.0,39.0,40.0)

        assertContentEquals(expected, actual)
    }

    @Test
    fun `shape mismatch throws exception`() {
        assertFailsWith<Exception> {
            val a = Matrix(2, 3, doubleArrayOf(1.0,2.0,3.0,4.0,5.0,6.0))
            val b = Matrix(2,2,doubleArrayOf(5.0,4.0,6.0,7.0))

            a.multiply(b)
        }
    }
}