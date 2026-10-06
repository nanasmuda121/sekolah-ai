package com.nanasai.nanas

import android.util.Log

object NanasAiNative {
    private const val TAG = "NanasAiNative"
    var isInitialized = false
        private set

    init {
        try {
            System.loadLibrary("nanas_ai")
            isInitialized = true
            Log.i(TAG, "Native library nanas_ai loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load nanas_ai library: ${e.message}")
        }
    }

    private external fun initModel(modelPath: String): Boolean
    private external fun generateResponse(prompt: String): String

    fun setup(modelPath: String = "models/qwen2.5-0.5b-q4.gguf"): Boolean {
        if (!isInitialized) return false
        return try {
            initModel(modelPath)
        } catch (e: Exception) {
            Log.e(TAG, "initModel error: ${e.message}")
            false
        }
    }

    fun ask(prompt: String): String {
        // Gunakan NanasAi Intelligent Offline Engine
        return NanasAiEngine.processQuery(prompt)
    }
}
