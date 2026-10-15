package com.example.data.repository

import com.example.data.model.DiscoveredDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class NetworkDiscoveryRepository {

    /**
     * Perform bounded local-network scan on the current device's IPv4 subnet.
     * Respects Android sandbox rules, limits concurrency, and restricts probing
     * to a safe range (max 50-60 target IPs around gateway and device).
     */
    suspend fun discoverLocalDevices(
        localIp: String,
        gatewayIp: String,
        onProgress: (Int, Int) -> Unit
    ): List<DiscoveredDevice> = withContext(Dispatchers.IO) {
        val discoveredList = mutableListOf<DiscoveredDevice>()

        if (localIp == "Unavailable" || !localIp.contains(".")) {
            return@withContext emptyList()
        }

        val parts = localIp.split(".")
        if (parts.size != 4) return@withContext emptyList()

        val prefix = "${parts[0]}.${parts[1]}.${parts[2]}"
        val myLastOctet = parts[3].toIntOrNull() ?: 1

        val gatewayLastOctet = if (gatewayIp.startsWith(prefix)) {
            gatewayIp.substringAfterLast(".").toIntOrNull() ?: 1
        } else 1

        // Target IPs: Probe common network hosts (1 to 30, plus neighborhood around device and gateway, max 45 targets)
        val targetOctets = mutableSetOf<Int>()
        targetOctets.add(gatewayLastOctet)
        targetOctets.add(myLastOctet)

        // Add typical router/AP addresses
        for (i in 1..15) targetOctets.add(i)
        // Add typical upper addresses (250..254)
        for (i in 250..254) targetOctets.add(i)
        // Add neighborhood of this device
        for (i in (myLastOctet - 8)..(myLastOctet + 8)) {
            if (i in 1..254) targetOctets.add(i)
        }

        val sortedTargets = targetOctets.sorted()
        val total = sortedTargets.size
        var completed = 0

        // Limit concurrent socket probes to 10 to avoid saturating network stack
        val semaphore = Semaphore(10)

        coroutineScope {
            val deferreds = sortedTargets.map { octet ->
                async {
                    semaphore.withPermit {
                        val hostIp = "$prefix.$octet"
                        val isGw = (hostIp == gatewayIp)
                        val isSelf = (hostIp == localIp)

                        val device = probeHost(hostIp, isGw, isSelf)
                        completed++
                        onProgress(completed, total)
                        device
                    }
                }
            }

            val results = deferreds.awaitAll()
            results.filterNotNull().forEach { discoveredList.add(it) }
        }

        // Return sorted: Gateway first, then self, then other discovered IPs
        discoveredList.sortedWith(
            compareByDescending<DiscoveredDevice> { it.isGateway }
                .thenByDescending { it.isCurrentDevice }
                .thenBy { it.ip }
        )
    }

    private fun probeHost(hostIp: String, isGateway: Boolean, isSelf: Boolean): DiscoveredDevice? {
        val startTime = System.currentTimeMillis()
        var reachable = false
        var hostname: String? = null

        if (isSelf) {
            return DiscoveredDevice(
                ip = hostIp,
                hostname = "This Android Device",
                isReachable = true,
                responseTimeMs = 1,
                isGateway = false,
                isCurrentDevice = true,
                discoveryMethod = "Local Host Interface",
                lastSeenTimestamp = System.currentTimeMillis()
            )
        }

        try {
            val inet = InetAddress.getByName(hostIp)
            // Try standard ICMP reachability check (short 250ms timeout)
            reachable = inet.isReachable(250)
            if (reachable) {
                hostname = inet.canonicalHostName
            }
        } catch (_: Exception) {}

        // If ICMP ping is blocked (common on many home routers/smart TVs), probe standard local service ports (80 HTTP, 443 HTTPS, 53 DNS, 5353 mDNS, 8080)
        if (!reachable) {
            val portsToProbe = if (isGateway) intArrayOf(80, 443, 53) else intArrayOf(80, 8080, 443)
            for (port in portsToProbe) {
                try {
                    val socket = Socket()
                    socket.connect(InetSocketAddress(hostIp, port), 250)
                    socket.close()
                    reachable = true
                    break
                } catch (_: Exception) {}
            }
        }

        if (reachable || isGateway) {
            val elapsed = (System.currentTimeMillis() - startTime).coerceAtLeast(1)
            val cleanHostname = if (hostname != null && hostname != hostIp && !hostname.contains("unknown")) {
                hostname
            } else if (isGateway) {
                "Default Gateway / Router"
            } else null

            return DiscoveredDevice(
                ip = hostIp,
                hostname = cleanHostname,
                isReachable = reachable,
                responseTimeMs = elapsed,
                isGateway = isGateway,
                isCurrentDevice = false,
                discoveryMethod = if (isGateway) "Gateway Route Table & Port Probe" else "TCP/ICMP Active Reachability",
                lastSeenTimestamp = System.currentTimeMillis()
            )
        }

        return null
    }
}
