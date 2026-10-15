package com.example.data.repository

import com.example.data.model.ConnectivityTestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class ConnectivityRepository {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun performConnectivityTest(): ConnectivityTestResult = withContext(Dispatchers.IO) {
        val targetDomain = "clients3.google.com"
        val testUrl = "https://clients3.google.com/generate_204"

        var dnsResolved = false
        var dnsTimeMs = 0L
        var httpLatencyMs = 0L
        var isInternetReachable = false
        var notes = ""

        // Phase 1: DNS Resolution Test
        val dnsStart = System.currentTimeMillis()
        try {
            val addresses = InetAddress.getAllByName(targetDomain)
            dnsTimeMs = System.currentTimeMillis() - dnsStart
            if (addresses.isNotEmpty()) {
                dnsResolved = true
            }
        } catch (e: Exception) {
            dnsTimeMs = System.currentTimeMillis() - dnsStart
            notes = "DNS resolution failed: ${e.localizedMessage ?: "Domain unresolvable"}"
        }

        // Phase 2: HTTP Handshake & Round-Trip Latency
        if (dnsResolved) {
            val httpStart = System.currentTimeMillis()
            try {
                val request = Request.Builder()
                    .url(testUrl)
                    .header("User-Agent", "RudraX-WiFi-Vision-3D/1.0")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    httpLatencyMs = System.currentTimeMillis() - httpStart
                    if (response.isSuccessful || response.code == 204) {
                        isInternetReachable = true
                        notes = "HTTP Status ${response.code} (No Content). WAN connectivity verified."
                    } else {
                        notes = "Gateway reached with HTTP ${response.code} (Captive Portal or Filtered)"
                    }
                }
            } catch (e: Exception) {
                httpLatencyMs = System.currentTimeMillis() - httpStart
                notes = "HTTP ping failed: ${e.localizedMessage ?: "Connection timed out"}"
            }
        } else {
            notes = "No internet DNS route available on current Wi-Fi."
        }

        ConnectivityTestResult(
            isInternetReachable = isInternetReachable,
            dnsResolved = dnsResolved,
            dnsResolutionTimeMs = dnsTimeMs,
            httpLatencyMs = httpLatencyMs,
            targetEndpoint = targetDomain,
            testedTimestamp = System.currentTimeMillis(),
            isTesting = false,
            notes = notes
        )
    }
}
