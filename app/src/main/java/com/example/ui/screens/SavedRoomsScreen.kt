package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomScanEntity
import com.example.data.repository.RoomScanRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.RudraXFooter
import com.example.ui.components.RudraXHeader
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderMuted
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SignalExcellent
import com.example.ui.theme.SignalFair
import com.example.ui.theme.SignalGood
import com.example.ui.theme.SignalVeryWeak
import com.example.ui.theme.SignalWeak
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedRoomsScreen(
    savedScans: List<RoomScanEntity>,
    roomScanRepository: RoomScanRepository,
    onDeleteScan: (RoomScanEntity) -> Unit,
    onDeleteAllScans: () -> Unit,
    onStartNewMeasurement: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault())

    val sortedScans = savedScans.sortedByDescending { it.avgRssi }
    val strongestScan = sortedScans.firstOrNull()
    val weakestScan = if (sortedScans.size > 1) sortedScans.lastOrNull() else null

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "Saved Room Scans",
                subtitle = "Signal Comparison & Analysis",
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

            // Action Header Buttons (Export Report, Start New, Clear All)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onStartNewMeasurement,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("saved_rooms_new_test_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Test", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val report = roomScanRepository.generateExportReport(savedScans)
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("RUDRAX Wi-Fi Report", report)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Full report copied to clipboard!", Toast.LENGTH_SHORT).show()

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, report)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Export RUDRAX WiFi Report")
                                context.startActivity(shareIntent)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("saved_rooms_export_report_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export", fontSize = 12.sp, color = TextPrimary)
                    }

                    if (savedScans.isNotEmpty()) {
                        IconButton(
                            onClick = onDeleteAllScans,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .testTag("saved_rooms_delete_all_button")
                        ) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear All Records", tint = Color(0xFFFF5252))
                        }
                    }
                }
            }

            // Strongest and Weakest Summary Card
            if (strongestScan != null) {
                item {
                    GlassCard(borderColor = CyanNeon) {
                        Column {
                            Text(
                                text = "COVERAGE RANKING SUMMARY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanBright,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Strongest
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = SignalExcellent, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "STRONGEST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SignalExcellent)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = strongestScan.roomName, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                                    Text(text = "${strongestScan.avgRssi} dBm (${strongestScan.qualityCategory})", fontSize = 11.sp, color = CyanBright)
                                }

                                // Weakest
                                if (weakestScan != null) {
                                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.TrendingDown, contentDescription = null, tint = SignalVeryWeak, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "WEAKEST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SignalVeryWeak)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = weakestScan.roomName, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                                        Text(text = "${weakestScan.avgRssi} dBm (${weakestScan.qualityCategory})", fontSize = 11.sp, color = SignalVeryWeak)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Visual Comparison Chart
            if (sortedScans.isNotEmpty()) {
                item {
                    GlassCard {
                        Column {
                            Text(
                                text = "ROOM COMPARISON CHART",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanBright,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            sortedScans.forEach { scan ->
                                val barFraction = ((scan.avgRssi + 100).toFloat() / 70f).coerceIn(0.05f, 1.0f)
                                val barColor = when {
                                    scan.avgRssi >= -50 -> SignalExcellent
                                    scan.avgRssi >= -60 -> SignalGood
                                    scan.avgRssi >= -67 -> SignalFair
                                    scan.avgRssi >= -75 -> SignalWeak
                                    else -> SignalVeryWeak
                                }

                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = scan.roomName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        Text(text = "${scan.avgRssi} dBm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = barColor)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { barFraction },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                        color = barColor,
                                        trackColor = Color(0x221E293B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Detailed Scans List
            item {
                Text(
                    text = "RECORDED LOCATIONS (${savedScans.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.8.sp
                )
            }

            if (savedScans.isEmpty()) {
                item {
                    GlassCard {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                        ) {
                            Text(text = "No saved measurements yet.", fontSize = 13.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onStartNewMeasurement,
                                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Start First Room Scan")
                            }
                        }
                    }
                }
            } else {
                items(sortedScans, key = { it.id }) { scan ->
                    ScanItemRow(
                        scan = scan,
                        formattedDate = dateFormat.format(Date(scan.timestamp)),
                        onDelete = { onDeleteScan(scan) }
                    )
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
private fun ScanItemRow(
    scan: RoomScanEntity,
    formattedDate: String,
    onDelete: () -> Unit
) {
    GlassCard(shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.roomName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Range: Min ${scan.minRssi} / Max ${scan.maxRssi} dBm · ${scan.frequencyBand}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                if (scan.notes.isNotBlank()) {
                    Text(
                        text = "Notes: ${scan.notes}",
                        fontSize = 10.sp,
                        color = CyanBright
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${scan.avgRssi} dBm",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = CyanNeon
                )
                Text(
                    text = scan.qualityCategory,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlue
                )
                Spacer(modifier = Modifier.height(6.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Scan Record",
                        tint = Color(0xFFFF5252).copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
