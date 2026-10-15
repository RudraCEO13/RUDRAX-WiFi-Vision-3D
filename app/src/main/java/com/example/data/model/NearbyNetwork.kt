package com.example.data.model

data class NearbyNetwork(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val frequencyMhz: Int,
    val band: String,
    val capabilities: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val quality: SignalQuality
        get() = when {
            rssi >= -50 -> SignalQuality.EXCELLENT
            rssi >= -60 -> SignalQuality.GOOD
            rssi >= -67 -> SignalQuality.FAIR
            rssi >= -75 -> SignalQuality.WEAK
            else -> SignalQuality.VERY_WEAK
        }
}

data class DiscoveredDevice(
    val ip: String,
    val hostname: String?,
    val isReachable: Boolean,
    val responseTimeMs: Long,
    val isGateway: Boolean = false,
    val isCurrentDevice: Boolean = false,
    val discoveryMethod: String = "ICMP / TCP Socket Probe",
    val lastSeenTimestamp: Long = System.currentTimeMillis()
)

data class ConnectivityTestResult(
    val isInternetReachable: Boolean = false,
    val dnsResolved: Boolean = false,
    val dnsResolutionTimeMs: Long = 0,
    val httpLatencyMs: Long = 0,
    val targetEndpoint: String = "dns.google (8.8.8.8)",
    val testedTimestamp: Long = System.currentTimeMillis(),
    val isTesting: Boolean = false,
    val notes: String = ""
)
