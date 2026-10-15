package com.example.data.model

data class WifiConnectionDetails(
    val isWifiEnabled: Boolean = false,
    val isConnected: Boolean = false,
    val ssid: String = "Unavailable",
    val bssid: String = "Unavailable",
    val rssi: Int = 0,
    val frequencyMhz: Int = 0,
    val frequencyBand: String = "Unavailable",
    val linkSpeedMbps: Int = 0,
    val ipAddress: String = "Unavailable",
    val gateway: String = "Unavailable",
    val subnetMask: String = "Unavailable",
    val transportType: String = "Wi-Fi",
    val lastUpdateTimestamp: Long = System.currentTimeMillis(),
    val trend: SignalTrend = SignalTrend.STABLE,
    val isStale: Boolean = false
) {
    val signalQuality: SignalQuality
        get() = when {
            !isConnected || rssi == 0 -> SignalQuality.UNAVAILABLE
            rssi >= -50 -> SignalQuality.EXCELLENT
            rssi >= -60 -> SignalQuality.GOOD
            rssi >= -67 -> SignalQuality.FAIR
            rssi >= -75 -> SignalQuality.WEAK
            else -> SignalQuality.VERY_WEAK
        }

    val signalPercentage: Int
        get() = when {
            !isConnected || rssi == 0 -> 0
            rssi >= -50 -> 100
            rssi <= -100 -> 0
            else -> 2 * (rssi + 100)
        }
}

enum class SignalQuality(val label: String) {
    EXCELLENT("EXCELLENT"),
    GOOD("GOOD"),
    FAIR("FAIR"),
    WEAK("WEAK"),
    VERY_WEAK("VERY WEAK"),
    UNAVAILABLE("UNAVAILABLE")
}

enum class SignalTrend(val label: String) {
    STABLE("STABLE"),
    IMPROVING("IMPROVING"),
    DECLINING("DECLINING")
}
