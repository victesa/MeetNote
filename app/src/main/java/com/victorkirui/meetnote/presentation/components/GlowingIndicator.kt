package com.victorkirui.meetnote.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlowingIndicator(
    color: Color = Color(0xFF4CAF50), // Standard online green
    dotSize: Dp = 12.dp,
    glowRadius: Dp = 8.dp
) {
    // 1. Set up the infinite pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "GlowTransition")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowScale"
    )

    Canvas(modifier = Modifier.size(dotSize)) {
        // 2. Draw the "Glow" (Blur effect)
        // We use drawIntoCanvas to access the native framework for blurring
        val paint = Paint().asFrameworkPaint().apply {
            this.color = color.copy(alpha = alpha).toArgb()
            this.maskFilter = android.graphics.BlurMaskFilter(
                glowRadius.toPx(),
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawCircle(
                size.width / 2,
                size.height / 2,
                (dotSize.toPx() / 2) * scale,
                paint
            )
        }

        // 3. Draw the solid center dot
        drawCircle(
            color = color,
            radius = dotSize.toPx() / 2
        )
    }
}


@Preview
@Composable
private fun GlowingIndicatorComponent(){
    GlowingIndicator()
}