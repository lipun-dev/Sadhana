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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.math.PI
import kotlin.math.sin


// --- 2. The Wave Component ---
@Composable
fun WavesLoadingIndicator(modifier: Modifier = Modifier, color: Color, progress: Float) {

    val transition = rememberInfiniteTransition(label = "wave")
    val waveShiftRatio by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing)),
        label = "shift"
    )
    val amplitudeRatio by transition.animateFloat(
        initialValue = 0.005f, targetValue = 0.015f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutLinearInEasing), RepeatMode.Reverse),
        label = "amp"
    )

    // 2. Use Spacer with drawWithCache instead of Canvas + State
    Spacer(
        modifier = modifier
            .drawWithCache {
                // A. Create Shader only when Size or Color changes
                val width = size.width.toInt()
                val height = size.height.toInt()
                val shader = createWavesShader(width, height, color)

                val paint = Paint().apply {
                    isAntiAlias = true
                    this.shader = shader
                }

                val matrix = Matrix()

                onDrawBehind {
                    // B. Read Animation Values HERE (Inside Draw Scope only)
                    // This prevents the Composable from recomposing, only repainting.

                    if (progress > 0f) {
                        matrix.reset()
                        // Scale Y based on amplitude
                        matrix.setScale(1f, amplitudeRatio / 0.09f, 0f, 0.5f * size.height)
                        // Translate X (movement) and Y (water level/progress)
                        matrix.postTranslate(
                            waveShiftRatio * size.width,
                            (0.5f - progress) * size.height
                        )
                        shader.setLocalMatrix(matrix)

                        drawIntoCanvas { canvas ->
                            canvas.drawRect(0f, 0f, size.width, size.height, paint)
                        }
                    }
                }
            }
    )
}
private fun createWavesShader(width: Int, height: Int, color: Color): Shader {

    if (width <= 0 || height <= 0) return BitmapShader(
        ImageBitmap(1, 1, ImageBitmapConfig.Argb8888).asAndroidBitmap(),
        Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
    )
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