package com.example.focusify.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepositoryTest {

    private val taskDao: TaskDao = mockk()
    private val projectDao: ProjectDao = mockk()
    private val tagDao: TagDao = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        coEvery { taskDao.getAllTasks() } returns flowOf(emptyList())
        coEvery { projectDao.getAllProjects() } returns flowOf(emptyList())
        coEvery { tagDao.getAllTags() } returns flowOf(emptyList())
        repository = TaskRepository(taskDao, projectDao, tagDao)
    }

    @Test
    fun `renameProject updates project and tasks`() = runTest(testDispatcher) {
        val oldName = "Work"
        val newName = "Job"
        val newColor = "#FF0000"

        coEvery { projectDao.renameProject(any(), any(), any()) } returns Unit
        coEvery { taskDao.updateTasksProjectName(any(), any()) } returns Unit

        repository.renameProject(oldName, newName, newColor)

        coVerify { projectDao.renameProject(oldName, newName, newColor) }
        coVerify { taskDao.updateTasksProjectName(oldName, newName) }
    }

    @Test
    fun `renameTag updates tag and all tasks with that tag`() = runTest(testDispatcher) {
        val oldName = "Kotlin"
        val newName = "Android"
        val newColor = "#00FF00"
        val task = Task(id = 1, name = "Task 1", tags = listOf("Kotlin", "Other"))

        coEvery { tagDao.renameTag(any(), any(), any()) } returns Unit
        coEvery { taskDao.getTasksWithTag(oldName) } returns listOf(task)
        coEvery { taskDao.updateTask(any()) } returns Unit

        repository.renameTag(oldName, newName, newColor)

        coVerify { tagDao.renameTag(oldName, newName, newColor) }
        coVerify { taskDao.updateTask(match { it.tags == listOf("Android", "Other") }) }
    }
}
