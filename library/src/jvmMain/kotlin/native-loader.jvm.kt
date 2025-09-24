package com.matmul.matrix

internal object NativeLoader {
    private var loaded = false
    fun load() {
        if(loaded) return
        System.loadLibrary("matrix_jni")
        loaded = true
    }
}