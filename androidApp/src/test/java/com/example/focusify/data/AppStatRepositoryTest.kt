package com.example.focusify.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class AppStatRepositoryTest {

    private val statDao: StatDao = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: AppStatRepository

    @Before
    fun setup() {
        repository = AppStatRepository(statDao, testDispatcher)
    }

    @Test
    fun `addFocusTime inserts new stat if not exists`() = runTest(testDispatcher) {
        val focusTime = 1000L
        val today = LocalDate.now()
        
        coEvery { statDao.statExists(today) } returns false
        coEvery { statDao.insertStat(any()) } returns Unit

        repository.addFocusTime(focusTime)

        // Since we can't easily mock LocalTime.now() without extra libraries or refactoring,
        // we verify that insertStat was called with some Stat object.
        coVerify { statDao.insertStat(match { it.date == today && it.totalFocusTime() == focusTime }) }
    }

    @Test
    fun `addFocusTime updates existing stat if exists`() = runTest(testDispatcher) {
        val focusTime = 1000L
        val today = LocalDate.now()
        
        coEvery { statDao.statExists(today) } returns true
        coEvery { statDao.addFocusTimeQ1(any(), any()) } returns Unit
        coEvery { statDao.addFocusTimeQ2(any(), any()) } returns Unit
        coEvery { statDao.addFocusTimeQ3(any(), any()) } returns Unit
        coEvery { statDao.addFocusTimeQ4(any(), any()) } returns Unit

        repository.addFocusTime(focusTime)

        // Verify that one of the addFocusTimeQx methods was called
        coVerify(exactly = 1) { 
            statDao.addFocusTimeQ1(today, focusTime)
            statDao.addFocusTimeQ2(today, focusTime)
            statDao.addFocusTimeQ3(today, focusTime)
            statDao.addFocusTimeQ4(today, focusTime)
        }
    }
}
