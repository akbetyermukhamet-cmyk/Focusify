package com.example.focusify.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class StatDaoTest {
    private lateinit var statDao: StatDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        statDao = db.statDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetStat() = runBlocking {
        val date = LocalDate.now()
        val stat = Stat(date, 100, 200, 300, 400, 50)
        statDao.insertStat(stat)
        val result = statDao.getStat(date).first()
        assertNotNull(result)
        assertEquals(stat.focusTimeQ1, result?.focusTimeQ1)
        assertEquals(stat.totalFocusTime(), result?.totalFocusTime())
    }

    @Test
    fun addFocusTimeUpdatesStat() = runBlocking {
        val date = LocalDate.now()
        val stat = Stat(date, 0, 0, 0, 0, 0)
        statDao.insertStat(stat)
        
        statDao.addFocusTimeQ1(date, 1000L)
        statDao.addFocusTimeQ2(date, 500L)
        
        val result = statDao.getStat(date).first()
        assertEquals(1000L, result?.focusTimeQ1)
        assertEquals(500L, result?.focusTimeQ2)
        assertEquals(1500L, result?.totalFocusTime())
    }
}
