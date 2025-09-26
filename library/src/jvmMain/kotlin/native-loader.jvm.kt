package com.matmul.matrix

internal object NativeLoader {
    private var loaded = false
    fun load() {
        if(loaded) return
        System.load("/home/user/projects/matmul-kt/backend/build/libmatrix_jni.so")
        loaded = true
    }
}