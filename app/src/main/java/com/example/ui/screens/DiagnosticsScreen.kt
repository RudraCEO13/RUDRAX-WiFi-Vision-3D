package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderMuted
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiagnosticsScreen(
    wifiDetails: WifiConnectionDetails,
    isMonitoringActive: Boolean,
    onToggleMonitoring: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    val diagnosticsText = buildString {
        appendLine("RUDRAX WiFi Vision 3D — Real-Time Wi-Fi Diagnostics")
        appendLine("Powered by RudraX Technologies Studio")
        appendLine("Timestamp: ${dateFormat.format(Date(wifiDetails.lastUpdateTimestamp))}")
        appendLine("--------------------------------------------------")
        appendLine("Wi-Fi Enabled: ${wifiDetails.isWifiEnabled}")
        appendLine("Connection Status: ${if (wifiDetails.isConnected) "Connected" else "Disconnected"}")
        appendLine("SSID: ${wifiDetails.ssid}")
        appendLine("BSSID: ${wifiDetails.bssid}")
        appendLine("Signal Strength (RSSI): ${wifiDetails.rssi} dBm")
        appendLine("Signal Quality: ${wifiDetails.signalQuality.label}")
        appendLine("Signal Percentage: ${wifiDetails.signalPercentage}%")
        appendLine("Frequency (MHz): ${if (wifiDetails.frequencyMhz > 0) "${wifiDetails.frequencyMhz} MHz" else "Unavailable"}")
        appendLine("Frequency Band: ${wifiDetails.frequencyBand}")
        appendLine("Physical Link Speed: ${if (wifiDetails.linkSpeedMbps > 0) "${wifiDetails.linkSpeedMbps} Mbps" else "Unavailable"}")
        appendLine("Local IPv4 Address: ${wifiDetails.ipAddress}")
        appendLine("Gateway Router IP: ${wifiDetails.gateway}")
        appendLine("Subnet Mask: ${wifiDetails.subnetMask}")
        appendLine("Transport Type: ${wifiDetails.transportType}")
        appendLine("Signal Trend: ${wifiDetails.trend.label}")
        appendLine("--------------------------------------------------")
        appendLine("© 2026 RudraX Technologies Studio. All rights reserved.")
    }

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "Wi-Fi Diagnostics",
                subtitle = "Hardware & RF Telemetry",
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

            // Control Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onToggleMonitoring(!isMonitoringActive) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMonitoringActive) ElectricBlue else Color(0xFF334155),
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("diag_toggle_monitoring_button")
                ) {
                    Icon(
                        imageVector = if (isMonitoringActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isMonitoringActive) "Pause Monitor" else "Start Monitor", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onRefresh,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("diag_refresh_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refresh", fontSize = 12.sp, color = TextPrimary)
                }

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("RudraX Diagnostics", diagnosticsText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Diagnostics copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("diag_copy_button")
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = CyanBright, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 12.sp, color = TextPrimary)
                }
            }

            // Overview Card
            GlassCard(borderColor = CyanNeon) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RF SPECTRUM SNAPSHOT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanBright,
                            letterSpacing = 0.8.sp
                        )
                        SignalQualityBadge(quality = wifiDetails.signalQuality)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    DiagDetailRow("Wi-Fi Radio Status", if (wifiDetails.isWifiEnabled) "Enabled (Active)" else "Disabled")
                    DiagDetailRow("Network Association", if (wifiDetails.isConnected) "Connected & Authenticated" else "Not Connected")
                    DiagDetailRow("Network SSID", wifiDetails.ssid)
                    DiagDetailRow("BSSID (Access Point MAC)", wifiDetails.bssid)
                    DiagDetailRow("Received Signal (RSSI)", "${wifiDetails.rssi} dBm")
                    DiagDetailRow("Estimated Signal Quality", "${wifiDetails.signalPercentage}% (${wifiDetails.signalQuality.label})")
                    DiagDetailRow("Signal Stability Trend", wifiDetails.trend.label)
                }
            }

            // Radio Frequency & Link Specs
            GlassCard(borderColor = ElectricBlue) {
                Column {
                    Text(
                        text = "RADIO & PHYSICAL LINK DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DiagDetailRow("Center Frequency", if (wifiDetails.frequencyMhz > 0) "${wifiDetails.frequencyMhz} MHz" else "Unavailable")
                    DiagDetailRow("Frequency Band", wifiDetails.frequencyBand)
                    DiagDetailRow("Physical Link Speed", if (wifiDetails.linkSpeedMbps > 0) "${wifiDetails.linkSpeedMbps} Mbps" else "Unavailable")
                    DiagDetailRow("Transport Layer", wifiDetails.transportType)
                    DiagDetailRow("Telemetry Timestamp", dateFormat.format(Date(wifiDetails.lastUpdateTimestamp)))
                }
            }

            // IP & Routing Card
            GlassCard {
                Column {
                    Text(
                        text = "IP CONFIGURATION & ROUTING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanBright,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DiagDetailRow("Local IPv4 Address", wifiDetails.ipAddress)
                    DiagDetailRow("Router / Gateway", wifiDetails.gateway)
                    DiagDetailRow("Subnet Mask", wifiDetails.subnetMask)
                }
            }

            // Android Privacy Explanation Note
            GlassCard(shape = RoundedCornerShape(10.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyanBright,
                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Android OS Security Architecture: On modern Android versions (Android 10+), SSID and BSSID access is protected by device location permissions to prevent geofencing tracking. When location is disabled, Android returns '<unknown ssid>' by design.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )
                }
            }

            RudraXFooter()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DiagDetailRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 12.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (value == "Unavailable" || value.startsWith("Protected")) TextMuted else TextPrimary
        )
    }
    HorizontalDivider(color = GlassBorderMuted, modifier = Modifier.padding(vertical = 2.dp))
}
