package com.nanasai.nanas

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var rvChat: RecyclerView
    private lateinit var emptyView: View
    private lateinit var etPrompt: EditText
    private lateinit var btnSend: TextView
    private lateinit var btnCamera: ImageView
    private lateinit var layoutAttachment: LinearLayout
    private lateinit var tvAttachmentTitle: TextView
    private lateinit var btnRemoveAttachment: TextView
    private lateinit var btnAbout: LinearLayout

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private var attachedImageUri: Uri? = null
    private var extractedOcrText: String? = null
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            processSelectedImage(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init views
        rvChat = findViewById(R.id.rvChat)
        emptyView = findViewById(R.id.emptyView)
        etPrompt = findViewById(R.id.etPrompt)
        btnSend = findViewById(R.id.btnSend)
        btnCamera = findViewById(R.id.btnCamera)
        layoutAttachment = findViewById(R.id.layoutAttachment)
        tvAttachmentTitle = findViewById(R.id.tvAttachmentTitle)
        btnRemoveAttachment = findViewById(R.id.btnRemoveAttachment)
        btnAbout = findViewById(R.id.btnAbout)

        setupRecyclerView()
        setupListeners()

        // Init C++ AI Engine in background
        lifecycleScope.launch(Dispatchers.IO) {
            NanasAiNative.setup(applicationContext)
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvChat.layoutManager = layoutManager
        rvChat.adapter = chatAdapter
    }

    private fun setupListeners() {
        // Text watcher for ReactBits send button transition
        etPrompt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                updateSendButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnCamera.setOnClickListener {
            takePictureLauncher.launch("image/*")
        }

        btnRemoveAttachment.setOnClickListener {
            clearAttachment()
        }

        btnSend.setOnClickListener {
            handleSendMessage()
        }

        btnAbout.setOnClickListener {
            showAboutDialog()
        }

        // Clickable sample cards
        findViewById<View>(R.id.cardSamplePythagoras).setOnClickListener {
            sendMessageDirectly("Segitiga siku-siku alasnya 6 cm dan tingginya 8 cm, berapa panjang sisi miringnya?")
        }
        findViewById<View>(R.id.cardSampleSejarah).setOnClickListener {
            sendMessageDirectly("Jelaskan peristiwa Rengasdengklok menjelang kemerdekaan RI secara singkat dan jelas!")
        }
        findViewById<View>(R.id.cardSampleInggris).setOnClickListener {
            sendMessageDirectly("Tolong jelaskan perbedaan Simple Present Tense dan Past Tense serta berikan contohnya!")
        }
    }

    private fun updateSendButtonState() {
        val hasText = etPrompt.text.toString().trim().isNotEmpty()
        val hasAttachment = attachedImageUri != null

        if (hasText || hasAttachment) {
            btnSend.setBackgroundColor(ContextCompat.getColor(this, R.color.nanas_amber))
            btnSend.setTextColor(Color.parseColor("#09090B"))
        } else {
            btnSend.setBackgroundResource(R.drawable.bg_badge)
            btnSend.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
        }
    }

    private fun processSelectedImage(uri: Uri) {
        attachedImageUri = uri
        layoutAttachment.visibility = View.VISIBLE
        tvAttachmentTitle.text = "📷 Memproses gambar..."
        updateSendButtonState()

        try {
            val image = InputImage.fromFilePath(this, uri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    extractedOcrText = visionText.text.trim()
                    tvAttachmentTitle.text = if (extractedOcrText.isNullOrEmpty()) {
                        "📷 Foto Soal Terlampir"
                    } else {
                        "📷 Soal Terdeteksi: ${extractedOcrText!!.take(35)}..."
                    }
                }
                .addOnFailureListener {
                    tvAttachmentTitle.text = "📷 Foto Soal Terlampir"
                }
        } catch (e: Exception) {
            tvAttachmentTitle.text = "📷 Foto Soal Terlampir"
        }
    }

    private fun clearAttachment() {
        attachedImageUri = null
        extractedOcrText = null
        layoutAttachment.visibility = View.GONE
        updateSendButtonState()
    }

    private fun sendMessageDirectly(text: String) {
        etPrompt.setText(text)
        handleSendMessage()
    }

    private fun handleSendMessage() {
        val rawText = etPrompt.text.toString().trim()
        if (rawText.isEmpty() && attachedImageUri == null) return

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val userImage = attachedImageUri
        val ocrContext = extractedOcrText

        var fullPrompt = rawText
        if (!ocrContext.isNullOrEmpty()) {
            fullPrompt = "[Teks dari Foto Soal]:\n$ocrContext\n\n[Pertanyaan]:\n${if (rawText.isNotEmpty()) rawText else "Tolong jelaskan dan selesaikan soal pada gambar di atas."}"
        }

        // Add user message
        val userMsg = ChatMessage(
            text = if (rawText.isNotEmpty()) rawText else "📷 Foto Soal",
            isUser = true,
            imageUri = userImage,
            timestamp = currentTime
        )
        chatAdapter.addMessage(userMsg)
        emptyView.visibility = View.GONE
        rvChat.scrollToPosition(messages.size - 1)

        // Reset input
        etPrompt.setText("")
        clearAttachment()

        // Query NanasAi offline
        lifecycleScope.launch {
            val reply = withContext(Dispatchers.IO) {
                NanasAiNative.ask(fullPrompt)
            }

            val nanasMsg = ChatMessage(
                text = reply,
                isUser = false,
                timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )
            chatAdapter.addMessage(nanasMsg)
            rvChat.scrollToPosition(messages.size - 1)
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(NanasAiIdentity.DISPLAY_NAME)
            .setMessage(NanasAiIdentity.ABOUT_TEXT.trim())
            .setPositiveButton("Tutup", null)
            .show()
    }
}
