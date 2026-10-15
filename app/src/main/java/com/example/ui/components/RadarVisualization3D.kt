package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.data.model.SignalQuality
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.SignalExcellent
import com.example.ui.theme.SignalFair
import com.example.ui.theme.SignalGood
import com.example.ui.theme.SignalVeryWeak
import com.example.ui.theme.SignalWeak
import com.example.ui.viewmodel.SpatialMarker
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarVisualization3D(
    rssi: Int,
    quality: SignalQuality,
    spatialMarkers: List<SpatialMarker> = emptyList(),
    modifier: Modifier = Modifier,
    is3DPerspective: Boolean = true,
    showSweep: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarAnimations")

    // Pulse wave 1
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseWave1"
    )

    // Pulse wave 2 (staggered)
    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, delayMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseWave2"
    )

    // Radar scanner sweep rotation angle (0 to 360 deg)
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    // Breathing glow on central signal core
    val coreGlow by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CoreGlow"
    )

    val signalColor = when (quality) {
        SignalQuality.EXCELLENT -> SignalExcellent
        SignalQuality.GOOD -> SignalGood
        SignalQuality.FAIR -> SignalFair
        SignalQuality.WEAK -> SignalWeak
        SignalQuality.VERY_WEAK -> SignalVeryWeak
        SignalQuality.UNAVAILABLE -> CyanNeon
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f
            val maxRadius = minOf(width, height) * 0.44f

            // 3D Perspective aspect ratio tilt (y squashed by 0.65 to simulate floor plane perspective)
            val ySquash = if (is3DPerspective) 0.68f else 1.0f

            // 1. Draw 3D Spatial Grid lines
            if (is3DPerspective) {
                val gridColor = Color(0x1A00E5FF)
                val gridSteps = 5
                for (i in -gridSteps..gridSteps) {
                    val x = centerX + (i * (maxRadius / gridSteps) * 1.3f)
                    drawLine(
                        color = gridColor,
                        start = Offset(x, centerY - maxRadius * ySquash),
                        end = Offset(x, centerY + maxRadius * ySquash),
                        strokeWidth = 1f
                    )
                }
                for (j in -3..3) {
                    val y = centerY + (j * (maxRadius * ySquash / 3))
                    drawLine(
                        color = gridColor,
                        start = Offset(centerX - maxRadius * 1.3f, y),
                        end = Offset(centerX + maxRadius * 1.3f, y),
                        strokeWidth = 1f
                    )
                }
            }

            // 2. Concentric Tilted Perspective Rings
            val ringCount = 4
            for (r in 1..ringCount) {
                val fraction = r.toFloat() / ringCount
                val rx = maxRadius * fraction
                val ry = rx * ySquash
                drawOval(
                    color = Color(0x3300E5FF),
                    topLeft = Offset(centerX - rx, centerY - ry),
                    size = Size(rx * 2, ry * 2),
                    style = Stroke(width = 1.5f)
                )
            }

            // 3. Dynamic Expanding Wavefronts (simulating measured RF radiation)
            listOf(pulse1, pulse2).forEach { pulse ->
                val waveRx = maxRadius * pulse
                val waveRy = waveRx * ySquash
                val waveAlpha = (1.0f - pulse).coerceIn(0.0f, 1.0f) * 0.7f
                drawOval(
                    color = signalColor.copy(alpha = waveAlpha),
                    topLeft = Offset(centerX - waveRx, centerY - waveRy),
                    size = Size(waveRx * 2, waveRy * 2),
                    style = Stroke(width = 3.5f * (1f - pulse * 0.5f))
                )
            }

            // 4. Rotating Holographic Sweep Beam
            if (showSweep) {
                rotate(sweepAngle, pivot = Offset(centerX, centerY)) {
                    val sweepPath = Path().apply {
                        moveTo(centerX, centerY)
                        val angleRad = -PI / 6.0 // 30-degree beam arc
                        val endX = centerX + maxRadius * cos(angleRad).toFloat()
                        val endY = centerY + (maxRadius * sin(angleRad).toFloat()) * ySquash
                        lineTo(endX, endY)
                        arcTo(
                            rect = androidx.compose.ui.geometry.Rect(
                                centerX - maxRadius,
                                centerY - maxRadius * ySquash,
                                centerX + maxRadius,
                                centerY + maxRadius * ySquash
                            ),
                            startAngleDegrees = -30f,
                            sweepAngleDegrees = 30f,
                            forceMoveTo = false
                        )
                        close()
                    }
                    drawPath(
                        path = sweepPath,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CyanNeon.copy(alpha = 0.35f),
                                ElectricBlue.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = Offset(centerX, centerY),
                            radius = maxRadius
                        )
                    )
                }
            }

            // 5. Central 3D Pulsing Signal Core
            val coreBaseRadius = 14.dp.toPx() * coreGlow
            // Outer aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        signalColor.copy(alpha = 0.8f),
                        signalColor.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = coreBaseRadius * 2.5f
                ),
                radius = coreBaseRadius * 2.5f,
                center = Offset(centerX, centerY)
            )
            // Core sphere
            drawCircle(
                color = signalColor,
                radius = coreBaseRadius,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = Color.White,
                radius = coreBaseRadius * 0.45f,
                center = Offset(centerX - coreBaseRadius * 0.2f, centerY - coreBaseRadius * 0.2f)
            )

            // 6. Draw Spatial Marker Beacons (placed by user in 3D AR mode)
            spatialMarkers.forEach { marker ->
                val markerX = marker.normalizedX * width
                val markerY = marker.normalizedY * height

                // Marker tether line to ground
                drawLine(
                    color = CyanBright.copy(alpha = 0.7f),
                    start = Offset(markerX, markerY),
                    end = Offset(markerX, markerY + 22.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )

                // Marker beacon orb
                drawCircle(
                    color = ElectricBlue,
                    radius = 8.dp.toPx(),
                    center = Offset(markerX, markerY)
                )
                drawCircle(
                    color = CyanBright,
                    radius = 4.dp.toPx(),
                    center = Offset(markerX, markerY)
                )
                drawCircle(
                    color = CyanNeon.copy(alpha = 0.4f),
                    radius = 14.dp.toPx(),
                    center = Offset(markerX, markerY),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}
