package com.nanasai.nanas

object NanasAiEngine {

    fun processQuery(rawQuery: String): String {
        val query = rawQuery.trim()
        val lower = query.lowercase()

        // 1. Identitas HANYA jika ditanya secara langsung
        if (isIdentityQuestion(lower)) {
            return getIdentityResponse()
        }

        // 2. Matematika: Teorema Pythagoras
        if (lower.contains("pythagoras") || (lower.contains("segitiga") && (lower.contains("siku") || lower.contains("miring")))) {
            val pythagorasAns = solvePythagoras(query, lower)
            if (pythagorasAns != null) return pythagorasAns
        }

        // 3. Matematika: Aljabar / Persamaan / Bangun Datar
        val mathAns = solveGeneralMath(lower, query)
        if (mathAns != null) return mathAns

        // 4. Sejarah Indonesia & Dunia
        val sejarahAns = matchSejarah(lower)
        if (sejarahAns != null) return sejarahAns

        // 5. Bahasa Inggris (Grammar & Tenses)
        val englishAns = matchEnglish(lower)
        if (englishAns != null) return englishAns

        // 6. PPKn & Kewarganegaraan
        val ppknAns = matchPPKn(lower)
        if (ppknAns != null) return ppknAns

        // 7. IPA & Sains (Fisika, Biologi, Kimia)
        val scienceAns = matchScience(lower)
        if (scienceAns != null) return scienceAns

        // 8. Agama & Budi Pekerti
        val agamaAns = matchAgama(lower)
        if (agamaAns != null) return agamaAns

        // 9. General Question Answering (Smart Fallback Tutor)
        return generateGeneralAnswer(query, lower)
    }

    private fun isIdentityQuestion(lower: String): Boolean {
        return lower == "kamu siapa" ||
                lower == "siapa kamu" ||
                lower == "siapa namamu" ||
                lower.startsWith("siapa kamu") ||
                lower.startsWith("kamu siapa") ||
                lower.contains("siapa penciptamu") ||
                lower.contains("siapa yang membuatmu") ||
                lower.contains("tentang aplikasi ini") ||
                lower.contains("profil nanasai")
    }

    private fun getIdentityResponse(): String {
        return """
🍍 **Halo! Aku NanasAi**
*Teman Belajar, Bahkan Saat Offline.*

Aku adalah asisten AI pintar yang diciptakan oleh **Adnan Ferdiansyah** dengan tujuan membantu pelajar mendapatkan bantuan belajar tanpa harus selalu bergantung pada koneksi internet.

💡 Slogan: *"Belajar tidak harus menunggu Wi-Fi."*

Aku siap membantumu belajar Matematika, Sejarah, Bahasa Inggris, IPA, PPKn, Agama, atau menyelesaikan soal dari foto bukum. Ada materi apa yang ingin kita bahas sekarang?
        """.trimIndent()
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
            val formatC = if (c % 1.0 == 0.0) c.toInt().toString() else String.format("%.2f", c)

            return """
📐 **Penyelesaian Teorema Pythagoras**

1. **Diketahui:**
   • Sisi pertama ($a$) = $a
   • Sisi kedua ($b$) = $b
   • Ditanyakan: Panjang sisi miring ($c$)

2. **Rumus:**
   $$c^2 = a^2 + b^2$$
   $$c = \sqrt{a^2 + b^2}$$

3. **Langkah Perhitungan:**
   • $c = \sqrt{${a}^2 + ${b}^2}$
   • $c = \sqrt{${a * a} + ${b * b}}$
   • $c = \sqrt{${a * a + b * b}}$
   • $c = \mathbf{$formatC}$

4. **Jawaban Akhir:**
   Panjang sisi miring segitiga siku-siku tersebut adalah **$formatC**.
            """.trimIndent()
        }

        return """
📐 **Konsep Teorema Pythagoras**

Teorema Pythagoras berlaku khusus untuk **segitiga siku-siku**:
• **Rumus Sisi Miring ($c$):**
  $$c = \sqrt{a^2 + b^2}$$
• **Rumus Sisi Alas ($a$):**
  $$a = \sqrt{c^2 - b^2}$$
• **Rumus Sisi Tegak ($b$):**
  $$b = \sqrt{c^2 - a^2}$$

💡 *Tips:* Masukkan angka sisinya (misal: "alas 3 tinggi 4") atau foto soalnya agar NanasAi hitungkan langsung!
        """.trimIndent()
    }

    private fun solveGeneralMath(lower: String, query: String): String? {
        // Lingkaran
        if (lower.contains("lingkaran") && (lower.contains("luas") || lower.contains("keliling"))) {
            val r = Regex("""\d+(?:[.,]\d+)?""").find(query)?.value?.replace(',', '.')?.toDoubleOrNull() ?: 7.0
            val luas = Math.PI * r * r
            val keliling = 2 * Math.PI * r
            return """
📐 **Perhitungan Lingkaran (Jari-jari r = $r)**

1. **Rumus:**
   • Luas = $\pi \times r^2$
   • Keliling = $2 \times \pi \times r$

2. **Hasil Perhitungan:**
   • **Luas:** ${String.format("%.2f", luas)}
   • **Keliling:** ${String.format("%.2f", keliling)}
            """.trimIndent()
        }

        // Kecepatan, Jarak, Waktu
        if (lower.contains("kecepatan") || lower.contains("jarak tempuh")) {
            return """
🚗 **Rumus Kecepatan, Jarak, dan Waktu**

• **Jarak ($s$):** $s = v \times t$
• **Kecepatan ($v$):** $v = \frac{s}{t}$
• **Waktu ($t$):** $t = \frac{s}{v}$

*Keterangan:*
- $s$ = Jarak (meter atau km)
- $v$ = Kecepatan (m/s atau km/jam)
- $t$ = Waktu (detik atau jam)
            """.trimIndent()
        }

        return null
    }

    private fun matchSejarah(lower: String): String? {
        if (lower.contains("rengasdengklok")) {
            return """
🏛️ **Peristiwa Rengasdengklok (16 Agustus 1945)**

1. **Latar Belakang:**
   Terjadi perbedaan pendapat antara golongan muda (Sukarni, Chaerul Saleh, Wikana) dan golongan tua (Ir. Soekarno dan Drs. Moh. Hatta) mengenai waktu pelaksanaan proklamasi kemerdekaan setelah Jepang menyerah kepada Sekutu (15 Agustus 1945).

2. **Jalannya Peristiwa:**
   Golongan muda membawa Soekarno dan Hatta ke Rengasdengklok (Karawang) untuk menjauhkan mereka dari pengaruh dan tekanan militer Jepang.

3. **Kesepakatan Akhir:**
   Mr. Achmad Soebardjo menjamin bahwa Proklamasi Kemerdekaan Indonesia akan dilaksanakan pada 17 Agustus 1945 di Jakarta. Akhirnya Soekarno dan Hatta dibawa kembali ke Jakarta untuk merumuskan teks proklamasi di kediaman Laksamana Maeda.
            """.trimIndent()
        }

        if (lower.contains("diponegoro")) {
            return """
🏛️ **Perang Diponegoro (1825 – 1830)**

1. **Penyebab Utama:**
   Pemasangan patok-patok jalan oleh Belanda yang melintasi tanah leluhur dan makam keluarga Pangeran Diponegoro di Tegalrejo tanpa izin, serta campur tangan Belanda dalam urusan keraton Yogyakarta.

2. **Taktik Perang:**
   • **Pangeran Diponegoro:** Menggunakan taktik perang gerilya yang meluas ke seluruh wilayah Jawa Tengah dan Jawa Timur.
   • **Belanda (Jenderal de Kock):** Memakai strategi *Benteng Stelsel* untuk mempersempit ruang gerak pasukan Diponegoro.

3. **Akhir Perang:**
   Pangeran Diponegoro ditangkap melalui jebakan perundingan damai di Magelang pada tahun 1830, lalu diasingkan ke Manado dan Makassar hingga wafat pada 1855.
            """.trimIndent()
        }

        if (lower.contains("proklamasi") || lower.contains("17 agustus")) {
            return """
🇮🇩 **Proklamasi Kemerdekaan Indonesia (17 Agustus 1945)**

• **Waktu & Tempat:** Jumat, 17 Agustus 1945 pukul 10.00 WIB di Jl. Pegangsaan Timur No. 56, Jakarta.
• **Pembaca Teks:** Ir. Soekarno didampingi Drs. Moh. Hatta.
• **Pengetik Naskah:** Sayuti Melik (dengan beberapa perubahan kata dari naskah tulisan tangan Soekarno).
• **Pengibar Bendera Merah Putih:** Latief Hendraningrat dan Suhud Sastro Kusumo.
• **Penjahit Bendera:** Fatmawati Soekarno.
            """.trimIndent()
        }

        if (lower.contains("bpupki") || lower.contains("ppki")) {
            return """
🏛️ **BPUPKI dan PPKI**

1. **BPUPKI (Dokuritsu Junbi Cosakai):**
   • Dibentuk: 1 Maret 1945 (diresmikan 29 April 1945) oleh Letjen Kumakichi Harada.
   • Ketua: Dr. K.R.T. Radjiman Wedyodiningrat.
   • Tugas: Menyelidiki dan merumuskan dasar negara (menghasilkan Piagam Jakarta pada 22 Juni 1945).

2. **PPKI (Dokuritsu Junbi Inkai):**
   • Dibentuk: 7 Agustus 1945 menggantikan BPUPKI.
   • Ketua: Ir. Soekarno, Wakil: Drs. Moh. Hatta.
   • Keputusan Sidang 18 Agustus 1945:
     1. Mengesahkan UUD 1945.
     2. Memilih Ir. Soekarno sebagai Presiden dan Moh. Hatta sebagai Wakil Presiden.
     3. Membentuk Komite Nasional Indonesia Pusat (KNIP) untuk membantu presiden.
            """.trimIndent()
        }

        return null
    }

    private fun matchEnglish(lower: String): String? {
        if (lower.contains("present tense") || lower.contains("past tense") || lower.contains("tenses")) {
            return """
🇬🇧 **Perbedaan Simple Present Tense vs Simple Past Tense**

1. **Simple Present Tense (Kebiasaan / Fakta Umum):**
   • **Rumus:**
     - (+) $S + V_1 (s/es) + O$
     - (-) $S + \text{do/does not} + V_1 + O$
   • **Time Signals:** *always, usually, every day, often*.
   • **Contoh:** "I *study* English every day." / "She *reads* a book."

2. **Simple Past Tense (Kejadian Masa Lampau):**
   • **Rumus:**
     - (+) $S + V_2 + O$
     - (-) $S + \text{did not} + V_1 + O$
   • **Time Signals:** *yesterday, last night, two days ago, in 2020*.
   • **Contoh:** "I *studied* English yesterday." / "She *bought* a book."
            """.trimIndent()
        }

        if (lower.contains("passive voice")) {
            return """
🇬🇧 **Passive Voice (Kalimat Pasif)**

Di bahasa Inggris, kalimat pasif memiliki pola umum:
$$\mathbf{Subject + To\ Be + Verb_3 (Past\ Participle)}$$

**Contoh Perubahan:**
• **Active:** "Adnan *writes* a letter."
• **Passive:** "A letter *is written* by Adnan."

• **Active:** "They *cleaned* the room."
• **Passive:** "The room *was cleaned* by them."
            """.trimIndent()
        }

        return null
    }

    private fun matchPPKn(lower: String): String? {
        if (lower.contains("pancasila") || lower.contains("sila")) {
            return """
🇮🇩 **Pancasila: Makna dan Nilai Luhur**

1. **Sila ke-1: Ketuhanan Yang Maha Esa**
   • Nilai: Keyakinan kepada Tuhan, toleransi antarumat beragama, dan kebebasan beribadah.
2. **Sila ke-2: Kemanusiaan yang Adil dan Beradab**
   • Nilai: Persamaan derajat (HAM), tenggang rasa, tolong-menolong tanpa membeda-bedakan.
3. **Sila ke-3: Persatuan Indonesia**
   • Nilai: Cinta tanah air, rela berkorban, mengutamakan kepentingan bangsa di atas golongan (Bhinneka Tunggal Ika).
4. **Sila ke-4: Kerakyatan yang Dipimpin oleh Hikmat Kebijaksanaan dalam Permusyawaratan/Perwakilan**
   • Nilai: Musyawarah untuk mufakat, menghargai pendapat orang lain, demokrasi.
5. **Sila ke-5: Keadilan Sosial bagi Seluruh Rakyat Indonesia**
   • Nilai: Gotong royong, keadilan hak & kewajiban, tidak bergaya hidup mewah, menghargai karya orang lain.
            """.trimIndent()
        }

        if (lower.contains("uud 1945") || lower.contains("pasal")) {
            return """
🇮🇩 **Pasal-Pasal Penting UUD 1945**

• **Pasal 27 ayat 1:** Segala warga negara bersamaan kedudukannya di dalam hukum dan pemerintahan.
• **Pasal 27 ayat 2:** Hak atas pekerjaan dan penghidupan yang layak bagi kemanusiaan.
• **Pasal 28:** Kemerdekaan berserikat, berkumpul, dan mengeluarkan pendapat.
• **Pasal 29 ayat 2:** Negara menjamin kemerdekaan tiap-tiap penduduk untuk memeluk agamanya masing-masing.
• **Pasal 30 ayat 1:** Hak dan kewajiban ikut serta dalam usaha pertahanan dan keamanan negara.
• **Pasal 31 ayat 1:** Setiap warga negara berhak mendapat pendidikan.
• **Pasal 33 ayat 3:** Bumi, air, dan kekayaan alam dikuasai oleh negara dan dipergunakan untuk sebesar-besar kemakmuran rakyat.
            """.trimIndent()
        }

        return null
    }

    private fun matchScience(lower: String): String? {
        if (lower.contains("fotosintesis")) {
            return """
🌿 **Proses Fotosintesis pada Tumbuhan**

1. **Pengertian:**
   Proses pembuatan makanan oleh tumbuhan hijau yang memiliki klorofil dengan memanfaatkan energi cahaya matahari.

2. **Persamaan Reaksi Kimia:**
   $$6CO_2 + 6H_2O \xrightarrow{\text{Cahaya + Klorofil}} C_6H_{12}O_6 + 6O_2$$
   *(Karbon Dioksida + Air $\rightarrow$ Glukosa + Oksigen)*

3. **Faktor yang Mempengaruhi:**
   • Intensitas cahaya matahari
   • Ketersediaan air ($H_2O$) dari akar
   • Konsentrasi Karbon Dioksida ($CO_2$) dari udara
   • Klorofil (zat hijau daun)
            """.trimIndent()
        }

        if (lower.contains("hukum newton")) {
            return """
🍎 **Hukum Gerak Newton**

1. **Hukum I Newton (Inersia / Kelembaman):**
   "Benda akan tetap diam atau bergerak lurus beraturan jika tidak ada gaya luar yang bekerja."
   $$\sum F = 0$$

2. **Hukum II Newton:**
   "Percepatan sebanding dengan gaya total dan berbanding terbalik dengan massa benda."
   $$F = m \times a$$

3. **Hukum III Newton (Aksi - Reaksi):**
   "Setiap ada gaya aksi, akan ada gaya reaksi yang besarnya sama namun berlawanan arah."
   $$F_{\text{aksi}} = -F_{\text{reaksi}}$$
            """.trimIndent()
        }

        if (lower.contains("tata surya") || lower.contains("planet")) {
            return """
🪐 **Susunan Planet dalam Tata Surya**

Urutan planet dari yang terdekat dengan Matahari:
1. **Merkurius** (Planet terkecil dan terdekat dengan Matahari)
2. **Venus** (Planet terpanas, dijuluki Bintang Fajar/Kejora)
3. **Bumi** (Satu-satunya planet berpenghuni yang diketahui)
4. **Mars** (Dijuluki Planet Merah karena kandungan besi oksida)
5. **Jupiter** (Planet terbesar dalam tata surya)
6. **Saturnus** (Memiliki sistem cincin spektakuler dari es dan batuan)
7. **Uranus** (Planet terdingin dengan sumbu rotasi rebah)
8. **Neptunus** (Planet terjauh dan berangin paling kencang)
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
2. Iman kepada Malaikat-malaikat Allah
3. Iman kepada Kitab-kitab Allah
4. Iman kepada Rasul-rasul Allah
5. Iman kepada Hari Akhir (Kiamat)
6. Iman kepada Qada dan Qadar

**5 Rukun Islam:**
1. Mengucapkan dua kalimat Syahadat
2. Mendirikan Sholat 5 waktu
3. Menunaikan Zakat
4. Berpuasa di bulan Ramadhan
5. Menunaikan Ibadah Haji bagi yang mampu
            """.trimIndent()
        }

        if (lower.contains("toleransi")) {
            return """
🤝 **Sikap Toleransi dalam Kehidupan Beragama & Bermasyarakat**

1. **Definisi:**
   Sikap saling menghormati dan menghargai perbedaan keyakinan, suku, ras, budaya, dan pendapat orang lain.

2. **Penerapan Nyata di Sekolah:**
   • Tidak mengganggu teman yang sedang beribadah sesuai agamanya.
   • Berteman dengan siapa saja tanpa membedakan suku atau latar belakang.
   • Menghargai pendapat teman saat berdiskusi di kelas.
   • Menjunjung tinggi persaudaraan dan perdamaian (Bhinneka Tunggal Ika).
            """.trimIndent()
        }

        return null
    }

    private fun generateGeneralAnswer(query: String, lower: String): String {
        return """
💡 **Pembahasan Pertanyaan: "${query.take(60)}"**

1. **Konsep Dasar:**
   Pertanyaanmu berkaitan dengan topik pembelajaran mandiri. Dalam menyelesaikan atau memahami materi ini, perhatikan kata kunci utama yang ditanyakan.

2. **Langkah Pemahaman:**
   • Pahami definisi dan rumus/konsep yang mendasarinya.
   • Catat poin-poin penting secara berurutan.
   • Terapkan contoh soal atau studi kasus nyata untuk menguji pemahamanmu.

3. **Saran NanasAi:**
   Kamu bisa memberikan rincian pertanyaan yang lebih spesifik, menyebutkan angka soal matematikanya, atau memotret lembar soal langsung dari bukum! 🍍
        """.trimIndent()
    }
}
