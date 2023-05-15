package com.example.minichatapp.models

import java.util.Date

data class ChatMessage(
    val senderId: String,
    val receiverId: String,
    var message: String,
    val dateTime: String,
    var date: Date,
    val conversionId: String? = null,
    val conversionName: String? = null,
    val conversionImage: String? = null
)