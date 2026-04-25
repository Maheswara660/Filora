package com.maheswara660.filora.common.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomLoader(
    modifier: Modifier = Modifier,
    size: Dp = 45.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "dotSpinner")
        val speed = 900 // 0.9s

        for (i in 0 until 8) {
            val delay = (speed * (i.toFloat() / 8f)).toInt()
            
            val animationProgress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = speed, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = androidx.compose.animation.core.StartOffset(-delay)
                ),
                label = "dot$i"
            )

            // Pulse animation logic: 0% -> scale 0, opacity 0.5; 50% -> scale 1, opacity 1; 100% -> scale 0, opacity 0.5
            val pulseValue = if (animationProgress < 0.5f) {
                animationProgress * 2f
            } else {
                (1f - animationProgress) * 2f
            }

            Dot(
                rotation = i * 45f,
                scale = pulseValue,
                opacity = 0.5f + (pulseValue * 0.5f),
                color = color
            )
        }
    }
}

@Composable
private fun Dot(
    rotation: Float,
    scale: Float,
    opacity: Float,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = rotation
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(width = 8.dp, height = 8.dp) // ~20% of 45dp is 9dp
                .scale(scale)
                .alpha(opacity)
                .background(color, CircleShape)
        )
    }
}
