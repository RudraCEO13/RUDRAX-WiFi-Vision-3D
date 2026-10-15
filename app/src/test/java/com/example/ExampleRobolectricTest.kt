package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.model.RoomScanEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun readAppNameFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("RUDRAX WiFi Vision 3D", appName)
    }

    @Test
    fun testRoomDatabaseInsertAndQuery() = runBlocking {
        val dao = database.roomScanDao()
        val scan = RoomScanEntity(
            roomName = "Living Room",
            rssi = -42,
            frequencyBand = "5 GHz",
            linkSpeedMbps = 866,
            sampleDurationSeconds = 6,
            sampleCount = 6,
            minRssi = -45,
            maxRssi = -40,
            avgRssi = -42,
            qualityCategory = "EXCELLENT",
            notes = "Test"
        )
        val id = dao.insertScan(scan)
        val allScans = dao.getAllScans().first()

        assertEquals(1, allScans.size)
        assertEquals("Living Room", allScans[0].roomName)
        assertEquals(-42, allScans[0].avgRssi)
    }
}
