package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiscoveredDevice
import com.example.data.model.WifiConnectionDetails
import com.example.ui.components.GlassCard
import com.example.ui.components.RudraXFooter
import com.example.ui.components.RudraXHeader
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepVoid
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderMuted
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SignalGood
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen

@Composable
fun LocalNetworkScreen(
    currentWifi: WifiConnectionDetails,
    discoveredDevices: List<DiscoveredDevice>,
    isScanning: Boolean,
    progress: Pair<Int, Int>,
    onStartScan: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "My Local Network",
                subtitle = "Subnet Reachability & Device Discovery",
                showBackButton = true,
                onBackClick = onNavigateBack,
                onAboutClick = { onNavigate(AppScreen.ABOUT) },
                onMenuSelect = { onNavigate(it) }
            )
        },
        containerColor = PitchBlack
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Subnet Scan Action & Progress Card
            item {
                GlassCard(borderColor = CyanNeon) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SUBNET DISCOVERY RANGE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanBright,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "Subnet: ${currentWifi.subnetMask}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Gateway: ${currentWifi.gateway} · My IP: ${currentWifi.ipAddress}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Button(
                                onClick = onStartScan,
                                enabled = !isScanning && currentWifi.isConnected,
                                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(42.dp).testTag("local_start_scan_button")
                            ) {
                                if (isScanning) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PitchBlack, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Scanning...", fontSize = 11.sp)
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Audit Subnet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (isScanning && progress.second > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val frac = progress.first.toFloat() / progress.second.toFloat()
                            LinearProgressIndicator(
                                progress = { frac.coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = CyanNeon,
                                trackColor = Color(0x3300E5FF)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Probing target IPs: ${progress.first} / ${progress.second}",
                                fontSize = 10.sp,
                                color = CyanBright
                            )
                        }
                    }
                }
            }

            // Router Administration Gateway Link
            if (currentWifi.gateway != "Unavailable" && currentWifi.gateway.isNotBlank()) {
                item {
                    GlassCard(borderColor = ElectricBlue) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Router, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ROUTER GATEWAY ADMINISTRATION",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlue
                                    )
                                }
                                Text(
                                    text = "http://${currentWifi.gateway}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Open router portal directly in browser to view authenticated client tables.",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            Button(
                                onClick = {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://${currentWifi.gateway}")).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(browserIntent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = TextPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(38.dp).testTag("open_router_admin_button")
                            ) {
                                Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Society Wi-Fi & Discovered Devices Clarification Box (Section 11 & 12)
            item {
                GlassCard(
                    borderColor = Color(0xFF651FFF).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFFB388FF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CRITICAL DISTINCTION: SOCIETY & LOCAL WI-FI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB388FF)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "1. Nearby Wi-Fi: Wireless networks detected by RF scanning.\n2. Local Network Devices: Hosts that respond to bounded subnet reachability probes.\n3. Router-Reported Clients: Exhaustive list maintained by the router management processor.\n\n\"Devices discovered on your current local network are not necessarily all devices connected to your router.\" Isolated clients or sleeping IoT nodes will not appear. RudraX respects client isolation and privacy.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Discovered Devices List
            item {
                Text(
                    text = "REACHABLE DEVICES ON SUBNET (${discoveredDevices.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.8.sp
                )
            }

            if (discoveredDevices.isEmpty() && !isScanning) {
                item {
                    GlassCard {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "No local devices mapped yet. Tap 'Audit Subnet' above to start.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(discoveredDevices) { dev ->
                    DiscoveredDeviceItem(device = dev)
                }
            }

            item {
                RudraXFooter()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DiscoveredDeviceItem(device: DiscoveredDevice) {
    val icon = when {
        device.isGateway -> Icons.Default.Router
        device.isCurrentDevice -> Icons.Default.PhoneAndroid
        else -> Icons.Default.Computer
    }

    val iconColor = when {
        device.isGateway -> ElectricBlue
        device.isCurrentDevice -> CyanNeon
        else -> Color(0xFF90CAF9)
    }

    GlassCard(shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f))
                        .border(1.dp, iconColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = device.ip,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (device.isGateway) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x332979FF))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "Gateway", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                            }
                        }
                        if (device.isCurrentDevice) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x3300E5FF))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "This Device", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanNeon)
                            }
                        }
                    }

                    if (device.hostname != null) {
                        Text(
                            text = device.hostname,
                            fontSize = 11.sp,
                            color = CyanBright
                        )
                    }

                    Text(
                        text = "Probe: ${device.discoveryMethod}",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${device.responseTimeMs} ms",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SignalGood
                )
                Text(
                    text = "Responsive",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
