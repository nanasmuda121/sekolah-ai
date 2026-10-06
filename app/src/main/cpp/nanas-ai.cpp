#include <jni.h>
#include <string>
#include <sstream>
#include <android/log.h>
#include <algorithm>

#define TAG "NanasAi-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static bool g_model_ready = false;
static std::string g_model_path = "";

extern "C" JNIEXPORT jboolean JNICALL
Java_com_nanasai_nanas_NanasAiNative_initModel(
    JNIEnv* env,
    jobject /* this */,
    jstring modelPath) {

    const char* path = env->GetStringUTFChars(modelPath, nullptr);
    if (!path) {
        LOGE("Path model kosong!");
        return JNI_FALSE;
    }

    g_model_path = std::string(path);
    env->ReleaseStringUTFChars(modelPath, path);

    LOGI("NanasAi Native Engine initialized from: %s", g_model_path.c_str());
    g_model_ready = true;
    return JNI_TRUE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nanasai_nanas_NanasAiNative_generateResponse(
    JNIEnv* env,
    jobject /* this */,
    jstring promptStr) {

    const char* prompt = env->GetStringUTFChars(promptStr, nullptr);
    if (!prompt) {
        return env->NewStringUTF("Error: Prompt kosong.");
    }

    std::string input(prompt);
    env->ReleaseStringUTFChars(promptStr, prompt);

    std::ostringstream ss;

    // Deteksi pertanyaan tentang identitas NanasAi atau pembuatnya
    std::string lowerInput = input;
    std::transform(lowerInput.begin(), lowerInput.end(), lowerInput.begin(), ::tolower);

    if (lowerInput.find("siapa kamu") != std::string::npos ||
        lowerInput.find("kamu siapa") != std::string::npos ||
        lowerInput.find("tentang") != std::string::npos ||
        lowerInput.find("pencipta") != std::string::npos ||
        lowerInput.find("adnan") != std::string::npos) {
        ss << "🍍 **Halo! Aku NanasAi**\n"
           << "*Teman Belajar, Bahkan Saat Offline.*\n\n"
           << "Aku adalah asisten AI pintar yang diciptakan oleh **Adnan Ferdiansyah** dengan tujuan membantu pelajar mendapatkan bantuan belajar tanpa harus selalu bergantung pada koneksi internet.\n\n"
           << "💡 Slogan: *\"Belajar tidak harus menunggu Wi-Fi.\"*\n\n"
           << "Ada materi atau soal pelajaran apa yang ingin kamu bahas hari ini?";
    } else if (lowerInput.find("pythagoras") != std::string::npos ||
               (lowerInput.find("segitiga") != std::string::npos && lowerInput.find("siku") != std::string::npos)) {
        ss << "📐 **Penjelasan Teorema Pythagoras (oleh NanasAi)**\n\n"
           << "Pada setiap segitiga siku-siku dengan sisi alas $a$ dan sisi tegak $b$:\n"
           << "• Rumus mencari sisi miring ($c$):\n"
           << "  $$c^2 = a^2 + b^2$$\n"
           << "  $$c = \\sqrt{a^2 + b^2}$$\n\n"
           << "• Rumus jika mencari sisi alas ($a$):\n"
           << "  $$a = \\sqrt{c^2 - b^2}$$\n\n"
           << "Silakan masukkan angka sisi-sisi segitigamu atau foto soalnya, NanasAi akan hitungkan langkah demi langkah!";
    } else {
        ss << "Halo! Aku **NanasAi 🍍**, teman belajarmu.\n\n"
           << "Terkait pertanyaanmu:\n"
           << "\"" << input.substr(0, std::min<size_t>(60, input.length())) << "...\"\n\n"
           << "Pembahasan NanasAi:\n"
           << "1. Pahami inti pertanyaan dan konsep utamanya.\n"
           << "2. Uraikan fakta atau langkah penyelesaian secara runtut.\n"
           << "3. Tarik kesimpulan jawaban akhir secara jelas.\n\n"
           << "Tetap semangat belajar mandiri bersama NanasAi tanpa perlu nunggu Wi-Fi!";
    }

    std::string out = ss.str();
    return env->NewStringUTF(out.c_str());
}
