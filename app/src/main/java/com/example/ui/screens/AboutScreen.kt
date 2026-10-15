package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.ui.components.GlassCard
import com.example.ui.components.RudraXFooter
import com.example.ui.components.RudraXHeader
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

private const val OFFICIAL_LOGO_URL = "https://rudraxstudio.freedev.app/RudraX-Technologies-Studio.jpg"
private const val OFFICIAL_WEBSITE = "https://rudraxstudio.freedev.app"

@Composable
fun AboutScreen(
    onClearImageCache: () -> Unit,
    onDeleteAllScans: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    val expandedSections = remember {
        mutableStateMapOf(
            1 to true,  // About the Application
            2 to false, // Features
            3 to false, // How to Connect
            4 to false, // How to Start Camera
            5 to false, // How to Read Signal
            6 to false, // How to Perform Room Scan
            7 to false, // How to Compare Rooms
            8 to false, // How to View Nearby Networks
            9 to false, // How to Discover Devices
            10 to false, // Android Permissions
            11 to false, // Privacy & Data Handling
            12 to false, // Troubleshooting
            13 to false, // Supported Devices & Limitations
            14 to true,  // Copyright & Ownership (Expanded by default)
            15 to true   // Official Company Attribution
        )
    }

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "About & Help",
                subtitle = "RudraX Technologies Studio",
                showBackButton = true,
                onBackClick = onNavigateBack,
                onAboutClick = {},
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

            // Official Logo & Branding Hero Card
            item {
                GlassCard(borderColor = CyanNeon) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        // Official RudraX Technologies Studio Logo
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF0B1120))
                                .border(1.5.dp, CyanNeon, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(OFFICIAL_LOGO_URL)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "RudraX Technologies Studio Official Logo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize().padding(6.dp),
                                loading = {
                                    Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(36.dp))
                                },
                                error = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(28.dp))
                                        Text("RudraX", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanBright)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "RUDRAX WiFi Vision 3D",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )

                        Text(
                            text = "Version 1.0 (Build 2026.10)",
                            fontSize = 11.sp,
                            color = CyanBright
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Powered by RudraX Technologies Studio",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElectricBlue
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(OFFICIAL_WEBSITE)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                            modifier = Modifier.height(38.dp).testTag("about_open_website_button")
                        ) {
                            Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Official Website: rudraxstudio.freedev.app", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }

            // SECTION 1: About the Application
            item {
                ExpandableSectionCard(
                    sectionNumber = 1,
                    title = "About the Application",
                    isExpanded = expandedSections[1] == true,
                    onToggle = { expandedSections[1] = !(expandedSections[1] ?: false) }
                ) {
                    Text(
                        text = "RUDRAX WiFi Vision 3D is a state-of-the-art native Android network diagnostics application designed to bridge physical architecture and radio frequency dynamics. By pairing real-time Android WifiManager and ConnectivityManager hardware measurements with CameraX AR overlays and 3D spatial beacons, users can visualize signal gradients, dead zones, and room-by-room coverage throughout homes, offices, and commercial facilities.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 2: Features
            item {
                ExpandableSectionCard(
                    sectionNumber = 2,
                    title = "Key Features",
                    isExpanded = expandedSections[2] == true,
                    onToggle = { expandedSections[2] = !(expandedSections[2] ?: false) }
                ) {
                    Text(
                        text = "• Live Camera AR 3D RF Visualization: Real-time CameraX feed overlaid with 3D radar rings and spatial markers.\n" +
                                "• 3D Simulation Fallback: Lightweight perspective grid for devices without camera permissions.\n" +
                                "• Room-by-Room Range Testing: Multi-sample duration tests with min/max/average RSSI analytics.\n" +
                                "• Persistent Room Comparison: SQLite/Room database storage with visual bar ranking.\n" +
                                "• Nearby Wi-Fi Spectrum Discovery: Scan AP frequencies, security suites, and channel bands.\n" +
                                "• Subnet Device Discovery: Privacy-bounded ICMP/TCP reachability audit with gateway detection.\n" +
                                "• Internet WAN Latency Test: Genuine DNS resolution and HTTP round-trip ping.\n" +
                                "• Report Export: Text-formatted audit summaries for sharing and archiving.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // SECTION 3: How to Connect to Wi-Fi
            item {
                ExpandableSectionCard(
                    sectionNumber = 3,
                    title = "How to Connect to Wi-Fi",
                    isExpanded = expandedSections[3] == true,
                    onToggle = { expandedSections[3] = !(expandedSections[3] ?: false) }
                ) {
                    Text(
                        text = "Step 1: Open device Wi-Fi Settings via the gate screen.\n" +
                                "Step 2: Connect to your 2.4 GHz, 5 GHz, or 6 GHz wireless network.\n" +
                                "Step 3: Return to RUDRAX WiFi Vision 3D. The app automatically verifies network link properties and unlocks the scanning dashboard.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 4: How to Start Camera Scanning
            item {
                ExpandableSectionCard(
                    sectionNumber = 4,
                    title = "How to Start Camera Scanning",
                    isExpanded = expandedSections[4] == true,
                    onToggle = { expandedSections[4] = !(expandedSections[4] ?: false) }
                ) {
                    Text(
                        text = "Open 'AR Signal Scanner' from the dashboard. When prompted, grant Camera permission. Point your rear camera toward the environment. The HUD will render holographic 3D radar rings anchored to your physical space.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 5: How to Read Signal Strength
            item {
                ExpandableSectionCard(
                    sectionNumber = 5,
                    title = "How to Read Signal Strength (dBm)",
                    isExpanded = expandedSections[5] == true,
                    onToggle = { expandedSections[5] = !(expandedSections[5] ?: false) }
                ) {
                    Text(
                        text = "Wi-Fi signal strength is measured in negative decibel milliwatts (dBm). Values closer to zero indicate stronger signal:\n\n" +
                                "• -30 to -50 dBm: EXCELLENT (Full speed, ideal for 4K streaming and low-latency gaming)\n" +
                                "• -51 to -60 dBm: GOOD (Stable connection, reliable browsing and video calls)\n" +
                                "• -61 to -67 dBm: FAIR (Acceptable, occasional packet retries)\n" +
                                "• -68 to -75 dBm: WEAK (Borderline coverage, potential buffer lag)\n" +
                                "• Below -75 dBm: VERY WEAK (High packet loss, disconnection likely)",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 6: How to Perform a Room Scan
            item {
                ExpandableSectionCard(
                    sectionNumber = 6,
                    title = "How to Perform a Room Scan",
                    isExpanded = expandedSections[6] == true,
                    onToggle = { expandedSections[6] = !(expandedSections[6] ?: false) }
                ) {
                    Text(
                        text = "1. Navigate to Room Scan.\n" +
                                "2. Select a room preset (e.g., Living Room, Bedroom) or enter a custom name.\n" +
                                "3. Tap 'Start 6-Second Room Test' and remain stationary.\n" +
                                "4. Observe real-time samples, min/max range, and average RSSI.\n" +
                                "5. Tap 'Save Room Measurement' to record the data locally in your persistent database.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 7: How to Compare Rooms
            item {
                ExpandableSectionCard(
                    sectionNumber = 7,
                    title = "How to Compare Rooms",
                    isExpanded = expandedSections[7] == true,
                    onToggle = { expandedSections[7] = !(expandedSections[7] ?: false) }
                ) {
                    Text(
                        text = "Navigate to 'Saved Room Scans'. The visual comparison bar chart automatically ranks your rooms from highest to lowest dBm coverage, highlighting the Strongest and Weakest areas.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 8: Nearby Wi-Fi Networks
            item {
                ExpandableSectionCard(
                    sectionNumber = 8,
                    title = "How to View Nearby Wi-Fi Networks",
                    isExpanded = expandedSections[8] == true,
                    onToggle = { expandedSections[8] = !(expandedSections[8] ?: false) }
                ) {
                    Text(
                        text = "Open 'Nearby Wi-Fi' from the overflow menu. With location permission enabled, the app scans available wireless channels and displays SSID, signal strength, channel frequency, and security suites (WPA2/WPA3).",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 9: Discover Local Devices
            item {
                ExpandableSectionCard(
                    sectionNumber = 9,
                    title = "How to Discover Local Network Devices",
                    isExpanded = expandedSections[9] == true,
                    onToggle = { expandedSections[9] = !(expandedSections[9] ?: false) }
                ) {
                    Text(
                        text = "Select 'My Local Network' from the overflow menu. Tap 'Audit Subnet'. The bounded scanner checks ICMP reachability and common service ports across your local subnet to detect responsive devices and router gateway addresses.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 10: Android Permissions
            item {
                ExpandableSectionCard(
                    sectionNumber = 10,
                    title = "Android Permissions & Privacy Rationale",
                    isExpanded = expandedSections[10] == true,
                    onToggle = { expandedSections[10] = !(expandedSections[10] ?: false) }
                ) {
                    Text(
                        text = "• CAMERA: Exclusively used for live rear-camera viewfinder in AR mode. Video is processed locally in RAM and never recorded or uploaded.\n" +
                                "• ACCESS_FINE_LOCATION: Mandated by Android OS to read Wi-Fi SSID and perform nearby spectrum scans to protect user geolocation privacy.\n" +
                                "• ACCESS_WIFI_STATE & ACCESS_NETWORK_STATE: Required to query signal RSSI, link speed, and gateway routing.\n" +
                                "• INTERNET: Used exclusively for the WAN reachability test and loading the official company logo.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 11: Privacy and Data Handling
            item {
                ExpandableSectionCard(
                    sectionNumber = 11,
                    title = "Privacy and Data Handling",
                    isExpanded = expandedSections[11] == true,
                    onToggle = { expandedSections[11] = !(expandedSections[11] ?: false) }
                ) {
                    Text(
                        text = "• Measurements are processed 100% locally on your smartphone.\n" +
                                "• No Wi-Fi passwords, credentials, or camera frames are ever collected or transmitted.\n" +
                                "• No third-party ad networks or tracking SDKs are incorporated.\n" +
                                "• All room scan records are stored in a private on-device SQLite database that you can wipe at any time.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 12: Troubleshooting
            item {
                ExpandableSectionCard(
                    sectionNumber = 12,
                    title = "Troubleshooting & Optimization",
                    isExpanded = expandedSections[12] == true,
                    onToggle = { expandedSections[12] = !(expandedSections[12] ?: false) }
                ) {
                    Text(
                        text = "• Signal drops unexpectedly: Check for microwave ovens, baby monitors, or thick masonry walls.\n" +
                                "• 5 GHz vs 2.4 GHz: 5 GHz offers higher bandwidth but shorter range and higher wall attenuation. 2.4 GHz travels further through obstacles.\n" +
                                "• SSID is displayed as 'Protected': Verify that Location services (GPS) are enabled in device settings.\n" +
                                "• Scan shows no nearby networks: Android enforces a scan throttle limit (max 4 scans per 2 minutes). Wait 30 seconds and retry.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 13: Supported Devices and Limitations
            item {
                ExpandableSectionCard(
                    sectionNumber = 13,
                    title = "Supported Devices & Physical Limitations",
                    isExpanded = expandedSections[13] == true,
                    onToggle = { expandedSections[13] = !(expandedSections[13] ?: false) }
                ) {
                    Text(
                        text = "A single smartphone measures radio signal at its internal antenna location. It cannot map a whole-room electromagnetic field from a single coordinate or determine precise router distance without multi-antenna beamforming. Signal strength does not guarantee internet ISP bandwidth.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // SECTION 14: Exact Copyright & Legal Notice (Section 17 requirement)
            item {
                GlassCard(
                    borderColor = CyanNeon,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        Text(
                            text = "14. COPYRIGHT AND COMPANY OWNERSHIP NOTICE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanBright,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "RUDRAX WiFi Vision 3D is an application developed and presented by RudraX Technologies Studio.\n\n" +
                                    "© 2026 RudraX Technologies Studio. All rights reserved.\n\n" +
                                    "The original application branding, original interface design, original graphics, and original software components created specifically for this project are claimed as the intellectual property of RudraX Technologies Studio, subject to applicable law.\n\n" +
                                    "Third-party software, open-source libraries, Android platform components, and externally licensed assets remain subject to their respective owners' rights and license terms.\n\n" +
                                    "Unauthorized copying, redistribution, or reproduction of protected original material may result in appropriate legal action, including a DMCA notice where applicable.\n\n" +
                                    "For intellectual property or copyright concerns, contact the company through its official website.",
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // SECTION 15: Official Company Attribution
            item {
                GlassCard(borderColor = ElectricBlue) {
                    Column {
                        Text(
                            text = "15. OFFICIAL COMPANY ATTRIBUTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "RUDRAX WiFi Vision 3D\n" +
                                    "Powered by RudraX Technologies Studio\n" +
                                    "© 2026 RudraX Technologies Studio. All rights reserved.\n" +
                                    "Application developed and published by RudraX Technologies Studio.\n" +
                                    "Official Website: https://rudraxstudio.freedev.app",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Privacy & Cache Controls Card
            item {
                GlassCard {
                    Column {
                        Text(
                            text = "PRIVACY & STORAGE CONTROLS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanBright,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onClearImageCache()
                                    Toast.makeText(context, "Logo cache purged.", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(44.dp).testTag("about_clear_cache_button")
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = CyanBright, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear Logo Cache", fontSize = 11.sp, color = TextPrimary)
                            }

                            Button(
                                onClick = {
                                    onDeleteAllScans()
                                    Toast.makeText(context, "All room scans deleted.", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF450A0A), contentColor = Color(0xFFFF8A80)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(44.dp).testTag("about_delete_all_data_button")
                            ) {
                                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete All Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
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
private fun ExpandableSectionCard(
    sectionNumber: Int,
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    GlassCard(shape = RoundedCornerShape(12.dp)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$sectionNumber. $title",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpanded) CyanNeon else TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = if (isExpanded) CyanNeon else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = GlassBorderMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    content()
                }
            }
        }
    }
}
