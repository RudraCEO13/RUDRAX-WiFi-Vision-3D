package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_scans")
data class RoomScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val roomName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val rssi: Int,
    val frequencyBand: String,
    val linkSpeedMbps: Int,
    val sampleDurationSeconds: Int,
    val sampleCount: Int,
    val minRssi: Int,
    val maxRssi: Int,
    val avgRssi: Int,
    val qualityCategory: String,
    val notes: String = ""
)
