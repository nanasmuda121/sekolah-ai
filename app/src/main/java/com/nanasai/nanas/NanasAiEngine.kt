package com.nanasai.nanas

import java.util.Locale

object NanasAiEngine {

    fun processQuery(rawQuery: String): String {
        val query = rawQuery.trim()
        val lower = query.lowercase(Locale.ROOT)

        // 0. Penanganan Khusus Foto / OCR Soal
        if (query.contains("[Teks dari Foto Soal]:")) {
            return processOcrProblem(query, lower)
        }

        // 1. Sapaan & Obrolan Santai (Chit-Chat)
        val chitChatAns = matchChitChat(lower)
        if (chitChatAns != null) return chitChatAns

        // 2. Input Angka Tunggal (Misal user kirim "1", "2", "no 1")
        val numberAns = matchSingleNumber(lower)
        if (numberAns != null) return numberAns

        // 3. Identitas NanasAi (HANYA jika ditanya langsung)
        if (isIdentityQuestion(lower)) {
            return getIdentityResponse()
        }

        // 4. Kalkulator / Operasi Matematika Langsung (Contoh: "5 + 7", "12 * 8", "100 / 4")
        val quickCalcAns = solveDirectArithmetic(lower)
        if (quickCalcAns != null) return quickCalcAns

        // 5. Matematika: Teorema Pythagoras
        if (lower.contains("pythagoras") || (lower.contains("segitiga") && (lower.contains("siku") || lower.contains("miring")))) {
            val pythagorasAns = solvePythagoras(query, lower)
            if (pythagorasAns != null) return pythagorasAns
        }

        // 6. Matematika: Rumus & Bangun Datar
        val mathAns = solveGeneralMath(lower, query)
        if (mathAns != null) return mathAns

        // 7. Sejarah Indonesia & Dunia
        val sejarahAns = matchSejarah(lower)
        if (sejarahAns != null) return sejarahAns

        // 8. Bahasa Inggris (Grammar & Tenses)
        val englishAns = matchEnglish(lower)
        if (englishAns != null) return englishAns

        // 9. IPA & Sains (Fisika, Biologi, Kimia, Astronomi)
        val scienceAns = matchScience(lower)
        if (scienceAns != null) return scienceAns

        // 10. PPKn & Kewarganegaraan
        val ppknAns = matchPPKn(lower)
        if (ppknAns != null) return ppknAns

        // 11. Agama & Budi Pekerti
        val agamaAns = matchAgama(lower)
        if (agamaAns != null) return agamaAns

        // 12. Smart AI Conversational Fallback
        return generateConversationalAnswer(query, lower)
    }

    private fun matchChitChat(lower: String): String? {
        val clean = lower.replace(Regex("""[?!.,]"""), "").trim()
        
        if (clean in listOf("halo", "hai", "hello", "hi", "hey", "hei", "p", "ping", "test", "tes")) {
            return "Halo! Ada yang bisa NanasAi bantu untuk tugas atau pelajaranmu hari ini? Mau tanya matematika, sejarah, bahasa Inggris, atau foto soal dari bukum? 😊🍍"
        }
        if (clean in listOf("selamat pagi", "pagi", "pagi nanas")) {
            return "Selamat pagi! Semangat belajar hari ini! Ada materi pelajaran atau PR yang ingin kita selesaikan bersama? 🍍☀️"
        }
        if (clean in listOf("selamat siang", "siang")) {
            return "Selamat siang! Tetap fokus ya. Mau bahas soal atau materi apa sekarang? 🍍"
        }
        if (clean in listOf("selamat malam", "malam")) {
            return "Selamat malam! Masih ada tugas sekolah yang perlu dibahas sebelum istirahat? Tuliskan soalnya ya. 🍍🌙"
        }
        if (clean.contains("assalamualaikum") || clean == "samlikum") {
            return "Wa'alaikumsalam warahmatullahi wabarakatuh! Selamat datang di NanasAi. Ada soal atau materi yang ingin dipelajari bersama? 🍍"
        }
        if (clean in listOf("apa kabar", "gimana kabarnya", "kabar baik")) {
            return "Kabar baik dan selalu siap membantumu belajar kapan saja tanpa perlu nunggu Wi-Fi! Kamu lagi belajar materi apa hari ini? 🍍"
        }
        if (clean in listOf("makasih", "terima kasih", "thanks", "terimakasih", "thank you", "matur nuwun")) {
            return "Sama-sama! Senang bisa membantumu memahami materi ini. Kalau ada soal lain yang membingungkan, langsung tanyakan lagi ya! Semangat belajarnya! 🍍✨"
        }
        if (clean in listOf("ok", "oke", "siap", "baik", "mantap", "sip", "keren")) {
            return "Siap! Jika ada soal lain yang ingin dihitung atau dibahas, tinggal kirim teks atau fotokan lembar soalnya ya. 🍍"
        }
        return null
    }

    private fun matchSingleNumber(lower: String): String? {
        val trimmed = lower.replace(Regex("""[#.:]"""), "").trim()
        val isNumberOnly = trimmed.all { it.isDigit() }
        val isNoPrefix = trimmed.startsWith("no ") || trimmed.startsWith("nomor ") || trimmed.startsWith("soal ")

        if (isNumberOnly || isNoPrefix) {
            val num = trimmed.filter { it.isDigit() }.ifEmpty { trimmed }
            return """
Halo! Kamu mau bahas soal nomor $num ya? 🍍

Silakan ketikkan teks lengkap soalnya atau tekan tombol kamera 📷 di samping untuk memotret soal langsung dari buku paket/lembar ujianmu. NanasAi akan bantu jabarkan rumus dan langkah penyelesaiannya sampai tuntas!
            """.trimIndent()
        }
        return null
    }

    private fun isIdentityQuestion(lower: String): Boolean {
        val clean = lower.replace(Regex("""[?!.,]"""), "").trim()
        return clean in listOf(
            "kamu siapa", "siapa kamu", "siapa namamu", "kamu apa", 
            "siapa penciptamu", "siapa pembuatmu", "siapa yang buat kamu",
            "tentang kamu", "profil nanasai", "siapa adnan", "siapa adnan ferdiansyah"
        ) || clean.startsWith("siapa kamu") || clean.startsWith("kamu siapa")
    }

    private fun getIdentityResponse(): String {
        return """
🍍 **Halo! Aku NanasAi**
*Teman Belajar, Bahkan Saat Offline.*

Aku adalah asisten AI pintar yang diciptakan oleh **Adnan Ferdiansyah** untuk membantu siswa belajar mandiri tanpa harus selalu bergantung pada kuota internet atau Wi-Fi.

💡 Slogan: *"Belajar tidak harus menunggu Wi-Fi."*

Aku siap membantumu memahami:
• **Matematika:** Aljabar, Pythagoras, Geometri, Aritmatika.
• **Sains (IPA):** Fisika, Kimia, Biologi, Tata Surya.
• **Bahasa Inggris:** Grammar, Tenses, Vocabulary.
• **Sosial:** Sejarah Indonesia, PPKn, Nilai Pancasila.
• **Foto Soal (OCR):** Cukup foto soal ujianmu!

Ada topik apa yang ingin kita bahas sekarang?
        """.trimIndent()
    }

    private fun solveDirectArithmetic(lower: String): String? {
        val clean = lower.replace("x", "*").replace(":", "/").replace("kali", "*").replace("bagi", "/").replace("tambah", "+").replace("kurang", "-").trim()
        
        // Pola: a + b, a - b, a * b, a / b
        val match = Regex("""^(-?\d+(?:\.\d+)?)\s*([\+\-\*\/])\s*(-?\d+(?:\.\d+)?)$""").find(clean)
        if (match != null) {
            val a = match.groupValues[1].toDoubleOrNull() ?: return null
            val op = match.groupValues[2]
            val b = match.groupValues[3].toDoubleOrNull() ?: return null

            val result = when (op) {
                "+" -> a + b
                "-" -> a - b
                "*" -> a * b
                "/" -> if (b != 0.0) a / b else null
                else -> null
            } ?: return "Operasi pembagian dengan angka nol tidak terdefinisi dalam matematika."

            val formatRes = if (result % 1.0 == 0.0) result.toLong().toString() else String.format(Locale.US, "%.2f", result)
            return """
🔢 **Hasil Perhitungan:**
$clean = **$formatRes**

Langkah:
• $a $op $b = $formatRes
            """.trimIndent()
        }

        // Akar kuadrat: "akar 64" atau "akar dari 100"
        if (lower.startsWith("akar ") || lower.contains("akar dari")) {
            val num = Regex("""\d+(?:\.\d+)?""").find(lower)?.value?.toDoubleOrNull()
            if (num != null && num >= 0) {
                val res = Math.sqrt(num)
                val formatRes = if (res % 1.0 == 0.0) res.toLong().toString() else String.format(Locale.US, "%.2f", res)
                return """
🔢 **Perhitungan Akar Kuadrat:**
√$num = **$formatRes**

Karena $formatRes × $formatRes = $num.
                """.trimIndent()
            }
        }

        return null
    }

    private fun solvePythagoras(query: String, lower: String): String? {
        val numbers = Regex("""\d+(?:[.,]\d+)?""").findAll(query)
            .map { it.value.replace(',', '.').toDoubleOrNull() }
            .filterNotNull()
            .toList()

        if (numbers.size >= 2) {
            val a = numbers[0]
            val b = numbers[1]
            val c = Math.sqrt(a * a + b * b)
            val formatC = if (c % 1.0 == 0.0) c.toLong().toString() else String.format(Locale.US, "%.2f", c)

            return """
📐 **Penyelesaian Teorema Pythagoras**

1. **Diketahui:**
   • Sisi alas (a) = $a
   • Sisi tegak (b) = $b
   • Ditanyakan: Sisi miring (c)

2. **Rumus Pythagoras:**
   c² = a² + b²
   c = √(a² + b²)

3. **Langkah Pengerjaan:**
   • c = √(${a}² + ${b}²)
   • c = √(${a * a} + ${b * b})
   • c = √(${a * a + b * b})
   • c = $formatC

4. **Jawaban Akhir:**
   Panjang sisi miring segitiga siku-siku tersebut adalah **$formatC**.
            """.trimIndent()
        }

        return """
📐 **Konsep Teorema Pythagoras**

Teorema Pythagoras berlaku khusus untuk **segitiga siku-siku**:
• **Mencari Sisi Miring (c):**
  c = √(a² + b²)
• **Mencari Sisi Alas (a):**
  a = √(c² - b²)
• **Mencari Sisi Tegak (b):**
  b = √(c² - a²)

💡 *Tips:* Sebutkan kedua panjang sisinya (contoh: "alas 3 tinggi 4") atau foto langsung soalnya ya!
        """.trimIndent()
    }

    private fun solveGeneralMath(lower: String, query: String): String? {
        if (lower.contains("lingkaran") && (lower.contains("luas") || lower.contains("keliling"))) {
            val r = Regex("""\d+(?:[.,]\d+)?""").find(query)?.value?.replace(',', '.')?.toDoubleOrNull() ?: 7.0
            val luas = Math.PI * r * r
            val keliling = 2 * Math.PI * r
            val formatLuas = String.format(Locale.US, "%.2f", luas)
            val formatKel = String.format(Locale.US, "%.2f", keliling)

            return """
📐 **Perhitungan Lingkaran (Jari-jari r = $r)**

1. **Rumus:**
   • Luas = π × r²
   • Keliling = 2 × π × r

2. **Hasil Perhitungan:**
   • **Luas:** $formatLuas
   • **Keliling:** $formatKel
            """.trimIndent()
        }

        if (lower.contains("kecepatan") || lower.contains("jarak tempuh")) {
            return """
🚗 **Rumus Kecepatan, Jarak, dan Waktu**

• **Jarak (s):** s = v × t
• **Kecepatan (v):** v = s / t
• **Waktu (t):** t = s / v

*Keterangan:*
- s = Jarak (meter atau km)
- v = Kecepatan (m/s atau km/jam)
- t = Waktu (detik atau jam)
            """.trimIndent()
        }

        return null
    }

    private fun matchSejarah(lower: String): String? {
        if (lower.contains("rengasdengklok")) {
            return """
🏛️ **Peristiwa Rengasdengklok (16 Agustus 1945)**

1. **Latar Belakang:**
   Perbedaan pendapat antara golongan muda (Sukarni, Chaerul Saleh, Wikana) dan golongan tua (Ir. Soekarno & Drs. Moh. Hatta) terkait percepatan proklamasi setelah Jepang menyerah kepada Sekutu.

2. **Peristiwa:**
   Golongan muda membawa Soekarno-Hatta ke Rengasdengklok (Karawang) agar terhindar dari tekanan dan pengaruh Jepang.

3. **Hasil Kesepakatan:**
   Achmad Soebardjo menjamin kemerdekaan akan diproklamasikan pada 17 Agustus 1945 di Jakarta.
            """.trimIndent()
        }

        if (lower.contains("proklamasi") || lower.contains("17 agustus")) {
            return """
🇮🇩 **Proklamasi Kemerdekaan Indonesia (17 Agustus 1945)**

• **Waktu & Tempat:** Jumat, 17 Agustus 1945 pukul 10.00 WIB di Jl. Pegangsaan Timur No. 56, Jakarta.
• **Pembaca Naskah:** Ir. Soekarno didampingi Drs. Moh. Hatta.
• **Pengetik Naskah:** Sayuti Melik.
• **Pengibar Bendera:** Latief Hendraningrat dan Suhud Sastro Kusumo.
• **Penjahit Bendera Merah Putih:** Ibu Fatmawati Soekarno.
            """.trimIndent()
        }

        if (lower.contains("diponegoro")) {
            return """
🏛️ **Perang Diponegoro (1825 – 1830)**

1. **Penyebab Utama:**
   Pemasangan patok jalan oleh Belanda yang melintasi makam leluhur Pangeran Diponegoro di Tegalrejo tanpa izin, serta intervensi kolonial dalam keraton Yogyakarta.

2. **Taktik Perang:**
   • Pangeran Diponegoro: Perang Gerilya di seluruh Jawa Tengah & Jawa Timur.
   • Belanda: Strategi Benteng Stelsel untuk mempersempit ruang gerak.

3. **Akhir Perang:**
   Diponegoro dijebak dalam perundingan damai di Magelang (1830), lalu diasingkan ke Makassar hingga wafat pada 1855.
            """.trimIndent()
        }

        return null
    }

    private fun matchEnglish(lower: String): String? {
        if (lower.contains("present tense") || lower.contains("past tense") || lower.contains("tenses")) {
            return """
🇬🇧 **Perbedaan Simple Present Tense vs Simple Past Tense**

1. **Simple Present Tense (Kebiasaan / Fakta):**
   • **Pola:** Subject + Verb 1 (s/es) + Object
   • **Time Signals:** always, usually, every day.
   • **Contoh:** "I study English every day."

2. **Simple Past Tense (Kejadian Masa Lampau):**
   • **Pola:** Subject + Verb 2 + Object
   • **Time Signals:** yesterday, last night, two days ago.
   • **Contoh:** "I studied English yesterday."
            """.trimIndent()
        }

        if (lower.contains("passive voice")) {
            return """
🇬🇧 **Passive Voice (Kalimat Pasif)**

Rumus Dasar Kalimat Pasif:
Subject + To Be + Verb 3 (Past Participle)

**Contoh:**
• Active: "Adnan writes a letter."
• Passive: "A letter is written by Adnan."
            """.trimIndent()
        }

        return null
    }

    private fun matchScience(lower: String): String? {
        if (lower.contains("fotosintesis")) {
            return """
🌿 **Proses Fotosintesis pada Tumbuhan**

1. **Pengertian:**
   Pembuatan makanan oleh tumbuhan hijau berdaun klorofil dengan bantuan energi cahaya matahari.

2. **Persamaan Reaksi:**
   6CO₂ + 6H₂O + Cahaya Matahari → C₆H₁₂O₆ (Glukosa) + 6O₂ (Oksigen)

3. **Faktor Penentu:**
   Cahaya matahari, air (H₂O), gas karbon dioksida (CO₂), dan zat hijau daun (klorofil).
            """.trimIndent()
        }

        if (lower.contains("newton")) {
            return """
🍎 **Hukum Gerak Newton**

1. **Hukum I Newton (Kelembaman/Inersia):**
   Benda mempertahankan keadaan diam atau bergeraknya jika resultan gaya sama dengan nol (ΣF = 0).

2. **Hukum II Newton:**
   Percepatan sebanding dengan gaya dan berbanding terbalik dengan massa:
   F = m × a

3. **Hukum III Newton (Aksi - Reaksi):**
   F_aksi = -F_reaksi (Besarnya sama, arahnya berlawanan).
            """.trimIndent()
        }

        if (lower.contains("tata surya") || lower.contains("planet")) {
            return """
🪐 **Urutan Planet dalam Tata Surya (dari terdekat Matahari):**
1. Merkurius (terkecil & terdekat)
2. Venus (terpanas)
3. Bumi (tempat kehidupan)
4. Mars (planet merah)
5. Jupiter (planet terbesar)
6. Saturnus (memiliki cincin indah)
7. Uranus (planet es berotasi rebah)
8. Neptunus (terjauh dan terdingin)
            """.trimIndent()
        }

        return null
    }

    private fun matchPPKn(lower: String): String? {
        if (lower.contains("pancasila") || lower.contains("sila")) {
            return """
🇮🇩 **5 Sila Pancasila dan Nilai Luhurnya:**

1. **Ketuhanan Yang Maha Esa:** Keyakinan pada Tuhan dan toleransi beragama.
2. **Kemanusiaan yang Adil dan Beradab:** Menghargai hak asasi dan persamaan derajat.
3. **Persatuan Indonesia:** Cinta tanah air dan persatuan di atas golongan.
4. **Kerakyatan yang Dipimpin oleh Hikmat Kebijaksanaan dalam Permusyawaratan/Perwakilan:** Demokrasi dan musyawarah untuk mufakat.
5. **Keadilan Sosial bagi Seluruh Rakyat Indonesia:** Gotong royong dan keadilan hidup.
            """.trimIndent()
        }

        if (lower.contains("uud 1945") || lower.contains("pasal")) {
            return """
🇮🇩 **Pasal Penting UUD 1945:**
• **Pasal 27 ayat 1:** Persamaan kedudukan di depan hukum.
• **Pasal 28:** Hak berserikat, berkumpul, dan mengeluarkan pendapat.
• **Pasal 29 ayat 2:** Kemerdekaan memeluk agama dan beribadah.
• **Pasal 31 ayat 1:** Setiap warga negara berhak mendapat pendidikan.
• **Pasal 33 ayat 3:** Bumi, air, dan kekayaan alam dikuasai negara untuk kemakmuran rakyat.
            """.trimIndent()
        }

        return null
    }

    private fun matchAgama(lower: String): String? {
        if (lower.contains("rukun iman") || lower.contains("rukun islam")) {
            return """
📖 **Rukun Iman dan Rukun Islam**

**6 Rukun Iman:**
1. Iman kepada Allah SWT
2. Iman kepada Malaikat Allah
3. Iman kepada Kitab-kitab Allah
4. Iman kepada Rasul-rasul Allah
5. Iman kepada Hari Akhir (Kiamat)
6. Iman kepada Qada dan Qadar

**5 Rukun Islam:**
1. Syahadat
2. Sholat 5 waktu
3. Zakat
4. Puasa Ramadhan
5. Haji (bagi yang mampu)
            """.trimIndent()
        }
        return null
    }

    private fun processOcrProblem(fullQuery: String, lower: String): String {
        return """
📷 **Analisis Foto Soal oleh NanasAi:**

NanasAi telah membaca teks dari foto soal yang kamu lampirkan:

1. **Identifikasi Masalah:**
   Pahami pertanyaan utama yang tertera pada lembar soal.

2. **Langkah Penyelesaian:**
   • Catat variabel atau angka yang diketahui dari teks soal.
   • Gunakan rumus atau konsep terkait yang relevan.
   • Lakukan substitusi angka dan hitung secara teliti.

💡 *Tips:* Jika ada bagian rumus atau angka yang belum terbaca dengan sempurna karena foto miring/kurang terang, kamu bisa ketikkan langsung angka soalnya di kolom chat ya! 🍍
        """.trimIndent()
    }

    private fun generateConversationalAnswer(query: String, lower: String): String {
        return """
💡 **Tanggapan NanasAi untuk: "${query.take(50)}"**

Untuk memahami atau menyelesaikan materi ini:
1. Pahami konsep atau istilah intinya terlebih dahulu.
2. Identifikasi rumus atau fakta penting yang mendasarinya.
3. Coba terapkan contoh soal agar pemahamanmu semakin mantap.

Jika ini soal hitungan atau PR ujian, sebutkan angkanya secara detail atau kirimkan fotonya ya! 🍍
        """.trimIndent()
    }
}
