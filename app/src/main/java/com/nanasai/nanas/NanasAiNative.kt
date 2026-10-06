package com.nanasai.nanas

import android.content.Context
import android.util.Log

object NanasAiNative {
    private const val TAG = "NanasAiNative"
    var isInitialized = false
        private set
    var isModelLoaded = false
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

    fun setup(context: Context): Boolean {
        if (!isInitialized) return false
        try {
            isModelLoaded = initModel("internal")
            Log.i(TAG, "NanasAi Native Engine ready: $isModelLoaded")
            return isModelLoaded
        } catch (e: Exception) {
            Log.e(TAG, "Setup error: ${e.message}")
        }
        return false
    }

    fun ask(prompt: String): String {
        if (isInitialized && isModelLoaded) {
            try {
                val nativeOutput = generateResponse(prompt)
                if (nativeOutput.isNotBlank()) {
                    return nativeOutput.trim()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Native generate error: ${e.message}")
            }
        }

        // Jalankan NanasAi Dynamic Conversational & Knowledge Engine
        return NanasAiEngine.processQuery(prompt)
    }
}
