package com.example.focusify.ui.timerScreen.viewModel

import com.example.focusify.data.Project
import com.example.focusify.data.StatRepository
import com.example.focusify.data.StateRepository
import com.example.focusify.data.Task
import com.example.focusify.data.TaskRepository
import com.example.focusify.di.TimerStateHolder
import com.example.focusify.service.ServiceHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    private val serviceHelper: ServiceHelper = mockk(relaxed = true)
    private val stateRepository: StateRepository = mockk(relaxed = true)
    private val statRepository: StatRepository = mockk(relaxed = true)
    private val taskRepository: TaskRepository = mockk(relaxed = true)
    private val timerStateHolder: TimerStateHolder = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: TimerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { taskRepository.allTasks } returns flowOf(emptyList())
        every { taskRepository.allProjects } returns flowOf(emptyList())
        every { taskRepository.allTags } returns flowOf(emptyList())
        
        viewModel = TimerViewModel(
            serviceHelper,
            stateRepository,
            statRepository,
            taskRepository,
            timerStateHolder
        )
    }

    @Test
    fun `onAction AddProject calls repository`() = runTest(testDispatcher) {
        val projectName = "New Project"
        val projectColor = "#FFFFFF"
        
        coEvery { taskRepository.allProjects } returns flowOf(emptyList())
        coEvery { taskRepository.insertProject(any()) } returns 1L

        viewModel.onAction(TimerAction.AddProject(projectName, projectColor))

        coVerify { taskRepository.insertProject(match { it.name == projectName && it.colorHex == projectColor }) }
    }

    @Test
    fun `onAction AddTask calls repository`() = runTest(testDispatcher) {
        val taskName = "New Task"
        
        coEvery { taskRepository.insertTask(any()) } returns 1L

        viewModel.onAction(TimerAction.AddTask(taskName))

        coVerify { taskRepository.insertTask(match { it.name == taskName }) }
    }
}
