package com.example

import com.example.data.model.RoomScanEntity
import com.example.data.model.SignalQuality
import com.example.data.model.WifiConnectionDetails
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testSignalQualityThresholds() {
        val excellent = WifiConnectionDetails(isConnected = true, rssi = -42)
        assertEquals(SignalQuality.EXCELLENT, excellent.signalQuality)

        val good = WifiConnectionDetails(isConnected = true, rssi = -55)
        assertEquals(SignalQuality.GOOD, good.signalQuality)

        val fair = WifiConnectionDetails(isConnected = true, rssi = -64)
        assertEquals(SignalQuality.FAIR, fair.signalQuality)

        val weak = WifiConnectionDetails(isConnected = true, rssi = -71)
        assertEquals(SignalQuality.WEAK, weak.signalQuality)

        val veryWeak = WifiConnectionDetails(isConnected = true, rssi = -82)
        assertEquals(SignalQuality.VERY_WEAK, veryWeak.signalQuality)

        val disconnected = WifiConnectionDetails(isConnected = false, rssi = -45)
        assertEquals(SignalQuality.UNAVAILABLE, disconnected.signalQuality)
    }

    @Test
    fun testSignalPercentageCalculation() {
        val maxSignal = WifiConnectionDetails(isConnected = true, rssi = -40)
        assertEquals(100, maxSignal.signalPercentage)

        val zeroSignal = WifiConnectionDetails(isConnected = true, rssi = -105)
        assertEquals(0, zeroSignal.signalPercentage)

        val midSignal = WifiConnectionDetails(isConnected = true, rssi = -75)
        // 2 * (-75 + 100) = 50%
        assertEquals(50, midSignal.signalPercentage)
    }

    @Test
    fun testRoomScanEntityCreation() {
        val entity = RoomScanEntity(
            roomName = "Living Room",
            rssi = -45,
            frequencyBand = "5 GHz",
            linkSpeedMbps = 866,
            sampleDurationSeconds = 6,
            sampleCount = 6,
            minRssi = -48,
            maxRssi = -42,
            avgRssi = -45,
            qualityCategory = "EXCELLENT",
            notes = "Near Router"
        )
        assertEquals("Living Room", entity.roomName)
        assertEquals(-45, entity.avgRssi)
        assertEquals(6, entity.sampleCount)
        assertEquals("EXCELLENT", entity.qualityCategory)
    }
}
