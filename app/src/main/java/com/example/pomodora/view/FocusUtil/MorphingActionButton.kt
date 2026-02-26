package com.example.pomodora.view.FocusUtil

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodora.services.TimerState
import com.example.pomodora.ui.theme.FocusAccentGreen
import com.example.pomodora.ui.theme.FocusBgDeep
import com.example.pomodora.ui.theme.FocusBreakBlue
import com.example.pomodora.ui.theme.FocusBreakBlueMuted
import com.example.pomodora.ui.theme.FocusDangerMuted
import com.example.pomodora.ui.theme.FocusDangerRed
import com.example.pomodora.ui.theme.FocusMutedGreen
import com.example.pomodora.ui.theme.GoldAccent
import kotlinx.coroutines.delay

@Composable
fun MorphingActionButton(
    timerState: TimerState,
    onStart: () -> Unit,
    onGiveUp: () -> Unit,
    onTakeBreak: () -> Unit
) {
    // Sealed state to drive all visual properties from one source of truth
    val isIdle              = timerState is TimerState.Idle
    val isFocusing          = timerState is TimerState.Focusing
    val isWaiting           = timerState is TimerState.WaitingForBreak
    val isOnBreak           = timerState is TimerState.OnBreak

    // ── Container color morphs across states
    val containerColor by animateColorAsState(
        targetValue = when {
            isFocusing -> FocusDangerMuted
            isOnBreak  -> FocusBreakBlueMuted
            isWaiting  -> Color(0xFF1A2D1A)
            else       -> FocusMutedGreen   // idle — will be overlaid by gradient brush
        },
        animationSpec = tween(500),
        label         = "ContainerColor"
    )

    // ── Border color
    val borderColor by animateColorAsState(
        targetValue = when {
            isFocusing -> FocusDangerRed.copy(alpha = 0.4f)
            isOnBreak  -> FocusBreakBlue.copy(alpha = 0.35f)
            isWaiting  -> GoldAccent.copy(alpha = 0.35f)
            else       -> FocusAccentGreen.copy(alpha = 0.5f)
        },
        animationSpec = tween(500),
        label         = "BorderColor"
    )

    // ── Corner radius: pill when idle/break, rounded rect when focusing
    val cornerRadius by animateDpAsState(
        targetValue   = when {
            isFocusing -> 20.dp   // squared — feels decisive/serious
            isWaiting  -> 28.dp   // softer — reward feeling
            else       -> 32.dp   // pill — inviting CTA
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label         = "CornerRadius"
    )

    // ── Height: taller when showing split (focus state)
    val buttonHeight by animateDpAsState(
        targetValue   = 64.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "ButtonHeight"
    )

    // ── Elastic entrance on first composition
    var appeared by remember { mutableStateOf(false) }
    val entranceScale by animateFloatAsState(
        targetValue   = if (appeared) 1f else 0.7f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
        label         = "EntranceScale"
    )
    LaunchedEffect(Unit) {
        delay(80)
        appeared = true
    }

    // ── Press feedback via InteractionSource
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue   = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label         = "PressScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .graphicsLayer {
                val combinedScale = entranceScale * pressScale
                scaleX = combinedScale
                scaleY = combinedScale
                shape = RoundedCornerShape(cornerRadius.toPx())
                clip = true
            }
            // 🚀 OPTIMIZATION: drawBehind prevents recomposition when the color animates
            .drawBehind {
                if (isIdle) {
                    // Draw Gradient for Idle
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(FocusAccentGreen, Color(0xFF50FFAD))),
                        cornerRadius = CornerRadius(cornerRadius.toPx())
                    )
                } else {
                    // Draw solid animated color for others
                    drawRoundRect(
                        color = containerColor,
                        cornerRadius = CornerRadius(cornerRadius.toPx())
                    )
                }
                // Draw Border
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(width = 1.dp.toPx()),
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            }
    ) {
        // ── Content swaps with shared-axis vertical slide ──────────────────
        AnimatedContent(
            targetState = timerState,
            transitionSpec = {
                // New content enters from below; old exits upward — forward-nav feel
                (fadeIn(tween(320)) + slideInVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    initialOffsetY = { it / 2 }
                )) togetherWith
                        (fadeOut(tween(200)) + slideOutVertically(
                            animationSpec = tween(200),
                            targetOffsetY = { -it / 2 }
                        ))
            },
            modifier = Modifier.fillMaxSize(),
            label    = "ButtonContent"
        ) { state ->
            when (state) {

                // ── IDLE: Full-width "Plant Tree" CTA ──────────────────────
                is TimerState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = interactionSource,
                                indication        = null,
                                onClick           = onStart
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🌱", fontSize = 22.sp)
                            Column {
                                Text(
                                    "Plant a Tree",
                                    color      = FocusBgDeep,
                                    fontWeight = FontWeight.Black,
                                    fontSize   = 16.sp
                                )
                                Text(
                                    "25 min focus session",
                                    color    = FocusBgDeep.copy(0.55f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // ── FOCUSING: Split — Give Up (left) + Break (right) ───────
                // Both actions live inside ONE container, separated by a 1dp
                // vertical divider line. This removes the two-button layout
                // entirely and keeps the UI compact and decisive.
                is TimerState.Focusing -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                                onClick           = onGiveUp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Close,
                                contentDescription = "Give Up",
                                tint               = FocusDangerRed,
                                modifier           = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    "Give Up",
                                    color      = FocusDangerRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 16.sp
                                )
                                Text(
                                    "Your tree will wither",
                                    color    = FocusDangerRed.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // ── WAITING FOR BREAK: Single break CTA ───────────────────
                is TimerState.WaitingForBreak -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                                onClick           = onTakeBreak
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("☕", fontSize = 20.sp)
                            Column {
                                Text(
                                    "Take a Break",
                                    color      = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 16.sp
                                )
                                Text(
                                    "5 min rest — you earned it",
                                    color    = GoldAccent.copy(0.55f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // ── ON BREAK: Pulsing status chip (non-interactive) ────────
                is TimerState.OnBreak -> {
                    BreakPulseContent()
                }

                else -> { /* fallback — should not occur */ }
            }
        }
    }
}

@Composable
private fun BreakPulseContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "BreakPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue  = 0.45f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "BreakAlpha"
    )
    Box(modifier = Modifier.fillMaxSize().graphicsLayer { this.alpha = alpha },
        contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🌿", fontSize = 18.sp)
            Text(
                "Enjoying your break…",
                color      = FocusBreakBlue,
                fontWeight = FontWeight.Medium,
                fontSize   = 15.sp,
                letterSpacing = 0.3.sp
            )
        }
    }
}