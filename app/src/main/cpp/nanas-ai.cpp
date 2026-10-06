#include <jni.h>
#include <string>
#include <vector>
#include <sstream>
#include <algorithm>
#include <android/log.h>

#include "llama.h"

#define TAG "NanasAi-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static llama_model* g_model = nullptr;
static llama_context* g_ctx = nullptr;
static llama_sampler* g_smpl = nullptr;
static bool g_is_model_loaded = false;

static void batch_add(struct llama_batch & batch, llama_token id, llama_pos pos, const std::vector<llama_seq_id> & seq_ids, bool logits) {
    batch.token   [batch.n_tokens] = id;
    batch.pos     [batch.n_tokens] = pos;
    batch.n_seq_id[batch.n_tokens] = seq_ids.size();
    for (size_t i = 0; i < seq_ids.size(); ++i) {
        batch.seq_id[batch.n_tokens][i] = seq_ids[i];
    }
    batch.logits  [batch.n_tokens] = logits;
    batch.n_tokens++;
}

static void batch_clear(struct llama_batch & batch) {
    batch.n_tokens = 0;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_nanasai_nanas_NanasAiNative_initModel(
    JNIEnv* env,
    jobject /* this */,
    jstring modelPath) {

    if (g_is_model_loaded && g_model != nullptr && g_ctx != nullptr) {
        return JNI_TRUE;
    }

    const char* path = env->GetStringUTFChars(modelPath, nullptr);
    if (!path) {
        LOGE("Model path is null!");
        return JNI_FALSE;
    }

    std::string model_file_path(path);
    env->ReleaseStringUTFChars(modelPath, path);

    LOGI("Memulai inisialisasi llama.cpp dari file: %s", model_file_path.c_str());

    llama_backend_init();

    llama_model_params model_params = llama_model_default_params();
    model_params.n_gpu_layers = 0; // CPU inference murni untuk kestabilan di Android

    g_model = llama_model_load_from_file(model_file_path.c_str(), model_params);
    if (!g_model) {
        LOGE("Gagal memuat llama_model dari file: %s", model_file_path.c_str());
        return JNI_FALSE;
    }

    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = 1024;     // Context window 1024 tokens
    ctx_params.n_batch = 256;
    ctx_params.n_threads = 4;    // 4 CPU threads
    ctx_params.no_perf = true;

    g_ctx = llama_init_from_model(g_model, ctx_params);
    if (!g_ctx) {
        LOGE("Gagal membuat llama_context!");
        llama_model_free(g_model);
        g_model = nullptr;
        return JNI_FALSE;
    }

    auto sparams = llama_sampler_chain_default_params();
    g_smpl = llama_sampler_chain_init(sparams);
    llama_sampler_chain_add(g_smpl, llama_sampler_init_temp(0.7f));
    llama_sampler_chain_add(g_smpl, llama_sampler_init_dist(42));

    g_is_model_loaded = true;
    LOGI("Berhasil menginisialisasi NanasAi LLM Engine!");
    return JNI_TRUE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nanasai_nanas_NanasAiNative_generateResponse(
    JNIEnv* env,
    jobject /* this */,
    jstring promptStr) {

    if (!g_is_model_loaded || !g_model || !g_ctx || !g_smpl) {
        return env->NewStringUTF("");
    }

    const char* prompt_c = env->GetStringUTFChars(promptStr, nullptr);
    if (!prompt_c) {
        return env->NewStringUTF("");
    }

    std::string user_prompt(prompt_c);
    env->ReleaseStringUTFChars(promptStr, prompt_c);

    const llama_vocab* vocab = llama_model_get_vocab(g_model);

    // Format prompt sesuai ChatML standar Qwen2.5
    std::string formatted_prompt = "<|im_start|>system\n"
                                   "Kamu adalah NanasAi, asisten AI pintar dan teman belajar offline buatan Adnan Ferdiansyah. Kamu ramah, cerdas, dan siap menjawab berbagai pertanyaan secara jelas dan terstruktur.<|im_end|>\n"
                                   "<|im_start|>user\n" + user_prompt + "<|im_end|>\n"
                                   "<|im_start|>assistant\n";

    int n_prompt = -llama_tokenize(vocab, formatted_prompt.c_str(), formatted_prompt.size(), NULL, 0, true, true);
    if (n_prompt <= 0) {
        return env->NewStringUTF("");
    }

    std::vector<llama_token> prompt_tokens(n_prompt);
    if (llama_tokenize(vocab, formatted_prompt.c_str(), formatted_prompt.size(), prompt_tokens.data(), prompt_tokens.size(), true, true) < 0) {
        return env->NewStringUTF("");
    }

    // Siapkan batch
    llama_batch batch = llama_batch_init(512, 0, 1);
    for (int i = 0; i < n_prompt; ++i) {
        batch_add(batch, prompt_tokens[i], i, { 0 }, (i == n_prompt - 1));
    }

    if (llama_decode(g_ctx, batch) != 0) {
        llama_batch_free(batch);
        return env->NewStringUTF("");
    }

    std::string response_text = "";
    int n_predict = 250; // Maksimal 250 token untuk respon yang lengkap dan cepat
    int n_cur = n_prompt;

    for (int i = 0; i < n_predict; ++i) {
        llama_token new_token_id = llama_sampler_sample(g_smpl, g_ctx, -1);

        if (llama_vocab_is_eog(vocab, new_token_id)) {
            break;
        }

        char piece_buf[128];
        int n_piece = llama_token_to_piece(vocab, new_token_id, piece_buf, sizeof(piece_buf), 0, true);
        if (n_piece > 0) {
            response_text.append(piece_buf, n_piece);
        }

        batch_clear(batch);
        batch_add(batch, new_token_id, n_cur++, { 0 }, true);

        if (llama_decode(g_ctx, batch) != 0) {
            break;
        }
    }

    llama_batch_free(batch);
    return env->NewStringUTF(response_text.c_str());
}
