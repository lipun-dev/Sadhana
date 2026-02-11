package com.example.pomodora.model

import com.google.firebase.Timestamp


data class Session(
    val id: String = "",
    val userId: String = "",
    val startTime: Timestamp = Timestamp.now(),
    val durationMinutes: Long = 0,
    val status: String = "COMPLETED", // or "WITHERED"
    val plantType: String = "default_tree"
)