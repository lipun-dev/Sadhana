package com.example.pomodora.view.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.pomodora.services.TimerState
import com.example.pomodora.view.FocusUtil.FullTreeAnimation
import com.example.pomodora.view.FocusUtil.HandTreeAnimation
import com.example.pomodora.view.FocusUtil.SaplingAnimation

@Composable
fun GrowingTreeIcon(timerState: TimerState) {

    val animationPhase = remember(timerState) {
        when(timerState) {
            is TimerState.WaitingForBreak,
            is TimerState.OnBreak  -> TreeAnimPhase.COMPLETE
            is TimerState.Focusing -> TreeAnimPhase.GROWING
            else                   -> TreeAnimPhase.IDLE
        }
    }

    AnimatedContent(
        targetState = animationPhase,
        transitionSpec = {
            fadeIn(tween(600)) togetherWith fadeOut(tween(400))
        },
        label = "TreePhaseSwitch"
    ) { phase ->
        when (phase) {
            TreeAnimPhase.IDLE    -> HandTreeAnimation()
            TreeAnimPhase.GROWING -> SaplingAnimation()
            TreeAnimPhase.COMPLETE -> FullTreeAnimation()
        }
    }//Circle
}

enum class TreeAnimPhase { IDLE, GROWING, COMPLETE }