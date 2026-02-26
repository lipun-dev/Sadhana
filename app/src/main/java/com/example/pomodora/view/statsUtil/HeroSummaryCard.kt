package com.example.pomodora.view.statsUtil

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodora.ui.theme.AccentGreen
import com.example.pomodora.ui.theme.CardBorder
import com.example.pomodora.ui.theme.CardSurface
import com.example.pomodora.ui.theme.EmptyCell
import com.example.pomodora.ui.theme.GlowGreen
import com.example.pomodora.ui.theme.TextPrimary
import com.example.pomodora.ui.theme.TextSecondary

@Composable
fun HeroSummaryCard(
    todaysMinutes: Int
) {

    val hours = todaysMinutes / 60
    val mins = todaysMinutes % 60
    val timeDisplay = if (hours > 0) "${hours}h ${mins}m" else "0h${mins}m"

    val targetProgress = (todaysMinutes.toFloat() / 240).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "HeroProgress"
    )


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(CardSurface)
            .border(1.dp, CardBorder, RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        // Background glow blob
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-30).dp)
                .blur(60.dp)
                .background(GlowGreen.copy(alpha = 0.06f), CircleShape)
        )

        Column {
            Text(
                "Today's Focus",
                color = TextSecondary,
                fontSize = 12.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    timeDisplay,
                    color = TextPrimary,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
                Text(
                    "/ 4h goal",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            ) {
                val cornerRadius = CornerRadius(50f, 50f)

                // Draw background track
                drawRoundRect(
                    color = EmptyCell,
                    size = size,
                    cornerRadius = cornerRadius
                )

                // Draw animated foreground gradient
                if (animatedProgress > 0f) {
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(AccentGreen, GlowGreen)),
                        size = Size(width = size.width * animatedProgress, height = size.height),
                        cornerRadius = cornerRadius
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick stats inline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatChip("25min Focus", GlowGreen, Modifier.weight(1f))
                QuickStatChip("☕ 5min Break", Color(0xFF60A5FA), Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun QuickStatChip(label: String, accentColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}