package com.example.pomodora.view.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pomodora.model.FocusEntry
import com.example.pomodora.ui.theme.AppBackground
import com.example.pomodora.ui.theme.GlowGreen
import com.example.pomodora.ui.theme.TextPrimary
import com.example.pomodora.ui.theme.TextSecondary
import com.example.pomodora.view.statsUtil.BottomStatsRow
import com.example.pomodora.view.statsUtil.HeroSummaryCard
import com.example.pomodora.view.statsUtil.WeeklyFocusCard
import com.example.pomodora.view.statsUtil.YearlyHeatmapCard
import com.example.pomodora.viewModel.StatsViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(viewModel: StatsViewModel) {

    val weeklyEntries by viewModel.weeklyBarEntries.collectAsStateWithLifecycle()
    val yearlyEntries by viewModel.heatmapEntries.collectAsStateWithLifecycle()

    val dummyWeekly = listOf(
        FocusEntry(LocalDate.now().minusDays(4), 250),
        FocusEntry(LocalDate.now().minusDays(3), 90),
        FocusEntry(LocalDate.now().minusDays(2), 150),
        FocusEntry(LocalDate.now().minusDays(1), 0),
        FocusEntry(LocalDate.now(), 180), // Friday Highlight
        FocusEntry(LocalDate.now().plusDays(1), 45),
        FocusEntry(LocalDate.now().plusDays(2), 210)
    )

    // Trigger data fetch (assuming current year for demo)
    LaunchedEffect(Unit) {
        viewModel.fetchStats(LocalDate.now().year.toString())
    }

    val todaysMinutes = remember(weeklyEntries) {
        val today = LocalDate.now()
        weeklyEntries.find { it.date == today }?.minutes ?: 0
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // 2. Main Layout
    Scaffold(
        containerColor = AppBackground,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                // 3. Pass scrollBehavior to TopAppBar
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBackground,
                    scrolledContainerColor = AppBackground
                ),
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Row {
                            Icon(Icons.Filled.Analytics, contentDescription = "Back", tint = GlowGreen)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Focus Stats",
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            LocalDate.now().year.toString(),
                            color = TextSecondary,
                            fontSize = 12.sp,
                            letterSpacing = 1.5.sp
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        PulsingDot()
                        Text("LIVE", color = GlowGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                },
                windowInsets = WindowInsets(0,0,0,0),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── Hero Summary ──
            HeroSummaryCard(todaysMinutes)

            // ── Weekly Bar Chart ──
            WeeklyFocusCard(entries = weeklyEntries)

            // ── Yearly Heatmap ──
            YearlyHeatmapCard(entries = yearlyEntries, year = LocalDate.now().year)

            // ── Bottom Stats Row ──
            BottomStatsRow()

            Spacer(modifier = Modifier.height(50.dp))
        }
    }


}


@Composable
fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .graphicsLayer { this.alpha = alpha }
            .clip(CircleShape)
            .background(GlowGreen)
    )
}