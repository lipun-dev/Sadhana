package com.example.pomodora.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pomodora.PomoApp

class ViewModelFactory(private val app: PomoApp) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(app.authRepo) as T
            }
            modelClass.isAssignableFrom(StatsViewModel::class.java) -> {
                StatsViewModel(app.statsRepo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}