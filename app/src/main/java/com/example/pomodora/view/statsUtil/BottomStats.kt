package com.example.pomodora.view.statsUtil

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodora.ui.theme.CardSurface
import com.example.pomodora.ui.theme.CoralAccent
import com.example.pomodora.ui.theme.EmptyCell
import com.example.pomodora.ui.theme.GlowGreen
import com.example.pomodora.ui.theme.GoldAccent
import com.example.pomodora.ui.theme.TextHint
import com.example.pomodora.ui.theme.TextPrimary
import com.example.pomodora.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun BottomStatsRow() {
    // 1. Define your focus/forest-themed motivational quotes
    val quotes = listOf(
        "Every minute of focus plants a seed for your future.",
        "Great forests grow from single saplings. Keep going!",
        "Protect your time. Stay focused, stay present.",
        "Small daily streaks build massive results.",
        "Deep work is your superpower."
    )

    // 2. State to hold the current quote index
    var currentQuoteIndex by remember { mutableIntStateOf(0) }

    // 3. Automatically cycle through quotes every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000L)
            currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
        }
    }

    // Wrap the original Row and the new Quote Banner in a Column
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- ORIGINAL STATS ROW ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMiniCard(
                modifier    = Modifier.weight(1f),
                emoji       = "⏱",
                title       = "Total Hours",
                value       = "450",
                unit        = "hrs",
                subtitle    = "Since Jan 2023",
                accentColor = GoldAccent, // Assuming you have this defined
                progress    = 0.72f
            )
            StatMiniCard(
                modifier    = Modifier.weight(1f),
                emoji       = "🌱",
                title       = "Trees Planted",
                value       = "1.2K",
                unit        = "",
                subtitle    = "Eco Impact",
                accentColor = GlowGreen, // Assuming you have this defined
                progress    = 0.85f
            )
            StatMiniCard(
                modifier    = Modifier.weight(1f),
                emoji       = "🔥",
                title       = "Streak",
                value       = "42",
                unit        = "days",
                subtitle    = "Personal best!",
                accentColor = CoralAccent, // Assuming you have this defined
                progress    = 0.60f
            )
        }

        // --- NEW ANIMATED MOTIVATIONAL BANNER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardSurface) // Matches your stat cards
                .border(1.dp, GlowGreen.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = quotes[currentQuoteIndex],
                transitionSpec = {
                    // Slides up from the bottom while fading in, pushes old text up and fades out
                    (slideInVertically(animationSpec = tween(600)) { height -> height } + fadeIn(animationSpec = tween(600)))
                        .togetherWith(
                            slideOutVertically(animationSpec = tween(600)) { height -> -height } + fadeOut(animationSpec = tween(600))
                        )
                },
                label = "quote_animation"
            ) { targetQuote ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "✨",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = targetQuote,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun StatMiniCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    value: String,
    unit: String,
    subtitle: String,
    accentColor: Color,
    progress: Float
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .border(1.dp, accentColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        // Emoji icon in a pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(emoji, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(title, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.3.sp)
        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(3.dp))
                Text(unit, color = accentColor, fontSize = 10.sp, modifier = Modifier.padding(bottom = 4.dp),
                    fontWeight = FontWeight.SemiBold)
            }
        }

        Text(subtitle, color = TextHint, fontSize = 9.sp)

        Spacer(modifier = Modifier.height(12.dp))

        // Mini progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(EmptyCell)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(accentColor.copy(0.7f), accentColor)))
            )
        }
    }
}