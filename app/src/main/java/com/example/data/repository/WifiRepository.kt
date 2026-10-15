package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.example.data.model.NearbyNetwork
import com.example.data.model.SignalTrend
import com.example.data.model.WifiConnectionDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.NetworkInterface

class WifiRepository(private val context: Context) {

    private val wifiManager: WifiManager? =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    private val connectivityManager: ConnectivityManager? =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val recentRssiHistory = ArrayDeque<Int>(10)
    private var lastRecordedTime = System.currentTimeMillis()

    /**
     * Check whether Wi-Fi is enabled at hardware/system level.
     */
    fun isWifiEnabled(): Boolean {
        return wifiManager?.isWifiEnabled == true
    }

    /**
     * Read instantaneous Wi-Fi connection snapshot from system APIs.
     */
    fun getCurrentWifiSnapshot(): WifiConnectionDetails {
        val cm = connectivityManager
        val wm = wifiManager

        val isEnabled = isWifiEnabled()
        if (!isEnabled) {
            return WifiConnectionDetails(
                isWifiEnabled = false,
                isConnected = false,
                lastUpdateTimestamp = System.currentTimeMillis()
            )
        }

        val activeNetwork = cm?.activeNetwork
        val caps = if (activeNetwork != null) cm.getNetworkCapabilities(activeNetwork) else null
        val linkProps = if (activeNetwork != null) cm.getLinkProperties(activeNetwork) else null

        val hasWifiTransport = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        if (!hasWifiTransport || activeNetwork == null) {
            return WifiConnectionDetails(
                isWifiEnabled = true,
                isConnected = false,
                lastUpdateTimestamp = System.currentTimeMillis()
            )
        }

        // Retrieve WifiInfo (from TransportInfo on API 29+ or WifiManager.connectionInfo)
        var wifiInfo: WifiInfo? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && caps != null) {
            wifiInfo = caps.transportInfo as? WifiInfo
        }
        if (wifiInfo == null) {
            @Suppress("DEPRECATION")
            wifiInfo = wm?.connectionInfo
        }

        val rawSsid = wifiInfo?.ssid
        val cleanSsid = cleanSsidString(rawSsid)
        val rawBssid = wifiInfo?.bssid ?: "Unavailable"

        val rssi = wifiInfo?.rssi ?: 0
        val freq = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wifiInfo?.frequency ?: 0
        } else 0
        val band = formatFrequencyBand(freq)
        val linkSpeed = wifiInfo?.linkSpeed ?: 0

        // Determine IP & Gateway
        var localIp = "Unavailable"
        var gateway = "Unavailable"
        var subnetMask = "Unavailable"

        if (linkProps != null) {
            for (linkAddr in linkProps.linkAddresses) {
                val addr = linkAddr.address
                if (addr is Inet4Address && !addr.isLoopbackAddress) {
                    localIp = addr.hostAddress ?: "Unavailable"
                    val prefix = linkAddr.prefixLength
                    subnetMask = prefixLengthToSubnetMask(prefix)
                    break
                }
            }
            for (route in linkProps.routes) {
                val gw = route.gateway
                if (gw is Inet4Address && !gw.isLoopbackAddress) {
                    gateway = gw.hostAddress ?: "Unavailable"
                    break
                }
            }
        }

        // Fallback IP reading via NetworkInterface if linkProps was unavailable
        if (localIp == "Unavailable") {
            localIp = getLocalIpv4Fallback()
        }

        // Calculate Signal Trend
        val trend = calculateTrend(rssi)
        lastRecordedTime = System.currentTimeMillis()

        return WifiConnectionDetails(
            isWifiEnabled = true,
            isConnected = true,
            ssid = cleanSsid,
            bssid = if (rawBssid == "02:00:00:00:00:00" || rawBssid.isBlank()) "Protected by Android OS" else rawBssid,
            rssi = rssi,
            frequencyMhz = freq,
            frequencyBand = band,
            linkSpeedMbps = if (linkSpeed > 0) linkSpeed else 0,
            ipAddress = localIp,
            gateway = gateway,
            subnetMask = subnetMask,
            transportType = "Wi-Fi (802.11)",
            lastUpdateTimestamp = lastRecordedTime,
            trend = trend,
            isStale = false
        )
    }

    /**
     * Continuous reactive Flow of Wi-Fi details.
     */
    fun observeWifiDetails(pollIntervalMs: Long = 1000L): Flow<WifiConnectionDetails> = callbackFlow {
        val cm = connectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(getCurrentWifiSnapshot())
            }
            override fun onLost(network: Network) {
                trySend(getCurrentWifiSnapshot())
            }
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                trySend(getCurrentWifiSnapshot())
            }
        }

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        cm?.registerNetworkCallback(request, callback)
        trySend(getCurrentWifiSnapshot())

        // Also run periodic poll for smooth RSSI updates while on screen
        val pollerJob = kotlinx.coroutines.CoroutineScope(Dispatchers.Default).launchPeriodic(pollIntervalMs) {
            trySend(getCurrentWifiSnapshot())
        }

        awaitClose {
            pollerJob.cancel()
            try {
                cm?.unregisterNetworkCallback(callback)
            } catch (_: Exception) {}
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Retrieve scan results of nearby Wi-Fi networks where permitted.
     */
    suspend fun getNearbyNetworks(): List<NearbyNetwork> = withContext(Dispatchers.IO) {
        val wm = wifiManager ?: return@withContext emptyList()
        try {
            @Suppress("DEPRECATION")
            val scanResults: List<ScanResult>? = wm.scanResults
            if (scanResults.isNullOrEmpty()) {
                return@withContext emptyList()
            }

            scanResults.mapNotNull { result ->
                val ssid = cleanSsidString(result.SSID)
                if (ssid.isEmpty() || ssid == "Unavailable") null
                else {
                    NearbyNetwork(
                        ssid = ssid,
                        bssid = result.BSSID ?: "Hidden",
                        rssi = result.level,
                        frequencyMhz = result.frequency,
                        band = formatFrequencyBand(result.frequency),
                        capabilities = result.capabilities ?: "Standard",
                        timestamp = System.currentTimeMillis()
                    )
                }
            }.sortedByDescending { it.rssi }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun cleanSsidString(raw: String?): String {
        if (raw == null || raw.isBlank()) return "Unavailable"
        var clean = raw.trim()
        if (clean.startsWith("\"") && clean.endsWith("\"") && clean.length > 1) {
            clean = clean.substring(1, clean.length - 1)
        }
        return if (clean == "<unknown ssid>" || clean.isEmpty()) "Protected / Location Required" else clean
    }

    private fun formatFrequencyBand(freqMhz: Int): String {
        return when {
            freqMhz in 2400..2495 -> "2.4 GHz"
            freqMhz in 5150..5895 -> "5 GHz"
            freqMhz in 5925..7125 -> "6 GHz (Wi-Fi 6E/7)"
            freqMhz > 0 -> "$freqMhz MHz"
            else -> "Unavailable"
        }
    }

    private fun prefixLengthToSubnetMask(prefix: Int): String {
        if (prefix <= 0 || prefix > 32) return "255.255.255.0 (/24)"
        val mask = (0xffffffffL shl (32 - prefix)) and 0xffffffffL
        val b1 = (mask shr 24 and 0xff)
        val b2 = (mask shr 16 and 0xff)
        val b3 = (mask shr 8 and 0xff)
        val b4 = (mask and 0xff)
        return "$b1.$b2.$b3.$b4 (/$prefix)"
    }

    private fun getLocalIpv4Fallback(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return "Unavailable"
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: "Unavailable"
                    }
                }
            }
        } catch (_: Exception) {}
        return "Unavailable"
    }

    private fun calculateTrend(currentRssi: Int): SignalTrend {
        if (currentRssi == 0) return SignalTrend.STABLE
        if (recentRssiHistory.size >= 6) {
            recentRssiHistory.removeFirst()
        }
        recentRssiHistory.addLast(currentRssi)

        if (recentRssiHistory.size < 3) return SignalTrend.STABLE
        val firstHalfAvg = recentRssiHistory.take(recentRssiHistory.size / 2).average()
        val secondHalfAvg = recentRssiHistory.takeLast(recentRssiHistory.size / 2).average()

        val diff = secondHalfAvg - firstHalfAvg
        return when {
            diff >= 2.0 -> SignalTrend.IMPROVING
            diff <= -2.0 -> SignalTrend.DECLINING
            else -> SignalTrend.STABLE
        }
    }
}

private fun kotlinx.coroutines.CoroutineScope.launchPeriodic(
    intervalMs: Long,
    action: suspend () -> Unit
): kotlinx.coroutines.Job {
    return this.launch {
        while (this.isActive) {
            kotlinx.coroutines.delay(intervalMs)
            action()
        }
    }
}
