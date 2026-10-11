package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val speed: Float,
    val angle: Double,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float,
    val shapeType: Int // 0: rect, 1: circle
)

@Composable
fun ConfettiCelebration(
    triggerKey: Long,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (triggerKey == 0L) return

    val progress = remember(triggerKey) { Animatable(0f) }

    val particles = remember(triggerKey) {
        val colors = listOf(
            Color(0xFF10B981), // Emerald
            Color(0xFF34D399), // Emerald light
            Color(0xFF8B5CF6), // Violet
            Color(0xFFA78BFA), // Violet light
            Color(0xFF6366F1), // Indigo
            Color(0xFFF59E0B), // Amber
            Color(0xFFF43F5E), // Rose
            Color(0xFF06B6D4)  // Cyan
        )
        List(60) {
            val angle = Random.nextDouble(0.0, Math.PI * 2)
            val speed = Random.nextFloat() * 700f + 300f
            ConfettiParticle(
                initialX = 0.5f,
                initialY = 0.45f,
                speed = speed,
                angle = angle,
                color = colors[Random.nextInt(colors.size)],
                size = Random.nextFloat() * 14f + 8f,
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                shapeType = Random.nextInt(2)
            )
        }
    }

    LaunchedEffect(triggerKey) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
        )
        onAnimationEnd()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = progress.value
        if (t >= 1f) return@Canvas

        val canvasW = size.width
        val canvasH = size.height

        particles.forEach { p ->
            val dist = p.speed * t
            val gravity = 900f * t * t // downward pull

            val currentX = (p.initialX * canvasW) + (cos(p.angle).toFloat() * dist)
            val currentY = (p.initialY * canvasH) + (sin(p.angle).toFloat() * dist) + gravity

            val alpha = (1f - (t * 1.1f)).coerceIn(0f, 1f)
            val rotation = p.rotationSpeed * t

            rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
                if (p.shapeType == 0) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(currentX - p.size / 2, currentY - p.size / 2),
                        size = Size(p.size, p.size * 0.6f)
                    )
                } else {
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.size / 2.5f,
                        center = Offset(currentX, currentY)
                    )
                }
            }
        }
    }
}
