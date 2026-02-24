package com.example.pomodora.model

import java.time.LocalDate

data class FocusEntry(
    val date: LocalDate,
    val minutes: Int
)