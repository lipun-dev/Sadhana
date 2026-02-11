package com.example.pomodora.viewModel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodora.services.FocusService
import com.example.pomodora.services.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FocusViewModel(application: Application) : AndroidViewModel(application) {

    // --- Service Connection Logic ---
    private var focusService: FocusService? = null
    private var isBound = false

    // UI States (Mirrors of Service State)
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState = _timerState.asStateFlow()

    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft = _timeLeft.asStateFlow()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as FocusService.LocalBinder
            focusService = binder.getService()
            isBound = true

            // Start observing the Service's flows immediately
            observeServiceState()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            focusService = null
        }
    }

    init {
        // Bind to the service immediately when ViewModel is created
        Intent(getApplication(), FocusService::class.java).also { intent ->
            getApplication<Application>().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun observeServiceState() {
        focusService?.let { service ->
            viewModelScope.launch {
                service.timeLeftInMillis.collect { _timeLeft.value = it }
            }
            viewModelScope.launch {
                service.timerState.collect { _timerState.value = it }
            }
        }
    }

    // --- User Actions ---

    fun startFocus() {
        // We must START the service (not just bind) so it keeps running in background
        val intent = Intent(getApplication(), FocusService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            getApplication<Application>().startForegroundService(intent)
        } else {
            getApplication<Application>().startService(intent)
        }
        focusService?.startFocusSession()
    }

    fun startBreak() {
        focusService?.startBreakSession()
    }

    fun giveUp() {
        focusService?.giveUp()
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(connection)
            isBound = false
        }
    }
}