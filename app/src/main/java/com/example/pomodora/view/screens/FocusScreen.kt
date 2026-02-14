package com.example.pomodora.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodora.services.PermissionHelper
import com.example.pomodora.services.TimerState
import com.example.pomodora.view.utils.GrowingTreeIcon
import com.example.pomodora.view.utils.TimerDisplayBox
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
        val totalTime = if (timerState is TimerState.OnBreak) totalBreakTime else totalFocusTime

        // Logic: (Total - Left) / Total gives us 0.0 at start, 1.0 at finish
        val elapsed = totalTime - timeLeft
        val fraction = elapsed.toFloat() / totalTime

        fraction.coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(targetValue = progress, label = "Progress")

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // --- Circular Timer ---
        Box(contentAlignment = Alignment.Center) {
            ExpressiveWaveTimer(
                progress = animatedProgress
            )
            GrowingTreeIcon(progress = animatedProgress, timerState = timerState)

        }

        TimerDisplayBox(timeString = formatTime(timeLeft))


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

            AnimatedFocusButtons(
                timerState = timerState,
                onStart = { viewModel.startFocus() },
                onGiveUp = { viewModel.giveUp() },
                onTakeBreak = { viewModel.startBreak() }
            )
        }



    }
}

// --- Helper Components ---

@Composable
fun ExpressiveWaveTimer(
    progress: Float,
    modifier: Modifier = Modifier
) {
    // 1. Infinite transition for the wave motion
    val trackThickness = 24.dp
    val activeIndicatorHeight = 24.dp // Stroke width of the wave
    val waveAmplitude = 10.dp         // Deviation from center
    val waveLength = 65.dp           // Distance between peaks
    val gapInDegrees = 20f

    // --- Colors ---
    val activeColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)

    // --- Animation ---
    val infiniteTransition = rememberInfiniteTransition(label = "WaveTransition")

    // Animate phase to make the wave "flow"
    // We animate from 0 to 2*PI (one full cycle)
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    Canvas(modifier = modifier.size(300.dp)) {
        val trackThicknessPx = trackThickness.toPx()
        val waveStrokePx = activeIndicatorHeight.toPx()
        val amplitudePx = waveAmplitude.toPx()
        val waveLengthPx = waveLength.toPx()

        val sizeMin = size.minDimension
        val radius = (sizeMin - trackThicknessPx - (amplitudePx * 2)) / 2
        val center = Offset(size.width / 2, size.height / 2)

        val startAngle = -90f // Top center
        val totalSweep = 360f
        val activeSweepAngle = totalSweep * progress

        // -------------------------------------------------------
        // 1. Draw the TRACK (With Gaps)
        // -------------------------------------------------------
        // We add a gap at the START (where it meets the wave head)
        // And we subtract a gap at the END (where it wraps to the wave tail)

        val trackStartAngle = startAngle + activeSweepAngle + gapInDegrees
        // Calculate remaining sweep, removing the gap from BOTH ends
        val trackSweepAngle = (totalSweep - activeSweepAngle) - (gapInDegrees * 2)

        if (trackSweepAngle > 1f) { // Only draw if there is track left
            drawArc(
                color = trackColor,
                startAngle = trackStartAngle,
                sweepAngle = trackSweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = trackThicknessPx, cap = StrokeCap.Round)
                // Using 'Butt' cap so it connects cleanly to the wave
            )
        }

        // -------------------------------------------------------
        // 2. Draw the ACTIVE WAVE (The "Elapsed" Time)
        // -------------------------------------------------------
        if (progress > 0) {
            val path = Path()

            val circumference = 2 * Math.PI * radius
            val waveCount = (circumference / waveLengthPx).toFloat()
            val steps = (activeSweepAngle * 2).toInt().coerceAtLeast(2)

            for (i in 0..steps) {
                val currentAngleDeg = startAngle + (activeSweepAngle * (i.toFloat() / steps))
                val angleRad = Math.toRadians(currentAngleDeg.toDouble()).toFloat()

                val waveOffset = kotlin.math.sin(angleRad * waveCount + wavePhase) * amplitudePx
                val r = radius + waveOffset

                val x = center.x + r * kotlin.math.cos(angleRad)
                val y = center.y + r * kotlin.math.sin(angleRad)

                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = activeColor,
                style = Stroke(
                    width = waveStrokePx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}


@Composable
fun AnimatedFocusButtons(
    timerState: TimerState,
    onStart: () -> Unit,
    onGiveUp: () -> Unit,
    onTakeBreak: () -> Unit
) {
    val isFocusing = timerState is TimerState.Focusing
    val isIdle = timerState is TimerState.Idle

    // Animation Configuration
    // We animate the weights. A small non-zero value keeps the button present but tiny.
    val animSpec = tween<Float>(durationMillis = 500)

    // If Idle: GiveUp is small (weight 1), Start is big (weight 4)
    // If Focusing: GiveUp is big (weight 4), Start is tiny (weight 0.01)
    val giveUpWeight by animateFloatAsState(if (isFocusing) 4f else 1f, animSpec, label = "giveUpWt")
    val startWeight by animateFloatAsState(if (isIdle) 4f else 0.01f, animSpec, label = "startWt")

    when (timerState) {
        TimerState.WaitingForBreak -> {
            // Simple single button for break state
            Button(
                onClick = onTakeBreak,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text("Take Break (5m)") }
        }
        TimerState.OnBreak -> {
            Text("Enjoy your break!", style = MaterialTheme.typography.titleMedium)
        }
        else -> {
            // The Row containing the two animated buttons
            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // --- Give Up Button (Left) ---
                // We hide the text if the weight is too small to prevent overflow
                val showGiveUpText = giveUpWeight > 1.5f

                Button(
                    onClick = onGiveUp,
                    // Use tertiary (e.g., reddish) color for negative action
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    // Dynamically change shape based on prominence
                    shape = RoundedCornerShape(if (showGiveUpText) 16.dp else 50.dp),
                    // Apply the animated weight
                    modifier = Modifier.weight(giveUpWeight).fillMaxHeight(),
                    enabled = isFocusing // Only enable give up if actually focusing
                ) {
                    AnimatedVisibility(visible = showGiveUpText, enter = fadeIn(), exit = fadeOut()) {
                        Text("Give Up", maxLines = 1)
                    }
                    if (!showGiveUpText) {
                        // Show an "X" icon when small
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                }

                // --- Start Button (Right) ---
                val showStartText = startWeight > 1.5f
                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(if (showStartText) 16.dp else 50.dp),
                    modifier = Modifier.weight(startWeight).fillMaxHeight(),
                    enabled = isIdle // Only enable start if idle
                ) {
                    AnimatedVisibility(visible = showStartText, enter = fadeIn(), exit = fadeOut()) {
                        Text("Plant Tree (25m)", maxLines = 1)
                    }
                }
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}