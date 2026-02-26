package com.example.pomodora.view.FocusUtil

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodora.services.TimerState
import com.example.pomodora.ui.theme.FocusBgSurface
import com.example.pomodora.ui.theme.FocusBreakBlue
import com.example.pomodora.ui.theme.FocusCardBorder
import com.example.pomodora.ui.theme.FocusCardSurface
import com.example.pomodora.ui.theme.FocusRingTrack
import com.example.pomodora.ui.theme.FocusTextSecondary
import com.example.pomodora.ui.theme.GlowGreen
import com.example.pomodora.ui.theme.GoldAccent
import com.example.pomodora.ui.theme.TextPrimary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PolishedWaveTimer(
    progress: () -> Float,
    ringColor: Color,
    timerState: TimerState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveFlow")

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation  = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    // Breath effect — ring subtly expands/contracts when active
    val isFocusing = timerState is TimerState.Focusing
    val breathScale by animateFloatAsState(
        targetValue = if (isFocusing) 1.0f else 0.98f,
        animationSpec = tween(600),
        label = "BreathScale"
    )

    Canvas(
        modifier = modifier
            .size(300.dp)
            .graphicsLayer {
                scaleX = breathScale
                scaleY = breathScale
            }
    ) {
        val currentProg = progress()
        // REFINED: Drastically thinner stroke values
        val trackPx     = 4.dp.toPx()  // Was 18.dp
        val wavePx      = 6.dp.toPx()  // Was 20.dp
        val amplitudePx = 5.dp.toPx()  // Was 9.dp (tighter wave)
        val waveLenPx   = 60.dp.toPx()

        val sizeMin     = size.minDimension
        val radius      = (sizeMin - trackPx - amplitudePx * 2) / 2
        val center      = Offset(size.width / 2, size.height / 2)
        val startAngle  = -90f
        val gapDeg      = 12f // Slightly smaller gap for thinner lines

        val trackStart = startAngle + currentProg * 360f + gapDeg
        val trackSweep = (360f - currentProg * 360f) - gapDeg * 2

        if (trackSweep > 1f) {
            drawArc(
                color      = FocusRingTrack,
                startAngle = trackStart,
                sweepAngle = trackSweep,
                useCenter  = false,
                topLeft    = Offset(center.x - radius, center.y - radius),
                size       = Size(radius * 2, radius * 2),
                style      = Stroke(width = trackPx, cap = StrokeCap.Round)
            )
        }

        if (currentProg > 0.005f) {
            val activeSweep = currentProg * 360f
            val circumference = 2 * Math.PI * radius
            val waveCount = (circumference / waveLenPx).toFloat()
            val steps = (activeSweep * 2.5f).toInt().coerceAtLeast(4)
            val path = Path()

            for (i in 0..steps) {
                val angleDeg = startAngle + activeSweep * (i.toFloat() / steps)
                val angleRad = Math.toRadians(angleDeg.toDouble()).toFloat()
                val waveOff  = sin(angleRad * waveCount + wavePhase) * amplitudePx
                val r        = radius + waveOff
                val x        = center.x + r * cos(angleRad)
                val y        = center.y + r * sin(angleRad)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            // Glow pass (softened for thinner line)
            drawPath(
                path  = path,
                color = ringColor.copy(alpha = 0.35f),
                style = Stroke(width = wavePx * 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Main wave
            drawPath(
                path  = path,
                brush = Brush.sweepGradient(
                    listOf(ringColor.copy(0.6f), ringColor, ringColor.copy(0.8f)),
                    center = center
                ),
                style = Stroke(width = wavePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

@Composable
fun PolishedTimerDisplay(
    timeString: () -> String,
    timerState: TimerState,
    ringColor: () ->Color
) {

    // Subtle scale pop on each second tick
    val digitScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "DigitScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .drawBehind {
                val dynamicRingColor = ringColor()

                // Draw the vertical gradient background
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(FocusCardSurface, FocusBgSurface)
                    )
                )

                // Draw the horizontal gradient border
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(
                            dynamicRingColor.copy(0.3f),
                            FocusCardBorder,
                            dynamicRingColor.copy(0.3f)
                        )
                    ),
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            .padding(horizontal = 36.dp, vertical = 16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Mode pill
            AnimatedContent(
                targetState = timerState,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "ModePill"
            ) { state ->
                val (pillText, pillColor) = when (state) {
                    is TimerState.Focusing        -> "FOCUS" to GlowGreen
                    is TimerState.OnBreak         -> "BREAK" to FocusBreakBlue
                    is TimerState.WaitingForBreak -> "DONE"  to GoldAccent
                    else                          -> "IDLE"  to FocusTextSecondary
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(pillColor.copy(alpha = 0.12f))
                        .border(1.dp, pillColor.copy(alpha = 0.3f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                ) {
                    Text(
                        text          = pillText,
                        color         = pillColor,
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time digits
            Text(
                text       = timeString(),
                fontSize   = 52.sp,
                fontWeight = FontWeight.Black,
                color      = TextPrimary,
                letterSpacing = (-1).sp,
                modifier   = Modifier.graphicsLayer {
                    // 🚀 OPTIMIZATION 4: Defer scale animation to Draw Phase
                    scaleX = digitScale
                    scaleY = digitScale
                }
            )
        }
    }
}


@Composable
fun SessionProgressDots(progress: () ->Float, color: Color) {
    val segments = 5
    Canvas(
        modifier = Modifier
            .height(6.dp)
            .width((segments * 8 + (segments - 1) * 8 + 16).dp) // Estimate max width
    ) {
        val currentProgress = progress()
        val spacingPx = 8.dp.toPx()
        val baseWidthPx = 8.dp.toPx()
        val expandedWidthPx = 24.dp.toPx()
        val heightPx = 6.dp.toPx()

        var currentX = 0f

        for (i in 0 until segments) {
            val threshold = (i + 1).toFloat() / segments
            val isFilled = currentProgress >= threshold - (1f / segments)
            val dotColor = if (isFilled) color else FocusRingTrack

            // Calculate width without animating in layout phase
            val isActiveSegment = i == (currentProgress * segments).toInt().coerceIn(0, segments - 1)
            // Note: If you want smooth width animation here, you can drive it via an Animatable
            // inside a LaunchedEffect based on the progress, and read its value here.
            // For raw performance, snapping the active width is 100x cheaper.
            val dotWidth = if (isActiveSegment) expandedWidthPx else baseWidthPx

            drawRoundRect(
                color = dotColor,
                topLeft = Offset(currentX, 0f),
                size = Size(dotWidth, heightPx),
                cornerRadius = CornerRadius(heightPx / 2)
            )
            currentX += dotWidth + spacingPx
        }
    }
}
// ─── Permission Card ──────────────────────────────────────────────────────────

@Composable
fun PolishedPermissionCard(onGrant: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E1208))
            .border(1.dp, GoldAccent.copy(0.25f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("⚠️", fontSize = 20.sp)
            Text(
                "Permissions Required",
                color      = GoldAccent,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "To keep you accountable, grant Usage Access and Display Over Other Apps.",
            color    = FocusTextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF3D2800), GoldAccent.copy(0.25f))
                    )
                )
                .border(1.dp, GoldAccent.copy(0.45f), RoundedCornerShape(14.dp))
                .clickable(onClick = onGrant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Grant Permissions",
                color      = GoldAccent,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp
            )
        }
    }
}