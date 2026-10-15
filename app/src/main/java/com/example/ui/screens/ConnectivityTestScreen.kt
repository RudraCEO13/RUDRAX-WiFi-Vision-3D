package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectivityTestResult
import com.example.ui.components.GlassCard
import com.example.ui.components.RudraXFooter
import com.example.ui.components.RudraXHeader
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorderMuted
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SignalGood
import com.example.ui.theme.SignalVeryWeak
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConnectivityTestScreen(
    testResult: ConnectivityTestResult,
    onRunTest: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "Internet Connectivity Test",
                subtitle = "WAN Reachability & Latency",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // Hero Status Card
            GlassCard(
                borderColor = if (testResult.isInternetReachable) SignalGood else CyanNeon
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (testResult.isInternetReachable) Icons.Default.CheckCircle else if (testResult.dnsResolved) Icons.Default.NetworkCheck else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (testResult.isInternetReachable) SignalGood else CyanNeon,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (testResult.isTesting) "Performing Live WAN Handshake..." else if (testResult.isInternetReachable) "Internet Reachable" else "WAN Offline or Testing Required",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (testResult.isTesting) "Probing DNS resolver and HTTP endpoint" else testResult.notes.ifBlank { "Tap 'Run Connectivity Ping' below to test live reachability." },
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onRunTest,
                        enabled = !testResult.isTesting,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("run_connectivity_test_button")
                    ) {
                        if (testResult.isTesting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = PitchBlack, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pinging Target Endpoint...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Run Connectivity Ping", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Measurement Stats Card
            GlassCard(borderColor = ElectricBlue) {
                Column {
                    Text(
                        text = "LATENCY & DNS RESOLUTION METRICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanBright,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Dns, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("DNS Lookup Time", fontSize = 12.sp, color = TextSecondary)
                        }
                        Text(
                            text = if (testResult.dnsResolved) "${testResult.dnsResolutionTimeMs} ms" else "Unresolved",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (testResult.dnsResolved) SignalGood else SignalVeryWeak
                        )
                    }
                    HorizontalDivider(color = GlassBorderMuted, modifier = Modifier.padding(vertical = 6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("HTTP Round-Trip Latency", fontSize = 12.sp, color = TextSecondary)
                        }
                        Text(
                            text = if (testResult.httpLatencyMs > 0) "${testResult.httpLatencyMs} ms" else "N/A",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }
                    HorizontalDivider(color = GlassBorderMuted, modifier = Modifier.padding(vertical = 6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Target Endpoint", fontSize = 12.sp, color = TextSecondary)
                        Text(testResult.targetEndpoint, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    HorizontalDivider(color = GlassBorderMuted, modifier = Modifier.padding(vertical = 6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Test Timestamp", fontSize = 12.sp, color = TextSecondary)
                        Text(dateFormat.format(Date(testResult.testedTimestamp)), fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            // Speed Testing Transparency Box (Section 13)
            GlassCard(shape = RoundedCornerShape(10.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp).padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Technical Honesty Note: This test verifies WAN reachability and DNS/HTTP round-trip latency. A reachability ping does not measure full download or upload bandwidth throughput. Fabricated speed meters have been intentionally excluded in compliance with RudraX engineering integrity principles.",
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
