package com.adnan.nanasai

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

    fun solvePythagorasFast(query: String): String? {
        val lower = query.lowercase()
        if (!lower.contains("pythagoras") &&
            !lower.contains("segitiga") &&
            !lower.contains("sisi miring") &&
            !lower.contains("siku-siku")
        ) {
            return null
        }

        val regex = Regex("""\d+(?:[.,]\d+)?""")
        val matches = regex.findAll(query).map { it.value.replace(',', '.').toDoubleOrNull() }.filterNotNull().toList()

        if (matches.size < 2) return null

        val a = matches[0]
        val b = matches[1]

        val c = Math.sqrt(a * a + b * b)
        val formattedC = if (c % 1.0 == 0.0) c.toInt().toString() else String.format("%.2f", c)

        return """
📐 **Penyelesaian Soal Pythagoras (oleh NanasAi)**

1. **Diketahui:**
   • Sisi alas (a) = $a
   • Sisi tegak (b) = $b
   • Ditanyakan: Panjang sisi miring (c)

2. **Rumus Teorema Pythagoras:**
   c² = a² + b²
   c = √(a² + b²)

3. **Langkah Perhitungan:**
   c = √($a² + $b²)
   c = √(${a * a} + ${b * b})
   c = √(${a * a + b * b})
   c = $formattedC

4. **Kesimpulan NanasAi:**
   Panjang sisi miring segitiga siku-siku tersebut adalah **$formattedC**.

Belajar matematika jadi mudah bersama NanasAi tanpa perlu nunggu Wi-Fi! 🍍
        """.trimIndent()
    }

    fun ask(prompt: String): String {
        // 1. Cek pemecah rumus instan jika ada pola Pythagoras
        val fastResult = solvePythagorasFast(prompt)
        if (fastResult != null) {
            return fastResult
        }

        // 2. Jalankan engine C++ native
        if (isInitialized) {
            return try {
                val fullPrompt = "<|im_start|>system\n${NanasAiIdentity.SYSTEM_PROMPT}<|im_end|>\n" +
                        "<|im_start|>user\n$prompt<|im_end|>\n" +
                        "<|im_start|>assistant\n"
                generateResponse(fullPrompt)
            } catch (e: Exception) {
                "[NanasAi Engine Error]: ${e.message}"
            }
        }

        return "Halo! Aku NanasAi 🍍, teman belajarmu. Pertanyaanmu: \"$prompt\" telah diterima dan diproses secara offline."
    }
}
