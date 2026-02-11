package com.example.pomodora.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pomodora.PomoApp
import com.example.pomodora.R
import com.example.pomodora.repo.SessionDbRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FocusService : Service() {

    private val binder = LocalBinder()
    private lateinit var repository: SessionDbRepo
    private lateinit var overlayHelper: OverlayHelper

    // Coroutine Scope for DB operations
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    // --- State Management ---
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState = _timerState.asStateFlow()

    private val _timeLeftInMillis = MutableStateFlow(0L)
    val timeLeftInMillis = _timeLeftInMillis.asStateFlow()

    private var timer: CountDownTimer? = null

    // Blocking Logic

    private var isMonitoring = false
    private var monitoringJob: Job? = null

    // Constants
    private val FOCUS_TIME = 25 * 60 * 1000L
    private val BREAK_TIME = 5 * 60 * 1000L

    override fun onCreate() {
        super.onCreate()
        // 1. Inject Repository manually from Application class
        repository = (application as PomoApp).repository
        overlayHelper = OverlayHelper(this)

        createNotificationChannel()
    }

    // --- Public Actions (Called from UI) ---

    fun startFocusSession() {
        // Cache allowed apps once at start to optimize performance
        startTimer(FOCUS_TIME, TimerState.Focusing)
        startBlockingMonitoring()

    }

    fun startBreakSession() {
        stopBlockingMonitoring() // No blocking during break
        startTimer(BREAK_TIME, TimerState.OnBreak)
    }

    fun giveUp() {
        timer?.cancel()
        stopBlockingMonitoring()
        _timerState.value = TimerState.Idle

        // Save "WITHERED" status to Firebase
        serviceScope.launch {
            // Calculate how much time they actually spent before giving up?
            // Or just 0 if strict. Let's save 0 for withered.
            repository.saveSession(duration = 0, isSuccess = false)
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    // --- Internal Logic ---

    private fun startTimer(duration: Long, state: TimerState) {
        timer?.cancel()
        _timerState.value = state

        // Start Foreground Notification
        startForeground(1, createNotification("Focus Mode On", "Planting your tree..."))

        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInMillis.value = millisUntilFinished
            }

            override fun onFinish() {
                handleTimerFinished(state)
            }
        }.start()
    }

    private fun handleTimerFinished(previousState: TimerState) {
        stopBlockingMonitoring()

        if (previousState == TimerState.Focusing) {
            // 25 mins done! Tree planted.
            _timerState.value = TimerState.WaitingForBreak
            serviceScope.launch {
                repository.saveSession(duration = 25, isSuccess = true)
            }
            updateNotification("Tree Planted!", "Time to water it (Take a break).")
        } else {
            // Break done!
            _timerState.value = TimerState.Idle
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    // --- App Blocking Monitor (The Shield) ---

    private fun startBlockingMonitoring() {
        isMonitoring = true
        monitoringJob = serviceScope.launch(Dispatchers.Default) {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            delay(1000)

            while (isActive && isMonitoring) {
                val currentApp = getForegroundApp(usageStatsManager)

                Log.d("FocusService", "Foreground App Detected: $currentApp")

                // If app is found, NOT in allowed list, and NOT our own app
                if (currentApp != null && currentApp != packageName && !isSystemUI(currentApp)) {

                    withContext(Dispatchers.Main) {
                        try {
                            // Show the Shield
                            overlayHelper.showOverlay {
                                // Action when they click "I Give Up"
                                giveUp()
                                overlayHelper.hideOverlay()
                            }
                            delay(3000)
                            overlayHelper.hideOverlay()
                        } catch (e: Exception) {
                            Log.e("FocusService", "Overlay failed: ${e.message}")
                        }
                    }
                } else {
                    if (currentApp == packageName) {
                        withContext(Dispatchers.Main) {
                            overlayHelper.hideOverlay()
                        }
                    }
                }
                delay(800) // Check every 1 second
            }
        }
    }
    private fun isSystemUI(packageName: String): Boolean {
        return packageName.contains("com.android.systemui") ||
                packageName.contains("com.google.android.inputmethod") || // Keyboard
                packageName.contains("nexuslauncher") || // Pixel Launcher
                packageName.contains("launcher") // Generic Launchers
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun stopBlockingMonitoring() {
        isMonitoring = false
        monitoringJob?.cancel()
        GlobalScope.launch(Dispatchers.Main) { overlayHelper.hideOverlay() }
    }

    private fun getForegroundApp(usageStatsManager: UsageStatsManager): String? {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 10000*60 // Look back 10 seconds to be safe

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )

        if (!usageStatsList.isNullOrEmpty()) {
            val sortedMap = java.util.TreeMap<Long, android.app.usage.UsageStats>()

            for (usageStats in usageStatsList) {
                sortedMap[usageStats.lastTimeUsed] = usageStats
            }

            if (sortedMap.isNotEmpty()) {
                // The last entry is the most recently used app
                return sortedMap.lastEntry()?.value?.packageName
            }
        }

        return null
    }

    // --- Boilerplate (Binder & Notification) ---

    inner class LocalBinder : Binder() { fun getService(): FocusService = this@FocusService }
    override fun onBind(intent: Intent): IBinder = binder

    // ... (createNotification and createNotificationChannel methods same as previous code) ...
    private fun createNotification(title: String, content: String): Notification {
        return NotificationCompat.Builder(this, "FocusChannel")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val notification = createNotification(title, content)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "FocusChannel", "Focus Session", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
        }
    }
}

// Helper Enum for UI State
sealed class TimerState {
    object Idle : TimerState()
    object Focusing : TimerState()       // 25 min timer running
    object WaitingForBreak : TimerState() // 25 min done, waiting for user to start break
    object OnBreak : TimerState()        // 5 min timer running
}