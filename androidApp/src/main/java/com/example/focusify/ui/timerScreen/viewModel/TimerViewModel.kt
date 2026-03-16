/*
 * Copyright (c) 2025 Akbet Ereke
 *
 * This file is part of Tomato - a minimalist pomodoro timer for Android.
 *
 * Tomato is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tomato is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tomato.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.focusify.ui.timerScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusify.data.Project
import com.example.focusify.data.Stat
import com.example.focusify.data.StatRepository
import com.example.focusify.data.StateRepository
import com.example.focusify.data.Tag
import com.example.focusify.data.Task
import com.example.focusify.data.TaskRepository
import com.example.focusify.di.TimerStateHolder
import com.example.focusify.service.ServiceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class DuplicateDialogType {
    EXACT_MATCH,
    NAME_MATCH_DIFFERENT_COLOR
}

data class DuplicateDialogState(
    val type: DuplicateDialogType,
    val name: String,
    val colorHex: String,
    val onConfirm: () -> Unit
)

@OptIn(FlowPreview::class)
class TimerViewModel(
    private val serviceHelper: ServiceHelper,
    private val stateRepository: StateRepository,
    private val statRepository: StatRepository,
    private val taskRepository: TaskRepository,
    private val timerStateHolder: TimerStateHolder,
) : ViewModel() {

    private val _time: MutableStateFlow<Long> = timerStateHolder.time
    val timerState: StateFlow<TimerState> = stateRepository.timerState.asStateFlow()

    val tasks = taskRepository.allTasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val projects = taskRepository.allProjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _tags = taskRepository.allTags.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val tags = _tags

    val duplicateProjectDialog = MutableStateFlow<DuplicateDialogState?>(null)
    val duplicateTagDialog = MutableStateFlow<DuplicateDialogState?>(null)

    val time: StateFlow<Long> = _time.asStateFlow()

    val progress = _time.combine(stateRepository.timerState) { remainingTime, uiState ->
        (uiState.totalTime.toFloat() - remainingTime) / uiState.totalTime
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Seed defaults
            seedDefaults()

            var lastDate = statRepository.getLastDate()
            val today = LocalDate.now()

            // Fills dates between today and lastDate with 0s to ensure continuous history
            if (lastDate != null) {
                while (ChronoUnit.DAYS.between(lastDate, today) > 0) {
                    lastDate = lastDate?.plusDays(1)
                    statRepository.insertStat(Stat(lastDate!!, 0, 0, 0, 0, 0))
                }
            } else {
                statRepository.insertStat(Stat(today, 0, 0, 0, 0, 0))
            }

            delay(1500)

            stateRepository.timerState.update { currentState ->
                currentState.copy(showBrandTitle = false)
            }

            // Move unfinished tasks from previous days to today
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            taskRepository.allTasks.first().forEach { task ->
                if (!task.isCompleted && task.dueDate != null) {
                    try {
                        val taskDate = LocalDate.parse(task.dueDate, formatter)
                        if (taskDate.isBefore(today)) {
                            // Move to today
                            taskRepository.updateTask(task.copy(dueDate = "Today"))
                        }
                    } catch (e: Exception) {
                        // Ignore non-date strings like "Today", "Tomorrow", "Planned"
                    }
                }
            }
        }
    }

    private suspend fun seedDefaults() {
        // Add mock tasks if the list is empty
        if (taskRepository.getTaskCount() == 0) {
            listOf(
                Task(
                    name = "Design User Experience (UX)",
                    tags = listOf("Research", "Work", "Productive"),
                    totalPomodoros = 7,
                    completedPomodoros = 0,
                    dueDate = "Today",
                    project = "Pomodoro App",
                    colorHex = "#5D4037" // Brownish
                ),
                Task(
                    name = "Design User Interface (UI)",
                    tags = listOf("Design", "Work", "Productive"),
                    totalPomodoros = 6,
                    completedPomodoros = 0,
                    dueDate = "Today",
                    project = "Pomodoro App",
                    colorHex = "#689F38" // Greenish
                ),
                Task(
                    name = "Create a Design Wireframe",
                    tags = listOf("Work", "Design", "Productive"),
                    totalPomodoros = 4,
                    completedPomodoros = 0,
                    dueDate = "Today",
                    project = "Pomodoro App",
                    colorHex = "#1976D2" // Blueish
                )
            ).forEach { task ->
                taskRepository.insertTask(task)
            }
        }

        if (taskRepository.getProjectCount() == 0) {
            listOf(
                Project(name = "General", colorHex = "#4CAF50"),
                Project(name = "Pomodoro App", colorHex = "#D3553D"),
                Project(name = "Fashion App", colorHex = "#8BC34A"),
                Project(name = "AI Chatbot App", colorHex = "#00BCD4"),
                Project(name = "Dating App", colorHex = "#E91E63"),
                Project(name = "Quiz App", colorHex = "#3F51B5"),
                Project(name = "News App", colorHex = "#009688"),
                Project(name = "Work Project", colorHex = "#FFA000")
            ).forEach { taskRepository.insertProject(it) }
        }

        if (taskRepository.getTagCount() == 0) {
            listOf(
                Tag(name = "Urgent", colorHex = "#F44336"),
                Tag(name = "Personal", colorHex = "#4CAF50"),
                Tag(name = "Work", colorHex = "#2196F3"),
                Tag(name = "Home", colorHex = "#00BCD4"),
                Tag(name = "Important", colorHex = "#FFA000"),
                Tag(name = "Design", colorHex = "#8BC34A"),
                Tag(name = "Research", colorHex = "#795548"),
                Tag(name = "Productive", colorHex = "#9C27B0")
            ).forEach { taskRepository.insertTag(it) }
        }
    }

    fun onAction(action: TimerAction) {
        when (action) {
            is TimerAction.AddTask -> {
                viewModelScope.launch(Dispatchers.IO) {
                    taskRepository.insertTask(
                        Task(
                            name = action.name,
                            totalPomodoros = action.totalPomodoros,
                            dueDate = action.dueDate,
                            priority = action.priority,
                            tags = action.tags,
                            project = action.project,
                            colorHex = action.colorHex
                        )
                    )
                }
            }

            is TimerAction.SelectTask -> {
                stateRepository.timerState.update { it.copy(selectedTaskName = action.name) }
                serviceHelper.startService(action)
            }

            is TimerAction.DeleteTask -> {
                viewModelScope.launch(Dispatchers.IO) {
                    tasks.value.find { it.name == action.name }?.let {
                        taskRepository.deleteTask(it)
                    }
                }
            }

            is TimerAction.EditTask -> {
                viewModelScope.launch(Dispatchers.IO) {
                    taskRepository.getTaskByName(action.originalName)?.let { originalTask ->
                        val updatedTask = originalTask.copy(
                            name = action.name,
                            totalPomodoros = action.totalPomodoros,
                            dueDate = action.dueDate,
                            priority = action.priority,
                            tags = action.tags,
                            project = action.project,
                            colorHex = action.colorHex
                        )
                        if (action.originalName != action.name) {
                            taskRepository.deleteTask(originalTask)
                            taskRepository.insertTask(updatedTask)
                        } else {
                            taskRepository.updateTask(updatedTask)
                        }
                    }
                }
            }

            is TimerAction.AddProject -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val existing = projects.value.find { it.name == action.name }
                    if (existing != null) {
                        if (existing.colorHex.equals(action.colorHex, ignoreCase = true)) {
                            duplicateProjectDialog.value = DuplicateDialogState(
                                type = DuplicateDialogType.EXACT_MATCH,
                                name = action.name,
                                colorHex = action.colorHex,
                                onConfirm = {
                                    action.onSelectExisting?.invoke(action.name)
                                    duplicateProjectDialog.value = null
                                }
                            )
                        } else {
                            duplicateProjectDialog.value = DuplicateDialogState(
                                type = DuplicateDialogType.NAME_MATCH_DIFFERENT_COLOR,
                                name = action.name,
                                colorHex = action.colorHex,
                                onConfirm = {
                                    viewModelScope.launch(Dispatchers.IO) {
                                        taskRepository.insertProject(Project(name = action.name, colorHex = action.colorHex))
                                    }
                                    duplicateProjectDialog.value = null
                                }
                            )
                        }
                    } else {
                        taskRepository.insertProject(Project(name = action.name, colorHex = action.colorHex))
                    }
                }
            }

            is TimerAction.EditProject -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (action.originalName != action.name) {
                         val existing = projects.value.find { it.name == action.name }
                         if (existing != null) {
                             if (existing.colorHex.equals(action.colorHex, ignoreCase = true)) {
                                 duplicateProjectDialog.value = DuplicateDialogState(
                                     type = DuplicateDialogType.EXACT_MATCH,
                                     name = action.name,
                                     colorHex = action.colorHex,
                                     onConfirm = { duplicateProjectDialog.value = null }
                                 )
                                 return@launch
                             } else {
                                 duplicateProjectDialog.value = DuplicateDialogState(
                                     type = DuplicateDialogType.NAME_MATCH_DIFFERENT_COLOR,
                                     name = action.name,
                                     colorHex = action.colorHex,
                                     onConfirm = {
                                         viewModelScope.launch(Dispatchers.IO) {
                                             taskRepository.renameProject(action.originalName, action.name, action.colorHex)
                                         }
                                         duplicateProjectDialog.value = null
                                     }
                                 )
                                 return@launch
                             }
                         }
                    }
                    taskRepository.renameProject(action.originalName, action.name, action.colorHex)
                }
            }

            is TimerAction.AddTag -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val existing = tags.value.find { it.name == action.name }
                    if (existing != null) {
                        if (existing.colorHex.equals(action.colorHex, ignoreCase = true)) {
                            duplicateTagDialog.value = DuplicateDialogState(
                                type = DuplicateDialogType.EXACT_MATCH,
                                name = action.name,
                                colorHex = action.colorHex,
                                onConfirm = {
                                    action.onSelectExisting?.invoke(action.name)
                                    duplicateTagDialog.value = null
                                }
                            )
                        } else {
                            duplicateTagDialog.value = DuplicateDialogState(
                                type = DuplicateDialogType.NAME_MATCH_DIFFERENT_COLOR,
                                name = action.name,
                                colorHex = action.colorHex,
                                onConfirm = {
                                    viewModelScope.launch(Dispatchers.IO) {
                                        taskRepository.insertTag(Tag(name = action.name, colorHex = action.colorHex))
                                    }
                                    duplicateTagDialog.value = null
                                }
                            )
                        }
                    } else {
                        taskRepository.insertTag(Tag(name = action.name, colorHex = action.colorHex))
                    }
                }
            }

            is TimerAction.EditTag -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (action.originalName != action.name) {
                         val existing = tags.value.find { it.name == action.name }
                         if (existing != null) {
                             if (existing.colorHex.equals(action.colorHex, ignoreCase = true)) {
                                 duplicateTagDialog.value = DuplicateDialogState(
                                     type = DuplicateDialogType.EXACT_MATCH,
                                     name = action.name,
                                     colorHex = action.colorHex,
                                     onConfirm = { duplicateTagDialog.value = null }
                                 )
                                 return@launch
                             } else {
                                 duplicateTagDialog.value = DuplicateDialogState(
                                     type = DuplicateDialogType.NAME_MATCH_DIFFERENT_COLOR,
                                     name = action.name,
                                     colorHex = action.colorHex,
                                     onConfirm = {
                                         viewModelScope.launch(Dispatchers.IO) {
                                             taskRepository.renameTag(action.originalName, action.name, action.colorHex)
                                         }
                                         duplicateTagDialog.value = null
                                     }
                                 )
                                 return@launch
                             }
                         }
                    }
                    taskRepository.renameTag(action.originalName, action.name, action.colorHex)
                }
            }

            else -> serviceHelper.startService(action)
        }
    }
}