package com.example.pomodora.view.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodora.services.PermissionHelper
import com.example.pomodora.services.TimerState
import com.example.pomodora.viewModel.FocusViewModel
import java.util.Locale

@Composable
fun FocusScreen(
    viewModel: FocusViewModel = viewModel<FocusViewModel>()
) {
    val timerState by viewModel.timerState.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val context = LocalContext.current

    // State to track permissions
    var hasPermissions by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermissions = PermissionHelper.hasUsageStatsPermission(context) &&
                        PermissionHelper.hasOverlayPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    // Constants for Max Time (to calculate progress)
    val totalFocusTime = 25 * 60 * 1000L
    val totalBreakTime = 5 * 60 * 1000L

    // Calculate progress (0.0 to 1.0)
    val progress = remember(timeLeft, timerState) {
        when (timerState) {
            is TimerState.Focusing -> timeLeft.toFloat() / totalFocusTime
            is TimerState.OnBreak -> timeLeft.toFloat() / totalBreakTime
            else -> 1f
        }
    }

    val animatedProgress by animateFloatAsState(targetValue = progress, label = "Progress")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // --- Circular Timer ---
        Box(contentAlignment = Alignment.Center) {
            CircularTimer(
                progress = animatedProgress,
                color = if (timerState is TimerState.OnBreak) Color.Cyan else Color.Green
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(timeLeft),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when (timerState) {
                        TimerState.Focusing -> "Planting..."
                        TimerState.OnBreak -> "Watering..."
                        TimerState.WaitingForBreak -> "Tree Grown!"
                        else -> "Ready to Plant"
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        if (!hasPermissions) {
            // Permission Warning Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Permissions Required", fontWeight = FontWeight.Bold)
                    Text("To prevent you from quitting, we need 'Usage Access' and 'Display Over Other Apps'.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (!PermissionHelper.hasUsageStatsPermission(context)) {
                            PermissionHelper.requestUsageStatsPermission(context)
                        } else if (!PermissionHelper.hasOverlayPermission(context)) {
                            PermissionHelper.requestOverlayPermission(context)
                        }
                    }) {
                        Text("Grant Permissions")
                    }
                }
            }
        } else {

            // --- Action Buttons ---
            when (timerState) {
                TimerState.Idle -> {
                    Button(
                        onClick = { viewModel.startFocus() },
                        modifier = Modifier.fillMaxWidth(0.6f).height(56.dp)
                    ) {
                        Text("Plant (25m)")
                    }
                }
                TimerState.Focusing -> {
                    Button(
                        onClick = { viewModel.giveUp() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Give Up")
                    }
                }
                TimerState.WaitingForBreak -> {
                    Button(
                        onClick = { viewModel.startBreak() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Take Break (5m)")
                    }
                }
                TimerState.OnBreak -> {
                    Button(
                        onClick = { viewModel.giveUp() }, // Actually "Skip Break" logic usually
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Skip Break")
                    }
                }
            }
        }



    }
}

// --- Helper Components ---

@Composable
fun CircularTimer(progress: Float, color: Color) {
    Canvas(modifier = Modifier.size(300.dp)) {
        val strokeWidth = 20.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

        // Background Circle (Gray)
        drawArc(
            color = Color.LightGray.copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidth)
        )

        // Progress Circle
        drawArc(
            color = color,
            startAngle = -90f, // Start from top
            sweepAngle = 360 * progress,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}