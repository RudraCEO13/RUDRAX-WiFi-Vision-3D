package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.ui.components.RadarVisualization3D
import com.example.ui.components.RudraXFooter
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepVoid
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private const val OFFICIAL_LOGO_URL = "https://rudraxstudio.freedev.app/RudraX-Technologies-Studio.jpg"

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnimations")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    LaunchedEffect(Unit) {
        delay(2600)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PitchBlack,
                        DeepVoid,
                        PitchBlack
                    )
                )
            )
            .clickable { onTimeout() }
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background 3D radar preview
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.Center)
        ) {
            RadarVisualization3D(
                rssi = -45,
                quality = com.example.data.model.SignalQuality.EXCELLENT,
                showSweep = true
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Official RudraX Technologies Studio Logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0D1527))
                    .border(2.dp, CyanNeon.copy(alpha = glowAlpha), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(OFFICIAL_LOGO_URL)
                        .crossfade(true)
                        .build(),
                    contentDescription = "RudraX Technologies Studio Official Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(6.dp),
                    loading = {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Loading Logo",
                            tint = CyanNeon,
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    error = {
                        // Clean fallback brand mark if image cannot load
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = "RudraX Fallback Mark",
                                tint = CyanNeon,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "RudraX",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanBright
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "RUDRAX WiFi Vision 3D",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "See Your Wi-Fi. Understand Your Space.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.5.sp
                ),
                color = CyanBright,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Progress bar
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CyanNeon,
                trackColor = Color(0x3300E5FF)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Initializing RF Spectrum Sensors...",
                fontSize = 11.sp,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onTimeout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x3300E5FF),
                    contentColor = CyanNeon
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.6f)),
                modifier = Modifier.testTag("splash_enter_button")
            ) {
                Text(
                    text = "Launch Scanner",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        RudraXFooter(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
