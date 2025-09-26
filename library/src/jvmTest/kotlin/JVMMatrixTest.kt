package com.matmul.matrix

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JVMMatrixTest {
    @Test
    fun createAndToDoubleArray() {
        val m = Matrix(2, 2, doubleArrayOf(1.0, 2.0, 3.0, 4.0))
        try {
            assertEquals(2, m.rows)
            assertEquals(2, m.cols)
            assertContentEquals(doubleArrayOf(1.0, 2.0, 3.0, 4.0), m.toDoubleArray())
        } finally {
            m.close()
        }
    }

    @Test
    fun multiply2x2() {
        val a = Matrix(2, 2, doubleArrayOf(1.0, 2.0, 3.0, 4.0))
        val b = Matrix(2, 2, doubleArrayOf(5.0, 6.0, 7.0, 8.0))
        try {
            val c = a.multiply(b)
            try {
                assertEquals(2, c.rows)
                assertEquals(2, c.cols)
                assertContentEquals(doubleArrayOf(19.0, 22.0, 43.0, 50.0), c.toDoubleArray())
            } finally {
                c.close()
            }
        } finally {
            a.close()
            b.close()
        }
    }

    @Test
    fun invalidMultiplyShapesThrows() {
        val a = Matrix(2, 3, DoubleArray(6) { 1.0 })
        val b = Matrix(2, 2, DoubleArray(4) { 1.0 })
        try {
            assertFailsWith<IllegalArgumentException> {
                a.multiply(b)
            }
        } finally {
            a.close()
            b.close()
        }
    }

    @Test
    fun nullDataCreatesZeroMatrix() {
        val m = Matrix(3, 2, null)
        try {
            assertEquals(3, m.rows)
            assertEquals(2, m.cols)
            assertContentEquals(DoubleArray(6) { 0.0 }, m.toDoubleArray())
        } finally {
            m.close()
        }
    }
}