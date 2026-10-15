package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ArScannerScreen
import com.example.ui.screens.ConnectivityTestScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.LocalNetworkScreen
import com.example.ui.screens.NearbyNetworksScreen
import com.example.ui.screens.RoomScanScreen
import com.example.ui.screens.SavedRoomsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WifiGateScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PitchBlack
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PitchBlack
                ) {
                    RudraXAppContent()
                }
            }
        }
    }
}

@Composable
fun RudraXAppContent(
    viewModel: MainViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val wifiDetails by viewModel.wifiDetails.collectAsState()
    val savedScans by viewModel.allRoomScans.collectAsState()
    val isRoomTesting by viewModel.isRoomTesting.collectAsState()
    val roomSamples by viewModel.roomTestSamples.collectAsState()
    val roomSecondsElapsed by viewModel.roomTestSecondsElapsed.collectAsState()
    val spatialMarkers by viewModel.spatialMarkers.collectAsState()
    val isOverlayVisible by viewModel.isOverlayVisible.collectAsState()
    val isMeasurementPaused by viewModel.isMeasurementPaused.collectAsState()
    val isCameraFallbackMode by viewModel.isCameraFallbackMode.collectAsState()
    val nearbyNetworks by viewModel.nearbyNetworks.collectAsState()
    val isScanningNearby by viewModel.isScanningNearby.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()
    val isScanningDevices by viewModel.isScanningDevices.collectAsState()
    val discoveryProgress by viewModel.discoveryProgress.collectAsState()
    val connectivityResult by viewModel.connectivityResult.collectAsState()
    val isMonitoringActive by viewModel.isMonitoringActive.collectAsState()

    // Handle Back Press
    BackHandler(enabled = currentScreen != AppScreen.SPLASH) {
        val handled = viewModel.navigateBack()
        if (!handled && currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.WIFI_GATE) {
            viewModel.navigateTo(if (wifiDetails.isConnected) AppScreen.DASHBOARD else AppScreen.WIFI_GATE)
        }
    }

    when (currentScreen) {
        AppScreen.SPLASH -> {
            SplashScreen(
                onTimeout = {
                    if (wifiDetails.isConnected) {
                        viewModel.navigateTo(AppScreen.DASHBOARD)
                    } else {
                        viewModel.navigateTo(AppScreen.WIFI_GATE)
                    }
                }
            )
        }

        AppScreen.WIFI_GATE -> {
            WifiGateScreen(
                wifiDetails = wifiDetails,
                onRefresh = { viewModel.refreshWifiStatus() },
                onUnlockDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.DASHBOARD -> {
            DashboardScreen(
                wifiDetails = wifiDetails,
                savedScans = savedScans,
                onRefresh = { viewModel.refreshWifiStatus() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.AR_SCANNER -> {
            ArScannerScreen(
                wifiDetails = wifiDetails,
                spatialMarkers = spatialMarkers,
                isOverlayVisible = isOverlayVisible,
                isMeasurementPaused = isMeasurementPaused,
                isCameraFallbackMode = isCameraFallbackMode,
                onToggleOverlay = { viewModel.toggleOverlayVisibility() },
                onTogglePause = { viewModel.toggleMeasurementPause() },
                onSetCameraFallbackMode = { viewModel.setCameraFallbackMode(it) },
                onAddSpatialMarker = { x, y, label -> viewModel.addSpatialMarker(x, y, label) },
                onClearSpatialMarkers = { viewModel.clearSpatialMarkers() },
                onNavigateBack = { viewModel.navigateBack() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.ROOM_SCAN -> {
            RoomScanScreen(
                wifiDetails = wifiDetails,
                isTesting = isRoomTesting,
                samples = roomSamples,
                secondsElapsed = roomSecondsElapsed,
                onStartTest = { duration -> viewModel.startRoomTest(duration) },
                onCancelTest = { viewModel.cancelRoomTest() },
                onSaveScan = { name, notes -> viewModel.saveRoomScan(name, notes) },
                onNavigateBack = { viewModel.navigateBack() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.SAVED_ROOMS -> {
            SavedRoomsScreen(
                savedScans = savedScans,
                roomScanRepository = viewModel.roomScanRepository,
                onDeleteScan = { viewModel.deleteScan(it) },
                onDeleteAllScans = { viewModel.deleteAllScans() },
                onStartNewMeasurement = { viewModel.navigateTo(AppScreen.ROOM_SCAN) },
                onNavigateBack = { viewModel.navigateBack() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.DIAGNOSTICS -> {
            DiagnosticsScreen(
                wifiDetails = wifiDetails,
                isMonitoringActive = isMonitoringActive,
                onToggleMonitoring = { viewModel.setMonitoringActive(it) },
                onRefresh = { viewModel.refreshWifiStatus() },
                onNavigateBack = { viewModel.navigateBack() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.NEARBY_NETWORKS -> {
            NearbyNetworksScreen(
                currentWifi = wifiDetails,
                nearbyNetworks = nearbyNetworks,
                isScanning = isScanningNearby,
                onScan = { viewModel.scanNearbyNetworks() },
                onNavigateBack = { viewModel.navigateBack() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.LOCAL_NETWORK -> {
            LocalNetworkScreen(
                currentWifi = wifiDetails,
                discoveredDevices = discoveredDevices,
                isScanning = isScanningDevices,
                progress = discoveryProgress,
                onStartScan = { viewModel.startLocalDeviceDiscovery() },
                onNavigateBack = { viewModel.navigateBack() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.CONNECTIVITY_TEST -> {
            ConnectivityTestScreen(
                testResult = connectivityResult,
                onRunTest = { viewModel.runConnectivityTest() },
                onNavigateBack = { viewModel.navigateBack() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.ABOUT -> {
            AboutScreen(
                onClearImageCache = { viewModel.clearLogoDiskCache() },
                onDeleteAllScans = { viewModel.deleteAllScans() },
                onNavigateBack = { viewModel.navigateBack() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        AppScreen.SETTINGS -> {
            SettingsScreen(
                onNavigateBack = { viewModel.navigateBack() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    }
}
