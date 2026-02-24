package com.example.pomodora.view.statsUtil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodora.ui.theme.AccentGreen
import com.example.pomodora.ui.theme.CardBorder
import com.example.pomodora.ui.theme.CardSurface
import com.example.pomodora.ui.theme.CoralAccent
import com.example.pomodora.ui.theme.EmptyCell
import com.example.pomodora.ui.theme.GlowGreen
import com.example.pomodora.ui.theme.TextPrimary
import com.example.pomodora.ui.theme.TextSecondary

@Composable
fun HeroSummaryCard() {
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
                    "3h 30m",
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(EmptyCell)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.875f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(listOf(AccentGreen, GlowGreen))
                        )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick stats inline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatChip("🔥 42 day streak", CoralAccent, Modifier.weight(1f))
                QuickStatChip("⚡ 87% efficiency", GlowGreen, Modifier.weight(1f))
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