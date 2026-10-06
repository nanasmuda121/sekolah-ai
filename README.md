# 🎓 Sekolah AI - Tutor Pelajaran Mandiri 100% Offline

Aplikasi Android edukasi mandiri yang dirancang khusus untuk berjalan lancar di HP dengan **RAM 1 GB ke bawah** (termasuk 750 MB RAM) secara **100% Offline** tanpa kuota internet.

---

## ✨ Fitur Utama

1. **AI Chat & Tutor Bebas:**
   - **Matematika (MTK):** Menyelesaikan rumus aljabar, geometri, dan Teorema Pythagoras secara bertahap (langkah demi langkah).
   - **Sejarah:** Menjelaskan kronologi peristiwa bersejarah Indonesia dan dunia, tokoh penting, dan latar belakang.
   - **Bahasa Inggris:** Melatih percakapan, menerjemahkan, menganalisis *grammar* & *tenses*.
   - **PPKn & Agama:** Menjawab konsep kewarganegaraan, Pancasila, UUD 1945, serta pendidikan budi pekerti.
2. **Vision & Kamera Soal (Mata):**
   - Siswa bisa memotret soal lembar kerja siswa (LKS), buku paket, atau tulisan soal di papan tulis.
   - Mesin OCR mengekstrak angka, rumus, dan pertanyaan secara otomatis.
3. **ReactBits Prompt-Bar (Micro Interaction):**
   - Komponen input bar modern terinspirasi dari [reactbits.dev](https://reactbits.dev/micro/prompt-bar).
   - Tombol kamera instan, selektor mata pelajaran (chips), dan animasi status kirim.
4. **Ringan & Aman di RAM 1 GB:**
   - Menggunakan bridge C++ native untuk inferensi hemat memori.
   - Menggunakan model `Qwen2.5-0.5B-Instruct` versi kuantisasi Q4.

---

## 🛠️ Arsitektur Teknologi

* **Frontend:** React Native 0.76 (TypeScript)
* **Native Core:** C++17 Android NDK (JNI Bridge)
* **Vision / OCR:** ML Kit Text Recognition
* **LLM Engine:** `llama.cpp` + `Qwen2.5-0.5B-Instruct-Q4_K_M.gguf`
* **CI/CD:** GitHub Actions (Build otomatis ke file APK & GitHub Releases)

---

## 🚀 Build Otomatis via GitHub Actions

Kamu tidak perlu meng-compile secara lokal di komputer yang berat. Cukup push repositori ini ke GitHub:

1. Buat repository baru di GitHub:
   ```bash
   gh repo create sekolah-ai --public --source=. --remote=origin --push
   ```
2. Workflow GitHub Actions di `.github/workflows/build-android.yml` akan berjalan otomatis:
   - Menyiapkan NDK & Java 17.
   - Mengunduh bobot model `Qwen2.5-0.5B` otomatis dari Hugging Face.
   - Meng-compile APK Release (`SekolahAI-Offline-Release-APK`).
3. Kamu bisa langsung mengunduh file `.apk` hasil build di tab **Actions** atau tab **Releases** di GitHub!
