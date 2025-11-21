package com.example.todoapplication.data

import com.google.firebase.Timestamp

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)