package com.example.pomodora.view.wavyAnimation

import android.graphics.BitmapShader
import android.graphics.Matrix
import android.graphics.Shader
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin


// --- 2. The Wave Component ---
@Composable
fun WavesLoadingIndicator(modifier: Modifier, color: Color, progress: Float) {
    BoxWithConstraints(modifier = modifier.offset(y = 16.dp), contentAlignment = Alignment.Center) {
        val constraintsWidth = maxWidth
        val constraintsHeight = maxHeight
        val density = LocalDensity.current

        val wavesShader by produceState<Shader?>(initialValue = null, constraintsHeight, constraintsWidth, color, density) {
            value = withContext(Dispatchers.Default) {
                createWavesShader(
                    width = with(density) { constraintsWidth.roundToPx() },
                    height = with(density) { constraintsHeight.roundToPx() },
                    color = color
                )
            }
        }

        if (progress > 0f && wavesShader != null) {
            WavesOnCanvas(shader = wavesShader!!, progress = progress)
        }
    }
}

// --- 3. Drawing Logic (Internal) ---
@Composable
private fun WavesOnCanvas(shader: Shader, progress: Float) {
    val matrix = remember { Matrix() }
    val paint = remember(shader) {
        Paint().apply {
            isAntiAlias = true
            this.shader = shader
        }
    }

    val transition = rememberInfiniteTransition(label = "wave")
    val waveShiftRatio by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing)), label = "shift"
    )
    val amplitudeRatio by transition.animateFloat(
        initialValue = 0.005f, targetValue = 0.015f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutLinearInEasing), RepeatMode.Reverse), label = "amp"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas {
            val height = size.height
            val width = size.width
            matrix.setScale(1f, amplitudeRatio / 0.09f, 0f, 0.5f * height)
            matrix.postTranslate(waveShiftRatio * width, (0.5f - progress) * height)
            shader.setLocalMatrix(matrix)
            it.drawRect(0f, 0f, width, height, paint)
        }
    }
}

private fun createWavesShader(width: Int, height: Int, color: Color): Shader {
    val bitmap = ImageBitmap(width, height, ImageBitmapConfig.Argb8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply { strokeWidth = 2f; isAntiAlias = true }
    val angularFrequency = 2f * PI / width
    val amplitude = height * 0.09f
    val waterLevel = height * 0.5f

    paint.color = color.copy(alpha = 0.3f)
    for (x in 0..width) {
        val y = waterLevel + amplitude * sin(x * angularFrequency).toFloat()
        canvas.drawLine(Offset(x.toFloat(), y), Offset(x.toFloat(), height.toFloat()), paint)
    }

    paint.color = color
    for (x in 0..width) {
        val y = waterLevel + amplitude * sin((x) * angularFrequency).toFloat()
        // Shift second wave slightly
        canvas.drawLine(Offset(x.toFloat(), y), Offset(x.toFloat(), height.toFloat()), paint)
    }
    return BitmapShader(bitmap.asAndroidBitmap(), Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
}