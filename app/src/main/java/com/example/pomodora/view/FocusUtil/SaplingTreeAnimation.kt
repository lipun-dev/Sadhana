package com.example.pomodora.view.FocusUtil

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.example.pomodora.ui.theme.CardSurface
import com.example.pomodora.ui.theme.GlowGreen

@Composable
fun SaplingAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("sapling_planted_animation.json")
    )

    // Track whether the one-shot animation has finished

    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,              // Play only ONCE
        isPlaying = true,
        speed = 0.8f,
        restartOnPlay = false
    )

    val trunkColor = GlowGreen.toArgb()
    val dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR,
                value = CardSurface.toArgb(),
                keyPath = arrayOf("Layer 6 Outlines", "Group 1", "Fill 1")
            ),

            // ── Layer 3 Outlines — trunk ───────────────────────────────────────
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR,
                value = trunkColor,
                keyPath = arrayOf("Layer 3 Outlines", "Group 1", "Fill 1")
            ),

            // ── Layer 4 Outlines — branch ──────────────────────────────────────
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR,
                value = trunkColor,
                keyPath = arrayOf("Layer 4 Outlines", "Group 1", "Fill 1")
            ),

            // ── Layer 5 Outlines — branch ──────────────────────────────────────
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR,
                value = trunkColor,
                keyPath = arrayOf("Layer 5 Outlines", "Group 1", "Fill 1")
            )
    )


    LottieAnimation(
        composition = composition,
        // If finished, lock to last frame (1f). Otherwise follow playback.
        progress = { lottieProgress },
        modifier = modifier.size(250.dp),
        contentScale = ContentScale.Fit,
        dynamicProperties = dynamicProperties

    )
}




