package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RoomScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomScanDao {
    @Query("SELECT * FROM room_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<RoomScanEntity>>

    @Query("SELECT * FROM room_scans ORDER BY avgRssi DESC")
    fun getScansRanked(): Flow<List<RoomScanEntity>>

    @Query("SELECT * FROM room_scans WHERE id = :id")
    suspend fun getScanById(id: Long): RoomScanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: RoomScanEntity): Long

    @Delete
    suspend fun deleteScan(scan: RoomScanEntity)

    @Query("DELETE FROM room_scans WHERE id = :id")
    suspend fun deleteScanById(id: Long)

    @Query("DELETE FROM room_scans")
    suspend fun deleteAllScans()
}
