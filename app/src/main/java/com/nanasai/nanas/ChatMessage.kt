package com.nanasai.nanas

import android.net.Uri

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val text: String,
    val isUser: Boolean,
    val imageUri: Uri? = null,
    val timestamp: String = ""
)
