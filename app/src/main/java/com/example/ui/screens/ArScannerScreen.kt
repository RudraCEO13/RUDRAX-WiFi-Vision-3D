package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.WifiConnectionDetails
import com.example.ui.components.GlassCard
import com.example.ui.components.RadarVisualization3D
import com.example.ui.components.RudraXHeader
import com.example.ui.components.SignalQualityBadge
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpatialMarker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ArScannerScreen(
    wifiDetails: WifiConnectionDetails,
    spatialMarkers: List<SpatialMarker>,
    isOverlayVisible: Boolean,
    isMeasurementPaused: Boolean,
    isCameraFallbackMode: Boolean,
    onToggleOverlay: () -> Unit,
    onTogglePause: () -> Unit,
    onSetCameraFallbackMode: (Boolean) -> Unit,
    onAddSpatialMarker: (Float, Float, String) -> Unit,
    onClearSpatialMarkers: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDeniedCount by remember { mutableStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            permissionDeniedCount++
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission && !isCameraFallbackMode) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Scaffold(
        topBar = {
            RudraXHeader(
                title = "AR 3D Signal Scanner",
                subtitle = if (isCameraFallbackMode) "3D Simulation Grid" else "Live Camera Telemetry",
                showBackButton = true,
                onBackClick = onNavigateBack,
                onAboutClick = { onNavigate(AppScreen.ABOUT) },
                onMenuSelect = { onNavigate(it) }
            )
        },
        containerColor = PitchBlack
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("ar_scanner_screen")
        ) {
            // LAYER 1: Background View (Real CameraX feed OR Non-Camera 3D Cyber Grid)
            if (hasCameraPermission && !isCameraFallbackMode) {
                CameraXPreviewView(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val normX = offset.x / size.width
                                val normY = offset.y / size.height
                                onAddSpatialMarker(normX, normY, "Point ${spatialMarkers.size + 1}")
                            }
                        }
                )
            } else {
                // Non-Camera Fallback 3D Grid Canvas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF030712))
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val normX = offset.x / size.width
                                val normY = offset.y / size.height
                                onAddSpatialMarker(normX, normY, "Point ${spatialMarkers.size + 1}")
                            }
                        }
                )
            }

            // LAYER 2: 3D Holographic Radar & Spatial Beacons Canvas Overlay
            if (isOverlayVisible) {
                RadarVisualization3D(
                    rssi = wifiDetails.rssi,
                    quality = wifiDetails.signalQuality,
                    spatialMarkers = spatialMarkers,
                    is3DPerspective = true,
                    showSweep = true,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // LAYER 3: If Camera Permission Denied & Not in Fallback, show graceful permission banner
            if (!hasCameraPermission && !isCameraFallbackMode) {
                CameraPermissionDeniedBanner(
                    permissionDeniedCount = permissionDeniedCount,
                    onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    onOpenSettings = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    },
                    onSwitchToFallback = { onSetCameraFallbackMode(true) }
                )
            }

            // LAYER 4: Compact Live HUD Overlay (Section 6.1)
            if (isOverlayVisible) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .width(260.dp)
                ) {
                    GlassCard(
                        shape = RoundedCornerShape(12.dp),
                        borderColor = CyanNeon.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Wi-Fi SIGNAL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "${wifiDetails.rssi} dBm",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CyanNeon
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "SIGNAL QUALITY", fontSize = 10.sp, color = TextMuted)
                                SignalQualityBadge(quality = wifiDetails.signalQuality)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "CONNECTION", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = if (wifiDetails.isConnected) "CONNECTED" else "DISCONNECTED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (wifiDetails.isConnected) Color(0xFF00E676) else Color.Red
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "FREQUENCY", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = wifiDetails.frequencyBand,
                                    fontSize = 10.sp,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "SIGNAL TREND", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = wifiDetails.trend.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanBright
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "LAST UPDATE", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = dateFormat.format(Date(wifiDetails.lastUpdateTimestamp)),
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // LAYER 5: Interactive Floating Action Controls (Pause, Hide Overlay, Drop Beacon, Mode switch)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Transparency disclaimer pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC000000))
                        .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Real-time measurements at device location. Tap screen to drop 3D beacon.",
                        fontSize = 10.sp,
                        color = CyanBright
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Place 3D Marker at center
                    Button(
                        onClick = {
                            onAddSpatialMarker(0.5f, 0.5f, "Beacon ${spatialMarkers.size + 1}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(46.dp)
                            .testTag("ar_place_marker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddLocation,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Drop Marker (${spatialMarkers.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Pause / Resume Toggle
                    IconButton(
                        onClick = onTogglePause,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xCC0D1527))
                            .border(1.dp, ElectricBlue, CircleShape)
                            .testTag("ar_pause_resume_button")
                    ) {
                        Icon(
                            imageVector = if (isMeasurementPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isMeasurementPaused) "Resume" else "Pause",
                            tint = CyanBright
                        )
                    }

                    // Toggle Overlay Visibility
                    IconButton(
                        onClick = onToggleOverlay,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xCC0D1527))
                            .border(1.dp, CyanNeon, CircleShape)
                            .testTag("ar_toggle_overlay_button")
                    ) {
                        Icon(
                            imageVector = if (isOverlayVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle HUD Overlays",
                            tint = CyanNeon
                        )
                    }

                    // Clear Markers
                    if (spatialMarkers.isNotEmpty()) {
                        IconButton(
                            onClick = onClearSpatialMarkers,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xCC0D1527))
                                .border(1.dp, Color(0xFFFF5252), CircleShape)
                                .testTag("ar_clear_markers_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear Markers",
                                tint = Color(0xFFFF5252)
                            )
                        }
                    }

                    // Switch Mode (Camera vs Fallback)
                    IconButton(
                        onClick = { onSetCameraFallbackMode(!isCameraFallbackMode) },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xCC0D1527))
                            .border(1.dp, Color(0x667C4DFF), CircleShape)
                            .testTag("ar_mode_switch_button")
                    ) {
                        Icon(
                            imageVector = if (isCameraFallbackMode) Icons.Default.CameraAlt else Icons.Default.ViewInAr,
                            contentDescription = "Switch Camera / 3D Grid Mode",
                            tint = Color(0xFFB388FF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraXPreviewView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                } catch (_: Exception) {}
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}

@Composable
private fun CameraPermissionDeniedBanner(
    permissionDeniedCount: Int,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onSwitchToFallback: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEE050A14))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Camera Permission Required for AR",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The physical rear camera is used exclusively to display live room video underneath your measured Wi-Fi 3D signal telemetry. Video is never recorded or uploaded.",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (permissionDeniedCount >= 2) {
                Button(
                    onClick = onOpenSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(0.8f).height(46.dp)
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open App Settings", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = PitchBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(0.8f).height(46.dp)
                ) {
                    Text("Grant Camera Access", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onSwitchToFallback,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue),
                modifier = Modifier.fillMaxWidth(0.8f).height(46.dp)
            ) {
                Icon(imageVector = Icons.Default.ViewInAr, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use 3D Radar Fallback Mode", color = TextPrimary, fontSize = 12.sp)
            }
        }
    }
}
