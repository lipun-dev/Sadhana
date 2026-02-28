package com.example.pomodora.view.utils

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DeepForest   = Color(0xFF0A1A0F)
private val MidForest    = Color(0xFF0F2318)
private val WarnAmber    = Color(0xFFE8A020)
private val WarnRed      = Color(0xFFD94040)
private val LeafGreen    = Color(0xFF4CAF6A)
private val TextPrimary  = Color(0xFFF0EBE0)
private val TextMuted    = Color(0xFF8A9E8C)

@Composable
fun WarningOverlayUI(
    onGiveUp: () -> Unit // Optional: Allow them to give up from the overlay
) {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")

    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "pulseScale"
    )

    val glowAlpha by pulseAnim.animateFloat(
        initialValue = 0.3f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "glowAlpha"
    )

    val ringRadius by pulseAnim.animateFloat(
        initialValue = 52f, targetValue = 68f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "ring"
    )

    // Leaf-fall shimmer
    val shimmerOffset by pulseAnim.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Restart),
        label = "shimmer"
    )

    // Give-up button reveal guard
    var showGiveUpConfirm by remember { mutableStateOf(false) }

    // ── Root ────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(MidForest, DeepForest, Color(0xFF040D08)),
                    radius = 1200f
                )
            )
            .pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center
    ) {

        // ── Decorative ambient ring (drawn behind content) ──────────────
        Spacer(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.Center)
                .drawBehind {
                    drawCircle(
                        color = WarnAmber.copy(alpha = glowAlpha * 0.18f),
                        radius = size.minDimension / 2
                    )
                    drawCircle(
                        color = WarnAmber.copy(alpha = glowAlpha * 0.08f),
                        radius = ringRadius.dp.toPx(),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
        )

        // ── Main card ───────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF152A1C).copy(alpha = 0.85f),
                            Color(0xFF0D1F13).copy(alpha = 0.92f)
                        )
                    )
                )
                .padding(36.dp)
        ) {

            // ── Pulsing warning icon ─────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .background(
                        Brush.radialGradient(
                            listOf(WarnAmber.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            ) {
                // Outer glow ring
                Spacer(
                    modifier = Modifier
                        .size(84.dp)
                        .drawBehind {
                            // Background
                            drawCircle(WarnAmber.copy(alpha = glowAlpha * 0.15f))
                            // Stroke
                            drawCircle(
                                color = WarnAmber.copy(alpha = glowAlpha * 0.5f),
                                style = Stroke(2.5.dp.toPx())
                            )
                        }
                )
                Text(text = "🌲", fontSize = 44.sp)
            }

            Spacer(Modifier.height(24.dp))

            // ── Headline ─────────────────────────────────────────────
            Text(
                text = "Your tree is withering",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    shadow = Shadow(
                        color = WarnAmber.copy(alpha = 0.4f),
                        offset = Offset(0f, 0f),
                        blurRadius = 12f
                    )
                )
            )

            Spacer(Modifier.height(10.dp))

            // ── Subtitle ─────────────────────────────────────────────
            Text(
                text = "You left the app.\nEvery second away drains your tree's life.",
                color = TextMuted,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            // ── Progress / urgency bar ───────────────────────────────
            UrgencyBar(shimmerOffset = {shimmerOffset})

            Spacer(Modifier.height(28.dp))

            // ── Primary action ───────────────────────────────────────
            Button(
                onClick = { /* No-op: the app will detect return */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LeafGreen
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "↩  Return to Focus",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A1A0F)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Give-up flow (2-step confirm) ────────────────────────
            if (!showGiveUpConfirm) {
                TextButton(onClick = { showGiveUpConfirm = true }) {
                    Text(
                        text = "I give up…",
                        color = TextMuted.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            } else {
                // Confirmation state
                GiveUpConfirmation(
                    onCancel = { showGiveUpConfirm = false },
                    onConfirm = onGiveUp
                )
            }
        }
    }
}


@Composable
private fun UrgencyBar(shimmerOffset: ()->Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tree Health", color = TextMuted, fontSize = 12.sp)
            Text("Draining…", color = WarnAmber, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(6.dp))

        val healthAnim = rememberInfiniteTransition(label = "health")
        val healthFraction by healthAnim.animateFloat(
            initialValue = 0.65f, targetValue = 0.42f,
            animationSpec = infiniteRepeatable(
                tween(4000, easing = LinearEasing), RepeatMode.Reverse
            ), label = "healthFraction"
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)) // Clip applies to the drawing bounds
        ) {
            // 1. Draw Track
            drawRect(
                color = Color.White.copy(alpha = 0.06f),
                size = size
            )

            // 2. Draw Progress
            // We read healthFraction HERE. This limits invalidation to the Draw Scope only.
            val currentWidth = size.width * healthFraction
            val shimmerVal = shimmerOffset()

            // Dynamic Gradient based on animation
            val brush = Brush.horizontalGradient(
                colors = listOf(
                    LeafGreen.copy(alpha = 0.6f),
                    WarnAmber.copy(alpha = 0.8f + shimmerVal * 0.2f),
                    WarnRed.copy(alpha = 0.9f)
                ),
                startX = 0f,
                endX = currentWidth
            )

            drawRoundRect(
                brush = brush,
                size = Size(width = currentWidth, height = size.height),
                cornerRadius = CornerRadius(100f, 100f) // Fully rounded
            )
        }
    }
}


@Composable
private fun GiveUpConfirmation(onCancel: () -> Unit, onConfirm: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(WarnRed.copy(alpha = 0.12f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "This will kill your tree permanently.",
            color = WarnRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
            ) {
                Text("Cancel", fontSize = 13.sp)
            }
            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarnRed)
            ) {
                Text("Kill Tree 🪓", fontSize = 13.sp, color = Color.White)
            }
        }
    }
}
