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
    ss << "Jawaban NanasAi untuk: " << input;

    std::string out = ss.str();
    return env->NewStringUTF(out.c_str());
}
