package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomScanScreen(
    wifiDetails: WifiConnectionDetails,
    isTesting: Boolean,
    samples: List<Int>,
    secondsElapsed: Int,
    onStartTest: (Int) -> Unit,
    onCancelTest: () -> Unit,
    onSaveScan: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedLocation by remember { mutableStateOf("Living Room") }
    var customLocationText by remember { mutableStateOf("") }
    var userNotes by remember { mutableStateOf("") }

    val presetLocations = listOf(
        "Living Room",
        "Bedroom",
        "Kitchen",
        "Bathroom",
        "Hallway",
        "Balcony",
        "Home Office",
        "Custom"
    )

    val effectiveLocation = if (selectedLocation == "Custom") {
        customLocationText.ifBlank { "Custom Location" }
    } else {
        selectedLocation
    }

    val currentRssi = if (samples.isNotEmpty()) samples.last() else wifiDetails.rssi
    val avgRssi = if (samples.isNotEmpty()) samples.average().toInt() else currentRssi
    val minRssi = if (samples.isNotEmpty()) samples.minOrNull() ?: currentRssi else currentRssi
    val maxRssi = if (samples.isNotEmpty()) samples.maxOrNull() ?: currentRssi else currentRssi

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "Room-by-Room Wi-Fi Scan",
                subtitle = "Precision Range & Coverage Testing",
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

            // Step 1: Location selection
            GlassCard {
                Column {
                    Text(
                        text = "STEP 1: SELECT ROOM OR LOCATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanBright,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetLocations.forEach { loc ->
                            val isSelected = selectedLocation == loc
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyanNeon else Color(0xFF0F172A))
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanNeon else GlassBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedLocation = loc }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = loc,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PitchBlack else TextPrimary
                                )
                            }
                        }
                    }

                    if (selectedLocation == "Custom") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customLocationText,
                            onValueChange = { customLocationText = it },
                            placeholder = { Text("Enter room name (e.g. Master Bedroom)", fontSize = 12.sp, color = TextMuted) },
                            modifier = Modifier.fillMaxWidth().testTag("room_custom_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // Step 2: Sampling & Measurement
            GlassCard(
                borderColor = if (isTesting) CyanNeon else ElectricBlue
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STEP 2: COLLECT MEASUREMENTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanBright,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Location: $effectiveLocation",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }

                        if (isTesting) {
                            Text(
                                text = "Testing ${secondsElapsed}s / 6s",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanNeon
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isTesting) {
                        LinearProgressIndicator(
                            progress = { (secondsElapsed.toFloat() / 6f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = CyanNeon,
                            trackColor = Color(0x3300E5FF)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Live stats block
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatPill(title = "CURRENT", value = "$currentRssi dBm", color = CyanNeon)
                        StatPill(title = "AVERAGE", value = "$avgRssi dBm", color = ElectricBlue)
                        StatPill(title = "MIN / MAX", value = "$minRssi / $maxRssi", color = TextSecondary)
                        StatPill(title = "SAMPLES", value = "${samples.size}", color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isTesting) {
                        Button(
                            onClick = { onStartTest(6) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("room_start_measurement_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = TextPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (samples.isEmpty()) "Start 6-Second Room Test" else "Re-take Measurement",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onCancelTest,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252))
                        ) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cancel Sampling", color = Color(0xFFFF5252))
                        }
                    }
                }
            }

            // Step 3: Save record
            if (samples.isNotEmpty() && !isTesting) {
                GlassCard(
                    borderColor = Color(0xFF00E676)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "STEP 3: SAVE TO DATABASE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = userNotes,
                            onValueChange = { userNotes = it },
                            placeholder = { Text("Optional notes (e.g. Near window, closed door)", fontSize = 12.sp, color = TextMuted) },
                            modifier = Modifier.fillMaxWidth().testTag("room_notes_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onSaveScan(effectiveLocation, userNotes) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("room_save_scan_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Room Measurement", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Quick navigation to Saved Room Scans & Compare
            OutlinedButton(
                onClick = { onNavigate(AppScreen.SAVED_ROOMS) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("room_view_saved_scans_button"),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Saved Room Scans & Comparison Chart", color = TextPrimary, fontSize = 12.sp)
            }

            // Accuracy Disclaimer Box (as required by prompt section 7)
            GlassCard(shape = RoundedCornerShape(10.dp)) {
                Column {
                    Text(
                        text = "IMPORTANT ACCURACY RULES & RF PHYSICS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanBright
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Measurements reflect smartphone antenna RSSI at test position. Results vary by concrete walls, human presence, metal doors, router antenna polarization, and frequency band (5 GHz attenuates faster through obstacles than 2.4 GHz).",
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
private fun StatPill(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color)
    }
}
