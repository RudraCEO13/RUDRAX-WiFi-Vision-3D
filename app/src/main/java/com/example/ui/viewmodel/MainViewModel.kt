package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import com.example.RudraXApp
import com.example.data.model.ConnectivityTestResult
import com.example.data.model.DiscoveredDevice
import com.example.data.model.NearbyNetwork
import com.example.data.model.RoomScanEntity
import com.example.data.model.SignalQuality
import com.example.data.model.WifiConnectionDetails
import com.example.data.repository.ConnectivityRepository
import com.example.data.repository.NetworkDiscoveryRepository
import com.example.data.repository.RoomScanRepository
import com.example.data.repository.WifiRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    WIFI_GATE,
    DASHBOARD,
    AR_SCANNER,
    ROOM_SCAN,
    SAVED_ROOMS,
    DIAGNOSTICS,
    NEARBY_NETWORKS,
    LOCAL_NETWORK,
    CONNECTIVITY_TEST,
    ABOUT,
    SETTINGS
}

data class SpatialMarker(
    val id: Long = System.currentTimeMillis(),
    val normalizedX: Float, // 0.0f to 1.0f on camera preview canvas
    val normalizedY: Float,
    val rssi: Int,
    val label: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val rudraXApp = application as RudraXApp
    val wifiRepository = WifiRepository(application)
    val roomScanRepository = RoomScanRepository(rudraXApp.database.roomScanDao())
    val networkDiscoveryRepository = NetworkDiscoveryRepository()
    val connectivityRepository = ConnectivityRepository()

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenStack = ArrayDeque<AppScreen>()

    // Wi-Fi Status
    private val _wifiDetails = MutableStateFlow(WifiConnectionDetails())
    val wifiDetails: StateFlow<WifiConnectionDetails> = _wifiDetails.asStateFlow()

    // Room Scans from Room DB
    val allRoomScans: StateFlow<List<RoomScanEntity>> = roomScanRepository.getAllScans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rankedRoomScans: StateFlow<List<RoomScanEntity>> = roomScanRepository.getScansRanked()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room Testing In-Progress State
    private val _isRoomTesting = MutableStateFlow(false)
    val isRoomTesting: StateFlow<Boolean> = _isRoomTesting.asStateFlow()

    private val _roomTestSamples = MutableStateFlow<List<Int>>(emptyList())
    val roomTestSamples: StateFlow<List<Int>> = _roomTestSamples.asStateFlow()

    private val _roomTestSecondsElapsed = MutableStateFlow(0)
    val roomTestSecondsElapsed: StateFlow<Int> = _roomTestSecondsElapsed.asStateFlow()

    private var roomTestJob: Job? = null

    // AR Scanner State
    private val _spatialMarkers = MutableStateFlow<List<SpatialMarker>>(emptyList())
    val spatialMarkers: StateFlow<List<SpatialMarker>> = _spatialMarkers.asStateFlow()

    private val _isOverlayVisible = MutableStateFlow(true)
    val isOverlayVisible: StateFlow<Boolean> = _isOverlayVisible.asStateFlow()

    private val _isMeasurementPaused = MutableStateFlow(false)
    val isMeasurementPaused: StateFlow<Boolean> = _isMeasurementPaused.asStateFlow()

    private val _isCameraFallbackMode = MutableStateFlow(false)
    val isCameraFallbackMode: StateFlow<Boolean> = _isCameraFallbackMode.asStateFlow()

    // Nearby Networks State
    private val _nearbyNetworks = MutableStateFlow<List<NearbyNetwork>>(emptyList())
    val nearbyNetworks: StateFlow<List<NearbyNetwork>> = _nearbyNetworks.asStateFlow()

    private val _isScanningNearby = MutableStateFlow(false)
    val isScanningNearby: StateFlow<Boolean> = _isScanningNearby.asStateFlow()

    // Local Network Discovery State
    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private val _isScanningDevices = MutableStateFlow(false)
    val isScanningDevices: StateFlow<Boolean> = _isScanningDevices.asStateFlow()

    private val _discoveryProgress = MutableStateFlow(Pair(0, 0))
    val discoveryProgress: StateFlow<Pair<Int, Int>> = _discoveryProgress.asStateFlow()

    // Internet Connectivity State
    private val _connectivityResult = MutableStateFlow(ConnectivityTestResult())
    val connectivityResult: StateFlow<ConnectivityTestResult> = _connectivityResult.asStateFlow()

    // Monitoring State for Diagnostics
    private val _isMonitoringActive = MutableStateFlow(true)
    val isMonitoringActive: StateFlow<Boolean> = _isMonitoringActive.asStateFlow()

    init {
        // Start continuous Wi-Fi updates
        viewModelScope.launch {
            wifiRepository.observeWifiDetails(pollIntervalMs = 1200L).collect { details ->
                if (!_isMeasurementPaused.value) {
                    _wifiDetails.value = details
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenStack.addLast(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeLast()
            return true
        }
        if (_currentScreen.value != AppScreen.DASHBOARD && _currentScreen.value != AppScreen.WIFI_GATE) {
            _currentScreen.value = if (_wifiDetails.value.isConnected) AppScreen.DASHBOARD else AppScreen.WIFI_GATE
            return true
        }
        return false
    }

    fun refreshWifiStatus() {
        _wifiDetails.value = wifiRepository.getCurrentWifiSnapshot()
    }

    fun setMonitoringActive(active: Boolean) {
        _isMonitoringActive.value = active
    }

    fun toggleOverlayVisibility() {
        _isOverlayVisible.value = !_isOverlayVisible.value
    }

    fun toggleMeasurementPause() {
        _isMeasurementPaused.value = !_isMeasurementPaused.value
    }

    fun setCameraFallbackMode(enabled: Boolean) {
        _isCameraFallbackMode.value = enabled
    }

    fun addSpatialMarker(normX: Float, normY: Float, label: String) {
        val currentRssi = _wifiDetails.value.rssi
        val newMarker = SpatialMarker(
            normalizedX = normX.coerceIn(0.1f, 0.9f),
            normalizedY = normY.coerceIn(0.1f, 0.9f),
            rssi = if (currentRssi == 0) -60 else currentRssi,
            label = label.ifBlank { "Point #${_spatialMarkers.value.size + 1}" }
        )
        _spatialMarkers.value = _spatialMarkers.value + newMarker
    }

    fun clearSpatialMarkers() {
        _spatialMarkers.value = emptyList()
    }

    // Room testing logic
    fun startRoomTest(targetDurationSeconds: Int = 6) {
        roomTestJob?.cancel()
        _isRoomTesting.value = true
        _roomTestSamples.value = emptyList()
        _roomTestSecondsElapsed.value = 0

        roomTestJob = viewModelScope.launch {
            val samples = mutableListOf<Int>()
            for (sec in 1..targetDurationSeconds) {
                delay(1000)
                _roomTestSecondsElapsed.value = sec
                val current = wifiRepository.getCurrentWifiSnapshot()
                val currentRssi = if (current.rssi != 0) current.rssi else -65
                samples.add(currentRssi)
                _roomTestSamples.value = samples.toList()
            }
            _isRoomTesting.value = false
        }
    }

    fun cancelRoomTest() {
        roomTestJob?.cancel()
        _isRoomTesting.value = false
        _roomTestSamples.value = emptyList()
        _roomTestSecondsElapsed.value = 0
    }

    fun saveRoomScan(
        roomName: String,
        notes: String = ""
    ) {
        val samples = _roomTestSamples.value
        val validSamples = if (samples.isNotEmpty()) samples else listOf(_wifiDetails.value.rssi.takeIf { it != 0 } ?: -60)
        val minRssi = validSamples.minOrNull() ?: -70
        val maxRssi = validSamples.maxOrNull() ?: -50
        val avgRssi = validSamples.average().toInt()

        val quality = when {
            avgRssi >= -50 -> SignalQuality.EXCELLENT
            avgRssi >= -60 -> SignalQuality.GOOD
            avgRssi >= -67 -> SignalQuality.FAIR
            avgRssi >= -75 -> SignalQuality.WEAK
            else -> SignalQuality.VERY_WEAK
        }.label

        val entity = RoomScanEntity(
            roomName = roomName.ifBlank { "Custom Location" },
            timestamp = System.currentTimeMillis(),
            rssi = _wifiDetails.value.rssi,
            frequencyBand = _wifiDetails.value.frequencyBand,
            linkSpeedMbps = _wifiDetails.value.linkSpeedMbps,
            sampleDurationSeconds = _roomTestSecondsElapsed.value.coerceAtLeast(1),
            sampleCount = validSamples.size,
            minRssi = minRssi,
            maxRssi = maxRssi,
            avgRssi = avgRssi,
            qualityCategory = quality,
            notes = notes
        )

        viewModelScope.launch {
            roomScanRepository.saveScan(entity)
            cancelRoomTest()
            navigateTo(AppScreen.SAVED_ROOMS)
        }
    }

    fun deleteScan(scan: RoomScanEntity) {
        viewModelScope.launch {
            roomScanRepository.deleteScan(scan)
        }
    }

    fun deleteAllScans() {
        viewModelScope.launch {
            roomScanRepository.deleteAllScans()
        }
    }

    // Nearby Wi-Fi scan
    fun scanNearbyNetworks() {
        viewModelScope.launch {
            _isScanningNearby.value = true
            val results = wifiRepository.getNearbyNetworks()
            _nearbyNetworks.value = results
            _isScanningNearby.value = false
        }
    }

    // Local device discovery
    fun startLocalDeviceDiscovery() {
        viewModelScope.launch {
            _isScanningDevices.value = true
            _discoveryProgress.value = Pair(0, 1)
            val current = wifiRepository.getCurrentWifiSnapshot()
            val devices = networkDiscoveryRepository.discoverLocalDevices(
                localIp = current.ipAddress,
                gatewayIp = current.gateway,
                onProgress = { currentCount, total ->
                    _discoveryProgress.value = Pair(currentCount, total)
                }
            )
            _discoveredDevices.value = devices
            _isScanningDevices.value = false
        }
    }

    // Internet connectivity test
    fun runConnectivityTest() {
        viewModelScope.launch {
            _connectivityResult.value = _connectivityResult.value.copy(isTesting = true)
            val result = connectivityRepository.performConnectivityTest()
            _connectivityResult.value = result
        }
    }

    // Privacy & cache wipe
    fun clearLogoDiskCache() {
        viewModelScope.launch {
            val imageLoader = rudraXApp.imageLoader
            imageLoader.diskCache?.clear()
            imageLoader.memoryCache?.clear()
        }
    }
}
