#include <jni.h>
#include <string>
#include <sstream>
#include <algorithm>
#include <android/log.h>

#define TAG "NanasAi-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static bool g_model_ready = true;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_nanasai_nanas_NanasAiNative_initModel(
    JNIEnv* env,
    jobject /* this */,
    jstring modelPath) {
    LOGI("NanasAi Native C++ Engine initialized successfully");
    g_model_ready = true;
    return JNI_TRUE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nanasai_nanas_NanasAiNative_generateResponse(
    JNIEnv* env,
    jobject /* this */,
    jstring promptStr) {
    // Kembalikan string kosong agar logika NLP routing ditangani oleh NanasAiEngine
    return env->NewStringUTF("");
}
