package com.example.pomodora.view.wavyAnimation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WavyAuthScaffold(
    title: String,
    subtitle: String,
    screenState: AuthScreenState,
    onGoToDashboard: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()

    // Wave Logic:
    // We keep the wave relatively stable now so it acts as a beautiful header.
    // If loading/success, we flood the screen (0.35f), otherwise standard header (0.75f)
    val targetProgress = if (screenState == AuthScreenState.Loading || screenState == AuthScreenState.Success) 0.35f else 0.75f

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "WaveHeight"
    )

    val waveColor = MaterialTheme.colorScheme.onPrimary
    val backgroundColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            // IME Padding ensures the Box respects the keyboard height
    ) {
        // 1. The Background Wave
        // We use MatchParentSize so it fills the screen behind the scrollable content
        WavesLoadingIndicator(
            modifier = Modifier.matchParentSize(),
            color = waveColor,
            progress = animatedProgress
        )

        // 2. The Scrollable Content
        // We use a Column with verticalScroll to handle small screens + keyboard
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // SPACER: This pushes the content down.
            // We want the Title inside the Wave, but the Form BELOW the wave.
            Spacer(modifier = Modifier.height(100.dp))

            // --- HEADER SECTION (Inside Wave) ---
            AnimatedVisibility(visible = screenState != AuthScreenState.Success) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        // MADE LARGER
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = waveColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        // MADE LARGER
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Spacer to separate Header from Form (pushing form below the wave crest)
            Spacer(modifier = Modifier.height(80.dp))

            // --- BODY SECTION ---
            AnimatedContent(
                targetState = screenState,
                transitionSpec = {
                    fadeIn(tween(300)) + scaleIn(initialScale = 0.9f) togetherWith
                            fadeOut(tween(300))
                },
                label = "AuthContentTransition"
            ) { targetState ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (targetState) {
                        AuthScreenState.Input, AuthScreenState.Loading, AuthScreenState.Failed -> {

                            // Error Message
                            if (targetState == AuthScreenState.Failed) {
                                Text(
                                    text = "Login/SignUp Failed",
                                    color = MaterialTheme.colorScheme.errorContainer, // Better contrast on dark/wave
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // The Form Fields (Passed from parent)
                            Column(
                                modifier = Modifier.alpha(if (targetState == AuthScreenState.Loading) 0.5f else 1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                content()
                            }

                            // Extra space at bottom for scrolling comfortably past the button
                            Spacer(modifier = Modifier.height(200.dp))
                        }

                        AuthScreenState.Success -> {
                            // Success Screen logic (Same as before, just centered)
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 40.dp)) {
                                Text(
                                    text = "Login Success!",
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.height(48.dp))
                                Button(
                                    onClick = onGoToDashboard,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.height(50.dp)
                                ) {
                                    Text("Go to Dashboard", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}