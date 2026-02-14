package com.example.pomodora.view.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pomodora.R
import com.example.pomodora.services.TimerState

@Composable
fun GrowingTreeIcon(progress: Float, timerState: TimerState) {
    // Determine which stage to show based on progress (1.0 down to 0.0)
    // We reverse progress so 1.0 (start) is stage 1, and near 0.0 (end) is stage 4
    val reverseProgress = 1f - progress

    val treeDrawableId = when {
        timerState is TimerState.WaitingForBreak -> R.drawable.ic_launcher_foreground // Success state
        reverseProgress < 0.25f -> R.drawable.ic_launcher_foreground
        reverseProgress < 0.50f -> R.drawable.ic_launcher_foreground
        reverseProgress < 0.75f -> R.drawable.ic_launcher_foreground
        else -> R.drawable.ic_launcher_foreground
    }

    // Fade animation when switching between tree stages
    AnimatedContent(
        targetState = treeDrawableId,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "TreeGrowthAnimation"
    ) { targetId ->
        Icon(
            painter = painterResource(id = targetId),
            contentDescription = "Growing Tree",
            // Tint the tree green, or use Color.Unspecified if you have colored PNGs
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(120.dp)
        )
    }
}