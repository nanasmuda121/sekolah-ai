# 🍍 NanasAi — Belajar Tanpa Menunggu Wi-Fi.

> **Teman Belajar, Bahkan Saat Offline.**  
> Diciptakan oleh **Adnan Ferdiansyah**

NanasAi adalah asisten belajar yang diciptakan oleh Adnan Ferdiansyah dengan tujuan membantu pelajar mendapatkan bantuan belajar tanpa harus selalu bergantung pada koneksi internet.

Dengan konsep AI yang siap membantu walau offline, NanasAi dirancang sebagai teman belajar yang tetap dapat digunakan ketika Wi-Fi mati, kuota habis, atau koneksi internet sedang tidak tersedia.

---

## 🎯 Tujuan NanasAi
Membantu pelajar belajar dengan lebih mudah, cepat, dan mandiri—tanpa terkendala masalah koneksi internet.

## 💡 Filosofi Nama
* **Nanas** melambangkan sesuatu yang sederhana, dekat, dan mudah dikenali.
* **Ai** berarti Artificial Intelligence, teknologi yang digunakan untuk membantu proses belajar.
* **Gagasan Utama:** *“Belajar tidak harus menunggu Wi-Fi.”*

## 👨‍💻 Diciptakan oleh
**Adnan Ferdiansyah**  
Seorang pengembang yang memiliki gagasan untuk menghadirkan teknologi pembelajaran yang dapat tetap bermanfaat bagi pelajar, termasuk ketika akses internet terbatas.

## 🚀 Visi & 🤝 Misi
* **Visi:** Mewujudkan asisten belajar yang dapat diakses pelajar kapan saja dan di mana saja, termasuk dalam kondisi tanpa koneksi internet.
* **Misi:**
  - Membantu pelajar memahami materi dengan lebih mudah.
  - Menyediakan bantuan belajar yang praktis.
  - Mengurangi ketergantungan terhadap koneksi internet.
  - Mendorong pelajar untuk belajar secara mandiri.
  - Mengembangkan teknologi AI yang lebih mudah diakses.

---

## ✨ Fitur Unggulan

1. **AI Chatbot Terpadu (Tanpa Pemisah):**
   - Siswa bisa bertanya apa saja secara langsung layaknya chatbot cerdas (Matematika, IPA, Sejarah, Bahasa Inggris, PPKn, Agama, dll).
2. **Kamera & Vision Soal Matematika (Pythagoras & Aljabar):**
   - Siswa cukup memotret soal lembar kerja atau diagram segitiga siku-siku di buku.
   - NanasAi mendeteksi angka dan menyajikan rumus serta langkah perhitungan secara bertahap hingga kesimpulan akhir.
3. **PromptBar Micro-Interaction (Inspirasi ReactBits):**
   - Tampilan input bar gelap elegan dengan sudut rounded.
   - Tombol kamera instan dan tombol kirim dinamis.
4. **Arsitektur Hemat RAM (Kotlin + C++ Native):**
   - Bebas dari overhead JavaScript engine.
   - Menggunakan memori hanya ~15–25 MB untuk UI, sehingga sisa RAM sangat lega untuk perangkat dengan **RAM 750 MB – 1 GB**.

---

## 🛠️ Arsitektur Teknologi

* **Bahasa:** Kotlin (Android Native) & C++17
* **Build System:** Gradle 8.3 & CMake 3.22 (Android NDK)
* **Vision / OCR:** Google ML Kit Text Recognition (On-Device Offline)
* **AI Core:** C++ Native Inference Engine (`nanas_ai.so`) + Model GGUF Kuantisasi Q4
* **CI/CD:** GitHub Actions otomatis meng-compile release APK
