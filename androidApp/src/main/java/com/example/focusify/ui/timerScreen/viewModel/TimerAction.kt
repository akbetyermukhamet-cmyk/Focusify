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

sealed interface TimerAction {
    data class SkipTimer(val fromButton: Boolean) : TimerAction

    data object ResetTimer : TimerAction
    data object UndoReset : TimerAction
    data object StopAlarm : TimerAction
    data object ToggleTimer : TimerAction

    data class AddTask(
        val name: String,
        val totalPomodoros: Int = 1,
        val dueDate: String? = null,
        val priority: String? = null,
        val tags: List<String> = emptyList(),
        val project: String? = null,
        val colorHex: String? = null
    ) : TimerAction

    data class EditTask(
        val originalName: String,
        val name: String,
        val totalPomodoros: Int = 1,
        val dueDate: String? = null,
        val priority: String? = null,
        val tags: List<String> = emptyList(),
        val project: String? = null,
        val colorHex: String? = null
    ) : TimerAction

    data class SelectTask(val name: String?) : TimerAction
    data class DeleteTask(val name: String) : TimerAction

    data class AddProject(
        val name: String,
        val colorHex: String,
        val onSelectExisting: ((String) -> Unit)? = null
    ) : TimerAction

    data class EditProject(val originalName: String, val name: String, val colorHex: String) : TimerAction

    data class AddTag(
        val name: String,
        val colorHex: String,
        val onSelectExisting: ((String) -> Unit)? = null
    ) : TimerAction
    data class EditTag(val originalName: String, val name: String, val colorHex: String) : TimerAction
}