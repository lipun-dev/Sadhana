package com.example.pomodora

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.pomodora.services.FocusService
import com.example.pomodora.ui.theme.PomoDoraTheme
import com.example.pomodora.view.AppNavigation


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, FocusService::class.java)
        try {
            startService(intent)
            Log.d("MainActivity", "Service started successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to start service: ${e.message}")
        }
        enableEdgeToEdge()
        setContent {
            PomoDoraTheme {
                AppNavigation()
            }
        }

    }

}

