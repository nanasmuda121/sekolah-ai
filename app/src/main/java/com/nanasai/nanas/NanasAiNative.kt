package com.nanasai.nanas

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

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
            val modelFile = File(context.filesDir, "qwen2.5-0.5b-q4.gguf")
            if (!modelFile.exists() || modelFile.length() < 100000000L) {
                try {
                    Log.i(TAG, "Mengekstrak model GGUF dari assets...")
                    context.assets.open("models/qwen2.5-0.5b-q4.gguf").use { input ->
                        FileOutputStream(modelFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.i(TAG, "Model berhasil diekstrak ke: ${modelFile.absolutePath}")
                } catch (e: Exception) {
                    Log.w(TAG, "Asset model belum tersedia: ${e.message}")
                }
            }

            if (modelFile.exists() && modelFile.length() > 100000000L) {
                isModelLoaded = initModel(modelFile.absolutePath)
                Log.i(TAG, "Inisialisasi LLM model sukses: $isModelLoaded")
                return isModelLoaded
            }
        } catch (e: Exception) {
            Log.e(TAG, "Setup error: ${e.message}")
        }
        return false
    }

    fun ask(prompt: String): String {
        // 1. Coba inferensi LLM neural network asli (Qwen2.5-0.5B via llama.cpp)
        if (isInitialized && isModelLoaded) {
            try {
                val llmOutput = generateResponse(prompt)
                if (llmOutput.isNotBlank()) {
                    return llmOutput.trim()
                }
            } catch (e: Exception) {
                Log.e(TAG, "LLM generate error: ${e.message}")
            }
        }

        // 2. Fallback otomatis ke NanasAi Knowledge & Math Engine
        return NanasAiEngine.processQuery(prompt)
    }
}
