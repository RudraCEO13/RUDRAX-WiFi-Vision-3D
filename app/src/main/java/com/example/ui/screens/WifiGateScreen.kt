package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WifiConnectionDetails
import com.example.ui.components.GlassCard
import com.example.ui.components.RudraXFooter
import com.example.ui.components.RudraXHeader
import com.example.ui.components.SignalQualityBadge
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SignalGood
import com.example.ui.theme.SignalVeryWeak
import com.example.ui.theme.SignalWeak
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen

@Composable
fun WifiGateScreen(
    wifiDetails: WifiConnectionDetails,
    onRefresh: () -> Unit,
    onUnlockDashboard: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "RUDRAX WiFi Vision 3D",
                subtitle = "Connection Verification Gate",
                showBackButton = false,
                onAboutClick = { onNavigate(AppScreen.ABOUT) },
                onMenuSelect = { onNavigate(it) }
            )
        },
        bottomBar = {
            RudraXFooter()
        },
        containerColor = PitchBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            when {
                // STATE A: Wi-Fi Disabled
                !wifiDetails.isWifiEnabled -> {
                    WifiDisabledCard(
                        onOpenSettings = {
                            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        onRefresh = onRefresh
                    )
                }

                // STATE B: Wi-Fi Enabled but NOT Connected
                !wifiDetails.isConnected -> {
                    WifiDisconnectedCard(
                        onConnect = {
                            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        onRefresh = onRefresh
                    )
                }

                // STATE C: Wi-Fi Connected
                else -> {
                    WifiConnectedCard(
                        wifiDetails = wifiDetails,
                        onRefresh = onRefresh,
                        onUnlockDashboard = onUnlockDashboard
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Offline Accessible Tools (Saved Scans & About always accessible)
            GlassCard(
                borderColor = Color(0x332979FF)
            ) {
                Column {
                    Text(
                        text = "OFFLINE TOOLS & DOCUMENTATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanBright,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Previously saved room measurements, signal reports, and the RudraX reference manual remain accessible without an active Wi-Fi connection.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigate(AppScreen.SAVED_ROOMS) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("gate_saved_rooms_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Saved Scans",
                                tint = ElectricBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Saved Scans", color = TextPrimary, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { onNavigate(AppScreen.ABOUT) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("gate_about_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About",
                                tint = CyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("About & Help", color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun WifiDisabledCard(
    onOpenSettings: () -> Unit,
    onRefresh: () -> Unit
) {
    GlassCard(
        borderColor = SignalVeryWeak.copy(alpha = 0.6f)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = "Wi-Fi Turned Off",
                tint = SignalVeryWeak,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Wi-Fi is turned off.",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The physical Wi-Fi radio is currently disabled on this device. Enable Wi-Fi to measure RF signal strength and analyze your space in 3D.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onOpenSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("gate_open_wifi_settings_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open Wi-Fi Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("gate_check_again_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = CyanBright,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Check Connection Again", color = TextPrimary)
            }
        }
    }
}

@Composable
private fun WifiDisconnectedCard(
    onConnect: () -> Unit,
    onRefresh: () -> Unit
) {
    GlassCard(
        borderColor = SignalWeak.copy(alpha = 0.6f)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "No Network Connected",
                tint = SignalWeak,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "No Wi-Fi network connected.",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Wi-Fi is turned on, but your device is not connected to any wireless access point. Connect to a Wi-Fi network to begin live 3D signal telemetry.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onConnect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("gate_connect_wifi_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = TextPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Connect to Wi-Fi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("gate_refresh_status_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = CyanBright,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh Connection Status", color = TextPrimary)
            }
        }
    }
}

@Composable
private fun WifiConnectedCard(
    wifiDetails: WifiConnectionDetails,
    onRefresh: () -> Unit,
    onUnlockDashboard: () -> Unit
) {
    GlassCard(
        borderColor = CyanNeon
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Connected",
                        tint = SignalGood,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Wi-Fi Connected",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
                SignalQualityBadge(quality = wifiDetails.signalQuality)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Network telemetry grid
            DetailRow(label = "Network SSID", value = wifiDetails.ssid)
            DetailRow(label = "Signal Strength", value = "${wifiDetails.rssi} dBm (${wifiDetails.signalPercentage}%)")
            DetailRow(label = "Frequency Band", value = wifiDetails.frequencyBand)
            DetailRow(label = "Link Speed", value = if (wifiDetails.linkSpeedMbps > 0) "${wifiDetails.linkSpeedMbps} Mbps" else "Unavailable")
            DetailRow(label = "Local IP Address", value = wifiDetails.ipAddress)
            DetailRow(label = "Gateway Router", value = wifiDetails.gateway)
            DetailRow(label = "Subnet Mask", value = wifiDetails.subnetMask)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onUnlockDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("gate_unlock_dashboard_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Open Scanning Dashboard",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = CyanBright,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Refresh Telemetry", color = TextPrimary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (value == "Unavailable" || value.startsWith("Protected")) TextMuted else TextPrimary
        )
    }
}
