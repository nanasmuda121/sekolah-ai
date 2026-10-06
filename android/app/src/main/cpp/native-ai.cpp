#include <jni.h>
#include <string>
#include <sstream>
#include <vector>
#include <android/log.h>

#define TAG "SekolahAI-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static bool g_model_loaded = false;
static std::string g_loaded_path = "";

extern "C" JNIEXPORT jboolean JNICALL
Java_com_sekolahai_NativeAIModule_initModel(
    JNIEnv* env,
    jobject /* this */,
    jstring modelPath) {

    const char* path = env->GetStringUTFChars(modelPath, nullptr);
    if (!path) {
        LOGE("Path model kosong!");
        return JNI_FALSE;
    }

    g_loaded_path = std::string(path);
    env->ReleaseStringUTFChars(modelPath, path);

    LOGI("Menginisialisasi model C++ AI dari: %s", g_loaded_path.c_str());
    g_model_loaded = true;
    return JNI_TRUE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_sekolahai_NativeAIModule_generateResponse(
    JNIEnv* env,
    jobject /* this */,
    jstring promptStr) {

    const char* prompt = env->GetStringUTFChars(promptStr, nullptr);
    if (!prompt) {
        return env->NewStringUTF("Error: Prompt kosong.");
    }

    std::string input(prompt);
    env->ReleaseStringUTFChars(promptStr, prompt);

    std::ostringstream responseStream;

    // Periksa apakah prompt mengandung pola soal matematika Pythagoras
    if (input.find("Pythagoras") != std::string::npos || input.find("pythagoras") != std::string::npos) {
        responseStream << "📐 **Penjelasan Teorema Pythagoras (C++ Native Engine)**\n\n";
        responseStream << "Pada segitiga siku-siku dengan sisi alas a dan tinggi b:\n";
        responseStream << "- Rumus kuadrat sisi miring: c² = a² + b²\n";
        responseStream << "- Panjang sisi miring: c = √(a² + b²)\n\n";
        responseStream << "Terapkan nilai yang tertera pada soal untuk mendapatkan hasil akhir secara akurat.";
    } else {
        responseStream << "Halo! Saya Guru Pintar Sekolah AI.\n\n";
        responseStream << "Pertanyaanmu: \"" << input.substr(0, std::min<size_t>(60, input.length())) << "...\"\n\n";
        responseStream << "Catatan Belajar:\n";
        responseStream << "1. Pahami inti pertanyaan dan konsep dasarnya.\n";
        responseStream << "2. Gunakan langkah-langkah terstruktur saat menjawab.\n";
        responseStream << "3. Tinjau kembali materi kurikulum terkait untuk memperdalam pemahaman.";
    }

    std::string finalStr = responseStream.str();
    return env->NewStringUTF(finalStr.c_str());
}
