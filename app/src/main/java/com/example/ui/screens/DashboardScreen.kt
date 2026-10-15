package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.core.content.ContextCompat
import com.example.data.model.RoomScanEntity
import com.example.data.model.WifiConnectionDetails
import com.example.ui.components.GlassCard
import com.example.ui.components.RadarVisualization3D
import com.example.ui.components.RudraXFooter
import com.example.ui.components.RudraXHeader
import com.example.ui.components.SignalQualityBadge
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderMuted
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SignalGood
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    wifiDetails: WifiConnectionDetails,
    savedScans: List<RoomScanEntity>,
    onRefresh: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "RUDRAX WiFi Vision 3D",
                subtitle = "Live Wi-Fi Intelligence",
                showBackButton = false,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // CARD 1: Current Wi-Fi Telemetry
            item {
                CurrentWifiCard(
                    wifiDetails = wifiDetails,
                    onRefresh = onRefresh
                )
            }

            // CARD 2: AR Signal Scanner
            item {
                ArScannerCard(
                    wifiDetails = wifiDetails,
                    hasCameraPermission = hasCameraPermission,
                    onStartScan = { onNavigate(AppScreen.AR_SCANNER) }
                )
            }

            // CARD 3: Room Scan
            item {
                RoomScanCard(
                    savedScansCount = savedScans.size,
                    onNewRoomTest = { onNavigate(AppScreen.ROOM_SCAN) },
                    onViewSavedRooms = { onNavigate(AppScreen.SAVED_ROOMS) }
                )
            }

            // CARD 4: Network Discovery
            item {
                NetworkDiscoveryCard(
                    onNearbyNetworks = { onNavigate(AppScreen.NEARBY_NETWORKS) },
                    onLocalNetwork = { onNavigate(AppScreen.LOCAL_NETWORK) }
                )
            }

            // CARD 5: Diagnostics & Internet Test
            item {
                DiagnosticsCard(
                    wifiDetails = wifiDetails,
                    onOpenDiagnostics = { onNavigate(AppScreen.DIAGNOSTICS) },
                    onInternetTest = { onNavigate(AppScreen.CONNECTIVITY_TEST) }
                )
            }

            // CARD 6: Recent Activity & Saved Reports
            item {
                RecentActivityCard(
                    savedScans = savedScans,
                    onViewHistory = { onNavigate(AppScreen.SAVED_ROOMS) }
                )
            }

            // Footer
            item {
                RudraXFooter()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CurrentWifiCard(
    wifiDetails: WifiConnectionDetails,
    onRefresh: () -> Unit
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
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (wifiDetails.isConnected) SignalGood else Color.Red)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (wifiDetails.isConnected) "Wi-Fi Connected" else "Disconnected",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SignalQualityBadge(quality = wifiDetails.signalQuality)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("dashboard_refresh_wifi_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Wi-Fi Status",
                            tint = CyanBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SSID",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = wifiDetails.ssid,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "SIGNAL (RSSI)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = if (wifiDetails.rssi != 0) "${wifiDetails.rssi} dBm" else "N/A",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = CyanNeon
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = GlassBorderMuted)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Band: ${wifiDetails.frequencyBand}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Speed: ${if (wifiDetails.linkSpeedMbps > 0) "${wifiDetails.linkSpeedMbps} Mbps" else "N/A"}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Trend: ${wifiDetails.trend.label}",
                    fontSize = 11.sp,
                    color = CyanBright,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ArScannerCard(
    wifiDetails: WifiConnectionDetails,
    hasCameraPermission: Boolean,
    onStartScan: () -> Unit
) {
    GlassCard(
        borderColor = ElectricBlue
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = "AR 3D Scanner",
                        tint = CyanNeon,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AR 3D Signal Scanner",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                }
                Text(
                    text = if (hasCameraPermission) "Camera Ready" else "Permission Req.",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (hasCameraPermission) SignalGood else Color(0xFFFFB300)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mini visual preview area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF060B14))
                    .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
                    .clickable { onStartScan() },
                contentAlignment = Alignment.Center
            ) {
                RadarVisualization3D(
                    rssi = wifiDetails.rssi,
                    quality = wifiDetails.signalQuality,
                    is3DPerspective = true,
                    showSweep = true
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xBB000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Live 3D RF Hologram",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStartScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("dashboard_start_scan_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Launch AR 3D Scanner",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun RoomScanCard(
    savedScansCount: Int,
    onNewRoomTest: () -> Unit,
    onViewSavedRooms: () -> Unit
) {
    GlassCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Room-by-Room Testing",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = "$savedScansCount Saved",
                    fontSize = 11.sp,
                    color = CyanBright,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Map signal propagation across rooms (Living Room, Bedroom, Kitchen, etc.) and calculate average dBm coverage.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNewRoomTest,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("dashboard_new_room_test_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Start Room Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onViewSavedRooms,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("dashboard_compare_rooms_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Text("Compare Rooms", fontSize = 12.sp, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun NetworkDiscoveryCard(
    onNearbyNetworks: () -> Unit,
    onLocalNetwork: () -> Unit
) {
    GlassCard {
        Column {
            Text(
                text = "Network Discovery & Auditing",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Scan nearby frequency spectrum networks or audit local devices on your current authorized subnet.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNearbyNetworks,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("dashboard_nearby_networks_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiFind,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nearby Wi-Fi", fontSize = 11.sp, color = TextPrimary)
                }

                OutlinedButton(
                    onClick = onLocalNetwork,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("dashboard_local_network_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Local Devices", fontSize = 11.sp, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun DiagnosticsCard(
    wifiDetails: WifiConnectionDetails,
    onOpenDiagnostics: () -> Unit,
    onInternetTest: () -> Unit
) {
    GlassCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diagnostics & Speed Test",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = CyanBright,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "IP: ${wifiDetails.ipAddress} | Gateway: ${wifiDetails.gateway}",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenDiagnostics,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("dashboard_full_diagnostics_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Text("Technical Stats", fontSize = 11.sp, color = TextPrimary)
                }

                Button(
                    onClick = onInternetTest,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("dashboard_internet_test_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = CyanNeon),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Internet Ping", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RecentActivityCard(
    savedScans: List<RoomScanEntity>,
    onViewHistory: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

    GlassCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Room Measurements",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                IconButton(
                    onClick = onViewHistory,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "View Scan History",
                        tint = CyanBright,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (savedScans.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No saved room tests yet. Run a room scan to compare signal strength across your living space.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                savedScans.take(3).forEach { scan ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = scan.roomName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = dateFormat.format(Date(scan.timestamp)),
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = "${scan.avgRssi} dBm",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }
                    HorizontalDivider(color = GlassBorderMuted, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
    }
}
