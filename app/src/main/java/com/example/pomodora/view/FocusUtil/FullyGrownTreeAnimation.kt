package com.example.pomodora.view.FocusUtil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.example.pomodora.ui.theme.CardSurface

@Composable
fun FullTreeAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("fully_grown_tree_new.json")
    )

    val animState by animateLottieCompositionAsState(
        composition  = composition,
        iterations   = 1,           // Play only ONCE
        isPlaying    = true,
        speed        = 1f,
        restartOnPlay = false        // Don't restart on recomposition
    )

    // Notify parent when animation finishes



    Box(
        modifier = modifier
            .size(200.dp)
            .clip(CircleShape)                      // ← contains the animation cleanly
            .background(CardSurface),         // ← matches dynamic bg color
        //   so no edge mismatch
        contentAlignment = Alignment.Center         // ← fixes the alignment issue
    ) {
        LottieAnimation(
            composition       = composition,
            progress          = { animState },
            modifier          = Modifier
                .fillMaxSize()
                .align(Alignment.Center),           // ← double-ensures centering
            // ← fills the box fully,
            //   no empty space around edges
        )
    }
}

@Composable
fun HandTreeAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("hand_with_tree.json") // ← your file name here
    )

    val lottieProgress by animateLottieCompositionAsState(
        composition  = composition,
        iterations   = LottieConstants.IterateForever,  // ← infinite loop
        isPlaying    = true,
        speed        = 1f,
        restartOnPlay = false
    )

    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value    = CardSurface.toArgb(),          // ← your desired color
            keyPath  = arrayOf("Circle", "**")
        )
    )

    LottieAnimation(
        composition  = composition,
        progress     = { lottieProgress },
        modifier     = modifier.size(250.dp),
        dynamicProperties = dynamicProperties
    )
}
