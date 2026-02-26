package com.example.pomodora.view.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodora.services.PermissionHelper
import com.example.pomodora.services.TimerState
import com.example.pomodora.ui.theme.FocusAccentGreen
import com.example.pomodora.ui.theme.FocusBgDeep
import com.example.pomodora.ui.theme.FocusBreakBlue
import com.example.pomodora.ui.theme.GlowGreen
import com.example.pomodora.ui.theme.TextPrimary
import com.example.pomodora.view.FocusUtil.MorphingActionButton
import com.example.pomodora.view.FocusUtil.PolishedPermissionCard
import com.example.pomodora.view.FocusUtil.PolishedTimerDisplay
import com.example.pomodora.view.FocusUtil.PolishedWaveTimer
import com.example.pomodora.view.FocusUtil.SessionProgressDots
import com.example.pomodora.view.utils.GrowingTreeIcon
import com.example.pomodora.viewModel.FocusViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: FocusViewModel = viewModel<FocusViewModel>()
) {
    val timerState by viewModel.timerState.collectAsState()
    val timeLeft   = viewModel.timeLeft.collectAsState()
    val context    = LocalContext.current

    var hasPermissions by remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isCurrentlyGranted = PermissionHelper.hasUsageStatsPermission(context) &&
                        PermissionHelper.hasOverlayPermission(context)

                // 2. Only update state (and trigger recomposition) if it actually changed
                if (hasPermissions != isCurrentlyGranted) {
                    hasPermissions = isCurrentlyGranted
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }


    val isFocusing = timerState is TimerState.Focusing
    val isOnBreak  = timerState is TimerState.OnBreak

    // Ambient ring color transitions between states
    val ringColor by animateColorAsState(
        targetValue = when {
            isOnBreak  -> FocusBreakBlue
            isFocusing -> GlowGreen
            else       -> FocusAccentGreen
        },
        animationSpec = tween(600),
        label = "RingColor"
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = FocusBgDeep,
        topBar = { FocusTopAppBar(timerState = timerState,
            scrollBehavior) }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AmbientGlow(color = ringColor, isFocusing = isFocusing || isOnBreak)

            // CHANGE 2: verticalScroll — content never hidden behind bottom nav.
            // Bottom padding 100.dp clears any system nav bar / bottom nav height.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimerSection(
                    timeLeftProvider = { timeLeft.value },
                    timerState = timerState,
                    ringColor = ringColor
                )
                Spacer(modifier = Modifier.height(20.dp))

                // CHANGE 3: Single morphing button replaces two-button row
                AnimatedContent(
                    targetState = hasPermissions,
                    transitionSpec = {
                        fadeIn(tween(400)) + slideInVertically { it / 4 } togetherWith
                                fadeOut(tween(200))
                    },
                    label = "PermissionSwitch"
                ) { hasPerm ->
                    if (hasPerm) {

                        MorphingActionButton(
                            timerState  = timerState,
                            onStart     = { viewModel.startFocus() },
                            onGiveUp    = { viewModel.giveUp() },
                            onTakeBreak = { viewModel.startBreak() }
                        )
                    } else {
                        // CHANGE 3: ONE MorphingActionButton for all states
                        PolishedPermissionCard {
                            if (!PermissionHelper.hasUsageStatsPermission(context))
                                PermissionHelper.requestUsageStatsPermission(context)
                            else if (!PermissionHelper.hasOverlayPermission(context))
                                PermissionHelper.requestOverlayPermission(context)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimerSection(
    timeLeftProvider: () -> Long,
    timerState: TimerState,
    ringColor: Color
) {
    // This is the ONLY scope that recomposes every second
    val rawTimeLeft = timeLeftProvider()

    val totalFocusTime = 25 * 60 * 1000L
    val totalBreakTime = 5  * 60 * 1000L

    // FIX: Provide the correct default times if the timer hasn't started yet
    val effectiveTime = when {
        rawTimeLeft > 0L -> rawTimeLeft
        timerState is TimerState.OnBreak || timerState is TimerState.WaitingForBreak -> totalBreakTime
        timerState is TimerState.Idle -> totalFocusTime // Idle state defaults to 25 mins
        else -> totalFocusTime
    }

    val progress = remember(effectiveTime, timerState) {
        val total = if (timerState is TimerState.OnBreak || timerState is TimerState.WaitingForBreak) totalBreakTime else totalFocusTime
        val elapsed = total - effectiveTime
        (elapsed.toFloat() / total).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300, easing = LinearEasing),
        label = "RingProgress"
    )

    val isFocusing = timerState is TimerState.Focusing

    // Timer ring
    Box(contentAlignment = Alignment.Center) {
        PulseRings(color = ringColor, active = isFocusing)
        PolishedWaveTimer(
            progress   = { animatedProgress },
            ringColor  = ringColor,
            timerState = timerState
        )
        // Assuming GrowingTreeIcon is defined in your project
        GrowingTreeIcon(timerState = timerState)
    }

    Spacer(modifier = Modifier.height(30.dp))

    PolishedTimerDisplay(
        timeString = { formatTime(effectiveTime) }, // Pass the fixed time here
        timerState = timerState,
        ringColor  = { ringColor }
    )

    Spacer(modifier = Modifier.height(20.dp))

    SessionProgressDots(progress = { animatedProgress }, color = ringColor)
}





@Composable
fun AmbientGlow(color: Color, isFocusing: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "AmbientPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.04f,
        targetValue  = if (isFocusing) 0.10f else 0.06f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AmbientAlpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush  = Brush.radialGradient(
                colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                center = Offset(size.width / 2f, size.height * 0.38f),
                radius = size.width * 0.75f
            ),
            radius = size.width * 0.75f,
            center = Offset(size.width / 2f, size.height * 0.38f)
        )
    }
}

// ─── Concentric Pulse Rings ───────────────────────────────────────────────────

@Composable
fun PulseRings(color: Color, active: Boolean) {
    if (!active) return

    val infiniteTransition = rememberInfiniteTransition(label = "PulseRings")

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.82f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "Scale1"
    )
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.18f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "Alpha1"
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.82f, targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            tween(2400, delayMillis = 800, easing = FastOutSlowInEasing), RepeatMode.Restart
        ),
        label = "Scale2"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.12f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(2400, delayMillis = 800, easing = FastOutSlowInEasing), RepeatMode.Restart
        ),
        label = "Alpha2"
    )

    Canvas(modifier = Modifier.size(300.dp)) {
        // The code inside this block runs in the Draw phase.
        // Reading scale1, alpha1, etc., here will ONLY trigger a re-draw,
        // completely skipping recomposition!

        val center = Offset(size.width / 2, size.height / 2)
        val baseRadius = size.minDimension / 2

        // Draw Ring 1
        drawCircle(
            color = color,
            radius = baseRadius * scale1,
            center = center,
            alpha = alpha1,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Draw Ring 2
        drawCircle(
            color = color,
            radius = baseRadius * scale2,
            center = center,
            alpha = alpha2,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

// --- Helper Components ---

@OptIn( ExperimentalMaterial3Api::class)
@Composable
fun FocusTopAppBar(timerState: TimerState,scrollBehavior: TopAppBarScrollBehavior) {
    val titleText = when (timerState) {
        is TimerState.Focusing        -> "Deep Focus"
        is TimerState.OnBreak         -> "Rest & Recharge"
        is TimerState.WaitingForBreak -> "Session Complete"
        else                          -> "Ready to Plant"
    }


        TopAppBar(
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Row {
                        Icon(imageVector = Icons.Filled.Timer,contentDescription = null, tint = GlowGreen)
                        Spacer(modifier = Modifier.width(5.dp))
                        AnimatedContent(
                            targetState = titleText,
                            transitionSpec = {
                                (fadeIn(tween(350)) + slideInVertically { -it / 2 }) togetherWith
                                        (fadeOut(tween(200)) + slideOutVertically { it / 2 })
                            },
                            label = "AppBarTitle"
                        ) { title ->

                            Text(
                                text          = title,
                                color         = TextPrimary,
                                fontSize      = 18.sp,
                                fontWeight    = FontWeight.ExtraBold,
                                letterSpacing = (-0.3).sp,
                                textAlign     = TextAlign.Center
                            )
                        }

                    }
                    // CHANGE 1: AnimatedContent slides the title vertically on state change
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = FocusBgDeep,
                scrolledContainerColor = FocusBgDeep
            ),
            windowInsets = WindowInsets(0,0,0,0),
            scrollBehavior = scrollBehavior
        )
}

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}