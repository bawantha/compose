package com.bawantha.composifi

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TikTokLoadingSpinner(
    baseLength: Float,
    scaleRangeStep: Float,
    color1: Color,
    color2: Color
) {
    // Total animation duration in milliseconds
    val animationDuration = 800

    // Create an infinite transition for animations
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // Scaling animation for the ball size
    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 2F,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    // Translation animation for the ball position
    val translationAnimation by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 1F,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration / 2, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    // Normalize progress range to [-1, 1]
    val progress = scaleAnimation - 1F

    // Ball radius and translation factor
    val ballRadius = baseLength / 2
    val translationFactor = ballRadius * 2

    // Horizontal offsets for the balls
    val offsetX1 = translationFactor * translationAnimation
    val offsetX2 = -translationFactor * translationAnimation

    // Define scaling values for the balls
    val maxScale = 1F
    val midScale = maxScale - scaleRangeStep
    val minScale = maxScale - scaleRangeStep * 2

    // Calculate scaling factors for the two balls
    val scale1 = maxScale.calculateScale1(progress, minScale, midScale)
    val scale2 = maxScale.calculateScale2(progress, minScale, midScale)


    Box(modifier = Modifier.size((baseLength * 2).dp, baseLength.dp)) {
        val density = LocalDensity.current.density
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ballRadiusPx = ballRadius * density
            val blendMode = BlendMode.Multiply

            if (progress >= 0) {
                drawBall(color1, offsetX1, ballRadiusPx, scale1, blendMode)
            }
            drawBall(color2, 3 * ballRadius + offsetX2, ballRadiusPx, scale2, blendMode)
            if (progress < 0) {
                drawBall(color1, offsetX1, ballRadiusPx, scale1, blendMode)
            }
        }
    }
}

// Helper function to draw a ball with specified parameters
private fun DrawScope.drawBall(
    color: Color,
    offsetX: Float,
    ballRadiusPx: Float,
    scale: Float,
    blendMode: BlendMode
) {
    drawCircle(
        color = color,
        center = Offset(offsetX * density, ballRadiusPx),
        radius = ballRadiusPx * scale,
        blendMode = blendMode
    )
}

// Calculate scaling for the first ball
private fun Float.calculateScale1(progress: Float, minValue: Float, midValue: Float): Float {
    val gapMinMid = midValue - minValue
    val gapMidMax = this - midValue
    return when (progress) {
        in -1F..-0.6F -> midValue + (progress + 1F) / 0.4F * gapMidMax  // Gradually increase to maxValue
        in -0.6F..-0.4F -> this  // Hold at maxValue
        in -0.4F..0F -> this - (progress + 0.4F) / 0.4F * gapMidMax // Gradually decrease to midValue
        in 0F..0.4F -> midValue - progress / 0.4F * gapMinMid  // Gradually decrease to minValue
        in 0.4F..0.6F -> minValue  // Hold at minValue
        in 0.6F..1F -> minValue + (progress - 0.6F) / 0.4F * gapMinMid  // Gradually increase to midValue
        else -> midValue
    }
}

// Calculate scaling for the second ball
private fun Float.calculateScale2(progress: Float, minValue: Float, midValue: Float): Float {
    val gapMinMid = midValue - minValue
    val gapMidMax = this - midValue
    return when (progress) {
        in -1F..-0.6F -> midValue - (progress + 1F) / 0.4F * gapMinMid  // Gradually decrease to minValue
        in -0.6F..-0.4F -> minValue  // Hold at minValue
        in -0.4F..0F -> minValue + (progress + 0.4F) / 0.4F * gapMinMid // Gradually increase to midValue
        in 0F..0.4F -> midValue + progress / 0.4F * gapMidMax  // Gradually increase to maxValue
        in 0.4F..0.6F -> this  // Hold at maxValue
        in 0.6F..1F -> this - (progress - 0.6F) / 0.4F * gapMidMax  // Gradually decrease to midValue
        else -> midValue
    }
}

@Preview(showBackground = true)
@Composable
fun TikTokLoadingSpinnerPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TikTokLoadingSpinner(
            baseLength = 40F,
            scaleRangeStep = 0.2F,
            color1 = Color(0xFFCB2790),
            color2 = Color(0xFF6BDFE0)
        )
    }
}