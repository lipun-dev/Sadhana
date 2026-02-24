package com.example.pomodora.view.statsUtil

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodora.model.FocusEntry
import com.example.pomodora.ui.theme.AccentGreen
import com.example.pomodora.ui.theme.CardBorder
import com.example.pomodora.ui.theme.CardSurface
import com.example.pomodora.ui.theme.GlowGreen
import com.example.pomodora.ui.theme.TextHint
import com.example.pomodora.ui.theme.TextPrimary
import com.example.pomodora.ui.theme.TextSecondary
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun YearlyHeatmapCard(entries: List<FocusEntry>,year: Int) {
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
                Text("Yearly Focus", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("$year contribution graph", color = TextSecondary, fontSize = 12.sp)
            }
            // Year badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlowGreen.copy(alpha = 0.08f))
                    .border(1.dp, GlowGreen.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("$year", color = GlowGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            // Day labels
            Column(
                modifier = Modifier.padding(top = 20.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                    Box(
                        modifier = Modifier.height(28.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = day,
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Scrollable months
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                val months = (1..12).map { YearMonth.of(year, it) }
                items(months.size) { index ->
                    MonthGridDark(yearMonth = months[index], allEntries = entries)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Less", fontSize = 10.sp, color = TextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            val legendColors = listOf(
                TextPrimary,
                Color(0xFF1A4D32),
                Color(0xFF237A50),
                AccentGreen,
                GlowGreen
            )
            legendColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                        .then(if (color == GlowGreen) Modifier.border(1.dp, GlowGreen.copy(0.5f), RoundedCornerShape(4.dp)) else Modifier)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("More", fontSize = 10.sp, color = TextPrimary)
        }
    }
}


@Composable
fun MonthGridDark(yearMonth: YearMonth, allEntries: List<FocusEntry>) {
    val firstDay     = yearMonth.atDay(1)
    val daysInMonth  = yearMonth.lengthOfMonth()
    val startOffset  = firstDay.dayOfWeek.value - 1
    val weeks        = (startOffset + daysInMonth + 6) / 7

    val cellSize = 28.dp
    val cellShape = RoundedCornerShape(6.dp)
    Column {
        Text(
            text = yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier
                .height(24.dp) // Fixed height to ensure perfect horizontal alignment with Y-axis labels
                .padding(bottom = 4.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (weekIndex in 0 until weeks) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (dayOfWeek in 0..6) {
                        val cellIndex = (weekIndex * 7) + dayOfWeek
                        val dayNumber = cellIndex - startOffset + 1

                        if (dayNumber in 1..daysInMonth) {
                            val date = yearMonth.atDay(dayNumber)
                            val minutes = allEntries.find { it.date == date }?.minutes
                                ?: 0

                            val cellColor = when {
                                minutes >= 90 -> GlowGreen
                                minutes >= 60 -> AccentGreen
                                minutes >= 25 -> Color(0xFF237A50)
                                minutes == 0  -> TextPrimary
                                else         -> TextPrimary
                            }

                            // Dynamic text color: Dark text on bright cells, light text on dark cells
                            val textColor = if (minutes > 60) Color(0xFF0A1F12) else TextHint.copy(alpha = 0.6f)

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .clip(cellShape)
                                    .background(cellColor)
                                    .then(
                                        if (minutes > 90)
                                            Modifier.border(1.dp, GlowGreen.copy(alpha = 0.6f), cellShape)
                                        else Modifier
                                    )
                                    // Added proper click handling modifier
                                    .clickable { /* Handle cell tap here */ },
                                contentAlignment = Alignment.Center
                            ) {
                                // Add the date number inside the cell
                                Text(
                                    text = dayNumber.toString(),
                                    color = textColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            // Blank spaces must also match the new cell size to keep the grid aligned
                            Spacer(modifier = Modifier.size(cellSize))
                        }
                    }
                }
            }
        }
    }
}
