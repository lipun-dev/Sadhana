package com.example.pomodora.view.statsUtil

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodora.model.FocusEntry
import com.example.pomodora.ui.theme.CardBorder
import com.example.pomodora.ui.theme.CardSurface
import com.example.pomodora.ui.theme.GlowGreen
import com.example.pomodora.ui.theme.GoldAccent
import com.example.pomodora.ui.theme.GoldBarGradient
import com.example.pomodora.ui.theme.GreenBarGradient
import com.example.pomodora.ui.theme.MutedGreen
import com.example.pomodora.ui.theme.TextPrimary
import com.example.pomodora.ui.theme.TextSecondary
import java.time.LocalDate

@Composable
fun WeeklyFocusCard(entries: List<FocusEntry>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(CardSurface)
            .border(1.dp, CardBorder, RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Weekly Progress",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "Last 7 days · ${entries.sumOf { it.minutes / 60 }}h total",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            // Period chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MutedGreen.copy(alpha = 0.5f))
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("7D", color = GlowGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val maxMinutes = 240f
        val dayLabels = listOf("S","M", "T", "W", "T", "F", "S")
        val todayIndex = entries.indexOfFirst { it.date == LocalDate.now() }.coerceAtLeast(0)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            // 1. STATIC BACKGROUND: Grid lines via Canvas
            Canvas(modifier = Modifier.fillMaxSize().padding(start = 36.dp, bottom = 36.dp)) {
                val levels = 4
                val step = size.height / levels
                for (i in 0..levels) {
                    val y = step * i
                    drawLine(
                        color = Color(0xFF1A3526),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 12f))
                    )
                }
            }

            // 2. STATIC FOREGROUND: Y-axis labels
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 36.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("4h", "3h", "2h", "1h", "0h").forEach { label ->
                    Text(
                        text = label,
                        color = TextPrimary,
                        fontSize = 10.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(28.dp)
                    )
                }
            }

            // 3. SCROLLABLE CONTENT: Bars and X-axis labels inside LazyRow
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 36.dp), // Push past the Y-axis labels
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp) // Spacing between columns
            ) {
                itemsIndexed(entries) { index, entry ->
                    val isToday = index == todayIndex

                    // CRASH FIX & MIN HEIGHT:
                    // coerceIn(0.02f, 1f) ensures the fraction never exceeds 1f (preventing the >240 crash)
                    // and never drops below 0.02f (giving 0min entries a tiny visible bar)
                    val barFraction = (entry.minutes.toFloat() / maxMinutes).coerceIn(0.02f, 1f)

                    val barColor = when {
                        isToday -> GoldBarGradient
                        else -> GreenBarGradient
                    }

                    // Wrap each day's bar and label in a single Column
                    Column(
                        modifier = Modifier
                            .width(44.dp) // Ensures a consistent, wider hit-box for each day
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Bar Area (takes up all space above the 36.dp label area)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // Value label above bar
                            Text(
                                text = if (entry.minutes >= 60) "${entry.minutes / 60}h${if (entry.minutes % 60 != 0) "${entry.minutes % 60}" else ""}"
                                else "${entry.minutes}m",
                                color = if (isToday) GoldAccent else TextPrimary,
                                fontSize = 9.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Glow under bar
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),          // ← gives the Box a real height
                                contentAlignment = Alignment.BottomCenter) {
                                if (isToday) {
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp) // Glow scaled up for wider bar
                                            .fillMaxHeight(barFraction * 0.6f)
                                            .blur(20.dp)
                                            .background(GoldAccent.copy(alpha = 0.45f))
                                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    )
                                }
                                // Solid bar (Widened)
                                Box(
                                    modifier = Modifier
                                        .width(if (isToday) 40.dp else 30.dp) // Increased from 20/14
                                        .fillMaxHeight(barFraction)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(barColor)
                                )
                            }
                        }

                        // X-axis day label (Fixed 36.dp height to perfectly align with Canvas bottom padding)
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayLabels.getOrElse(index) { "?" },
                                color = if (isToday) GoldAccent else TextSecondary,
                                fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}