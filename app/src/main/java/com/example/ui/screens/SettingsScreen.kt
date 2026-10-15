package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.RudraXFooter
import com.example.ui.components.RudraXHeader
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorderMuted
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var sampleDurationSeconds by remember { mutableIntStateOf(6) }
    var highPrecisionRadar by remember { mutableStateOf(true) }
    var soundFeedbackEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "Settings",
                subtitle = "Telemetry & Radar Configuration",
                showBackButton = true,
                onBackClick = onNavigateBack,
                onAboutClick = { onNavigate(AppScreen.ABOUT) },
                onMenuSelect = { onNavigate(it) }
            )
        },
        containerColor = PitchBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            GlassCard(borderColor = CyanNeon) {
                Column {
                    Text(
                        text = "ROOM SAMPLING ENGINE",
                        fontSize = 11.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = CyanBright,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Room Test Duration", fontSize = 13.sp, color = TextPrimary)
                            Text("Duration for multi-sample RSSI average", fontSize = 11.sp, color = TextMuted)
                        }
                        Text("$sampleDurationSeconds Seconds", fontSize = 13.sp, color = CyanNeon, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(4, 6, 8, 10).forEach { dur ->
                            OutlinedButton(
                                onClick = { sampleDurationSeconds = dur },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (sampleDurationSeconds == dur) CyanNeon else GlassBorderMuted
                                ),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text("${dur}s", color = if (sampleDurationSeconds == dur) CyanNeon else TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            GlassCard(borderColor = ElectricBlue) {
                Column {
                    Text(
                        text = "3D RADAR DISPLAY",
                        fontSize = 11.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = ElectricBlue,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("High Precision Sweep Beam", fontSize = 13.sp, color = TextPrimary)
                            Text("Render smooth rotating holographic beam", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = highPrecisionRadar,
                            onCheckedChange = { highPrecisionRadar = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanNeon, checkedTrackColor = ElectricBlue)
                        )
                    }
                }
            }

            GlassCard {
                Column {
                    Text(
                        text = "SIGNAL THRESHOLD SPECIFICATION",
                        fontSize = 11.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = CyanBright,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("• -30 to -50 dBm: Excellent (Fastest)", fontSize = 11.sp, color = Color(0xFF00E5FF))
                    Text("• -51 to -60 dBm: Good (Optimal)", fontSize = 11.sp, color = Color(0xFF00E676))
                    Text("• -61 to -67 dBm: Fair (Acceptable)", fontSize = 11.sp, color = Color(0xFFFFD600))
                    Text("• -68 to -75 dBm: Weak (Degraded)", fontSize = 11.sp, color = Color(0xFFFF9100))
                    Text("• Below -75 dBm: Very Weak (High Latency)", fontSize = 11.sp, color = Color(0xFFFF1744))
                }
            }

            RudraXFooter()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
