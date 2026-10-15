package com.example.data.repository

import com.example.data.db.RoomScanDao
import com.example.data.model.RoomScanEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoomScanRepository(private val dao: RoomScanDao) {

    fun getAllScans(): Flow<List<RoomScanEntity>> = dao.getAllScans()

    fun getScansRanked(): Flow<List<RoomScanEntity>> = dao.getScansRanked()

    suspend fun saveScan(scan: RoomScanEntity): Long = withContext(Dispatchers.IO) {
        dao.insertScan(scan)
    }

    suspend fun deleteScan(scan: RoomScanEntity) = withContext(Dispatchers.IO) {
        dao.deleteScan(scan)
    }

    suspend fun deleteScanById(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteScanById(id)
    }

    suspend fun deleteAllScans() = withContext(Dispatchers.IO) {
        dao.deleteAllScans()
    }

    suspend fun generateExportReport(scans: List<RoomScanEntity>): String = withContext(Dispatchers.Default) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("====================================================\n")
        sb.append("RUDRAX WiFi Vision 3D — Room Signal Audit Report\n")
        sb.append("Powered by RudraX Technologies Studio\n")
        sb.append("Exported: ${dateFormat.format(Date())}\n")
        sb.append("====================================================\n\n")

        if (scans.isEmpty()) {
            sb.append("No saved room scans recorded.\n")
            return@withContext sb.toString()
        }

        val sorted = scans.sortedByDescending { it.avgRssi }
        val strongest = sorted.first()
        val weakest = sorted.last()

        sb.append("EXECUTIVE SUMMARY:\n")
        sb.append("- Total Locations Tested: ${scans.size}\n")
        sb.append("- Best Signal Area: ${strongest.roomName} (${strongest.avgRssi} dBm - ${strongest.qualityCategory})\n")
        sb.append("- Weakest Signal Area: ${weakest.roomName} (${weakest.avgRssi} dBm - ${weakest.qualityCategory})\n")
        sb.append("\n----------------------------------------------------\n")
        sb.append("ROOM-BY-ROOM MEASUREMENTS:\n\n")

        sorted.forEachIndexed { index, scan ->
            sb.append("${index + 1}. [${scan.roomName}]\n")
            sb.append("   - Date/Time: ${dateFormat.format(Date(scan.timestamp))}\n")
            sb.append("   - Avg RSSI: ${scan.avgRssi} dBm (${scan.qualityCategory})\n")
            sb.append("   - Observed Range: Min ${scan.minRssi} dBm, Max ${scan.maxRssi} dBm\n")
            sb.append("   - Band: ${scan.frequencyBand} | Link Speed: ${scan.linkSpeedMbps} Mbps\n")
            sb.append("   - Samples: ${scan.sampleCount} collected over ${scan.sampleDurationSeconds}s\n")
            if (scan.notes.isNotBlank()) {
                sb.append("   - Notes: ${scan.notes}\n")
            }
            sb.append("\n")
        }

        sb.append("----------------------------------------------------\n")
        sb.append("MEASUREMENT METHODOLOGY & DISCLAIMER:\n")
        sb.append("Measurements reflect smartphone antenna RSSI at test locations.\n")
        sb.append("Physical materials (concrete, water, metal) and interference affect reception.\n")
        sb.append("RSSI does not guarantee downstream internet ISP throughput.\n\n")
        sb.append("© 2026 RudraX Technologies Studio. All rights reserved.\n")

        sb.toString()
    }
}
