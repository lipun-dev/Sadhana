package com.example.pomodora.view.wavyAnimation

enum class AuthScreenState {
    Input,      // User is typing
    Loading,    // Checking credentials (waves rise)
    Success,    // "Login Success" + Dashboard button
    Failed      // "Login Failed" -> returns to Input
}