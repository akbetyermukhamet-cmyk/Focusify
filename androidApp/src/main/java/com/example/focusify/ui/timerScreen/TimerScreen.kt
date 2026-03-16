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

package com.example.focusify.ui.timerScreen

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ButtonGroup
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import org.koin.compose.koinInject
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch
import com.example.focusify.R
import com.example.focusify.data.Project
import com.example.focusify.data.Tag
import com.example.focusify.data.Task
import com.example.focusify.ui.settingsScreen.viewModel.SettingsState
import com.example.focusify.ui.theme.AppFonts.googleFlex600
import com.example.focusify.ui.theme.AppFonts.robotoFlexTopBar
import com.example.focusify.ui.theme.CustomColors.detailPaneTopBarColors
import com.example.focusify.ui.theme.CustomColors.listItemColors
import com.example.focusify.ui.theme.TomatoTheme
import com.example.focusify.ui.timerScreen.viewModel.TimerAction
import com.example.focusify.ui.timerScreen.viewModel.TimerMode
import com.example.focusify.ui.timerScreen.viewModel.TimerState
import com.example.focusify.ui.timerScreen.viewModel.TimerViewModel
import com.example.focusify.ui.timerScreen.viewModel.DuplicateDialogType
import com.example.focusify.utils.millisecondsToStr

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun SharedTransitionScope.TimerScreen(
    viewModel: TimerViewModel,
    timerState: TimerState,
    settingsState: SettingsState,
    tasks: List<Task>,
    projects: List<Project>,
    tags: List<Tag>,
    isPlus: Boolean,
    contentPadding: PaddingValues,
    progress: () -> Float,
    onAction: (TimerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val motionScheme = motionScheme
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    var showAddNewTask by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showTaskSheet by remember { mutableStateOf(false) }

    val duplicateProjectDialog by viewModel.duplicateProjectDialog.collectAsStateWithLifecycle()
    val duplicateTagDialog by viewModel.duplicateTagDialog.collectAsStateWithLifecycle()

    if (showTaskSheet) {
        TaskSelectionSheet(
            tasks = tasks,
            onAction = onAction,
            onAddTask = { showAddNewTask = true },
            onEditTask = {
                taskToEdit = it
                showAddNewTask = true
            },
            onDismiss = { showTaskSheet = false }
        )
    }

    if (duplicateProjectDialog != null) {
        val state = duplicateProjectDialog!!
        AlertDialog(
            onDismissRequest = { viewModel.duplicateProjectDialog.value = null },
            title = {
                Text(
                    text = if (state.type == DuplicateDialogType.EXACT_MATCH)
                        "This project already exists."
                    else
                        "A project with this name already exists but has a different color."
                )
            },
            confirmButton = {
                TextButton(onClick = state.onConfirm) {
                    Text(if (state.type == DuplicateDialogType.EXACT_MATCH) "Select Existing" else "Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.duplicateProjectDialog.value = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (duplicateTagDialog != null) {
        val state = duplicateTagDialog!!
        AlertDialog(
            onDismissRequest = { viewModel.duplicateTagDialog.value = null },
            title = {
                Text(
                    text = if (state.type == DuplicateDialogType.EXACT_MATCH)
                        "This tag already exists."
                    else
                        "A tag with this name already exists but has a different color."
                )
            },
            confirmButton = {
                TextButton(onClick = state.onConfirm) {
                    Text(if (state.type == DuplicateDialogType.EXACT_MATCH) "Select Existing" else "Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.duplicateTagDialog.value = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddNewTask) {
        AddNewTaskSheet(
            initialTask = taskToEdit,
            projectsList = projects.map { it.name to Color(android.graphics.Color.parseColor(it.colorHex)) },
            tagsList = tags.map { it.name to Color(android.graphics.Color.parseColor(it.colorHex)) },
            onAdd = { title, pomo, due, priority, tagsList, proj ->
                val projectColor = projects.find { it.name == proj }?.colorHex
                val priorityColor = when (priority) {
                    "High Priority" -> "#F44336"
                    "Medium Priority" -> "#FFA000"
                    "Low Priority" -> "#4CAF50"
                    else -> "#9E9E9E"
                }
                
                if (taskToEdit != null) {
                    onAction(
                        TimerAction.EditTask(
                            originalName = taskToEdit!!.name,
                            name = title,
                            totalPomodoros = pomo,
                            dueDate = due,
                            priority = priority,
                            tags = tagsList,
                            project = proj,
                            colorHex = projectColor ?: priorityColor
                        )
                    )
                } else {
                    onAction(
                        TimerAction.AddTask(
                            name = title,
                            totalPomodoros = pomo,
                            dueDate = due,
                            priority = priority,
                            tags = tagsList,
                            project = proj,
                            colorHex = projectColor ?: priorityColor
                        )
                    )
                }
                showAddNewTask = false
                taskToEdit = null
            },
            onAddProject = { name, color, onSelectExisting ->
                onAction(TimerAction.AddProject(name, String.format("#%06X", (0xFFFFFF and color.toArgb())), onSelectExisting))
            },
            onEditProject = { original, name, color ->
                onAction(TimerAction.EditProject(original, name, String.format("#%06X", (0xFFFFFF and color.toArgb()))))
            },
            onAddTag = { name, color, onSelectExisting ->
                onAction(TimerAction.AddTag(name, String.format("#%06X", (0xFFFFFF and color.toArgb())), onSelectExisting))
            },
            onEditTag = { original, name, color ->
                onAction(TimerAction.EditTag(original, name, String.format("#%06X", (0xFFFFFF and color.toArgb()))))
            },
            onDismiss = { 
                showAddNewTask = false
                taskToEdit = null
            }
        )
    }

    val color by animateColorAsState(
        if (timerState.timerMode == TimerMode.FOCUS) colorScheme.primary
        else colorScheme.tertiary,
        animationSpec = motionScheme.slowEffectsSpec()
    )
    val onColor by animateColorAsState(
        if (timerState.timerMode == TimerMode.FOCUS) colorScheme.onPrimary
        else colorScheme.onTertiary,
        animationSpec = motionScheme.slowEffectsSpec()
    )
    val colorContainer by animateColorAsState(
        if (timerState.timerMode == TimerMode.FOCUS) colorScheme.secondaryContainer
        else colorScheme.tertiaryContainer,
        animationSpec = motionScheme.slowEffectsSpec()
    )

    val widthExpanded = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    val navigator = rememberSupportingPaneScaffoldNavigator(
        adaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(supportingPaneAdaptStrategy = AdaptStrategy.Hide)
    )
    val expansionState = rememberPaneExpansionState()

    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        scaffoldState = navigator.scaffoldState,
        mainPane = {
            AnimatedPane {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) { innerPadding ->
                    LazyColumn(
                        horizontalAlignment = CenterHorizontally,
                        contentPadding = PaddingValues(
                            top = innerPadding.calculateTopPadding() + 24.dp,
                            bottom = contentPadding.calculateBottomPadding(),
                            start = 24.dp,
                            end = 24.dp
                        ),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            TimerHeader(isPlus = isPlus, timerState = timerState)
                        }

                        item {
                            TaskSelector(
                                selectedTaskName = timerState.selectedTaskName,
                                onClick = { showTaskSheet = true },
                                onRemoveTask = { onAction(TimerAction.SelectTask(null)) }
                            )
                        }

                        item {
                            TimerProgressSection(
                                timerState = timerState,
                                progress = progress,
                                color = color,
                                colorContainer = colorContainer,
                                widthExpanded = widthExpanded
                            )
                        }

                        item {
                            TimerControls(
                                timerState = timerState,
                                color = color,
                                onColor = onColor,
                                colorContainer = colorContainer,
                                onAction = onAction,
                                haptic = haptic,
                                permissionLauncher = permissionLauncher,
                                snackbarHostState = snackbarHostState,
                                scope = scope,
                                context = context
                            )
                        }
                    }
                }
            }
        },
        supportingPane = {
            val isFocus = timerState.timerMode == TimerMode.FOCUS
            AnimatedPane {
                LazyColumn(
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .background(detailPaneTopBarColors.containerColor)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.up_next),
                                    fontFamily = robotoFlexTopBar,
                                    maxLines = 1
                                )
                            },
                            subtitle = {},
                            windowInsets = WindowInsets(),
                            colors = detailPaneTopBarColors
                        )
                    }
                    items(timerState.totalFocusCount) {
                        val currentSession =
                            it + 1 == timerState.currentFocusCount // currentFocusCount is 1-indexed
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            SegmentedListItem(
                                onClick = {},
                                enabled = it + 1 >= timerState.currentFocusCount,
                                selected = currentSession && isFocus,
                                shapes = ListItemDefaults.segmentedShapes(0, 2),
                                colors = listItemColors,
                                leadingContent = {
                                    AnimatedContent(
                                        if (currentSession && isFocus) 1
                                        else if (it < timerState.currentFocusCount) 2
                                        else 3
                                    ) { show ->
                                        when (show) {
                                            1 -> Icon(
                                                painterResource(R.drawable.in_progress_40dp),
                                                null
                                            )

                                            2 -> Icon(
                                                painterResource(R.drawable.check_circle_40dp),
                                                null
                                            )

                                            else -> Icon(
                                                painterResource(R.drawable.not_started_40dp),
                                                null
                                            )
                                        }
                                    }
                                },
                                supportingContent = {
                                    Text(
                                        millisecondsToStr(settingsState.focusTime),
                                        maxLines = 1
                                    )
                                }
                            ) {
                                Text(
                                    stringResource(R.string.focus),
                                    maxLines = 1
                                )
                            }

                            SegmentedListItem(
                                onClick = {},
                                enabled = it + 1 >= timerState.currentFocusCount,
                                selected = currentSession && !isFocus,
                                shapes = ListItemDefaults.segmentedShapes(1, 2),
                                colors = listItemColors,
                                leadingContent = {
                                    AnimatedContent(
                                        if (currentSession && !isFocus) 1
                                        else if (it + 1 < timerState.currentFocusCount) 2
                                        else 3
                                    ) { show ->
                                        when (show) {
                                            1 -> Icon(
                                                painterResource(R.drawable.in_progress_40dp),
                                                null
                                            )

                                            2 -> Icon(
                                                painterResource(R.drawable.check_circle_40dp),
                                                null
                                            )

                                            else -> Icon(
                                                painterResource(R.drawable.not_started_40dp),
                                                null
                                            )
                                        }
                                    }
                                },
                                supportingContent = {
                                    Text(
                                        if (it != timerState.totalFocusCount - 1) millisecondsToStr(
                                            settingsState.shortBreakTime
                                        )
                                        else millisecondsToStr(settingsState.longBreakTime),
                                        maxLines = 1
                                    )
                                }
                            ) {
                                Text(
                                    if (it != timerState.totalFocusCount - 1) stringResource(R.string.short_break)
                                    else stringResource(R.string.long_break),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        },
        paneExpansionDragHandle = {
            val interactionSource = remember { MutableInteractionSource() }
            VerticalDragHandle(
                modifier = Modifier
                    .paneExpansionDraggable(
                        expansionState,
                        LocalMinimumInteractiveComponentSize.current,
                        interactionSource
                    )
                    .systemGestureExclusion()
            )
        },
        paneExpansionState = expansionState
    )
}

@Composable
fun TimerHeader(isPlus: Boolean, timerState: TimerState) {
    AnimatedContent(
        if (!timerState.showBrandTitle) timerState.timerMode else TimerMode.BRAND,
        transitionSpec = {
            slideInVertically { -it }.togetherWith(slideOutVertically { it })
        },
        label = "header"
    ) { mode ->
        Text(
            text = when (mode) {
                TimerMode.BRAND -> if (!isPlus) stringResource(R.string.app_name) else stringResource(
                    R.string.app_name_plus
                )

                TimerMode.FOCUS -> stringResource(R.string.focus)
                TimerMode.SHORT_BREAK -> stringResource(R.string.short_break)
                TimerMode.LONG_BREAK -> stringResource(R.string.long_break)
            },
            style = typography.headlineMedium.copy(
                fontFamily = robotoFlexTopBar,
                fontSize = 24.sp,
                color = when (mode) {
                    TimerMode.BRAND -> colorScheme.error
                    TimerMode.FOCUS -> colorScheme.primary
                    else -> colorScheme.tertiary
                }
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TaskSelector(
    selectedTaskName: String?,
    onClick: () -> Unit,
    onRemoveTask: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = shapes.medium,
        color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (selectedTaskName != null) {
                    IconButton(
                        onClick = onRemoveTask,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Deselect Task",
                            tint = colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = selectedTaskName ?: "Select Task",
                    style = typography.bodyLarge,
                    color = if (selectedTaskName != null) colorScheme.onSurface else colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(R.drawable.arrow__down_black),
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSelectionSheet(
    tasks: List<Task>,
    onAction: (TimerAction) -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (Task) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var newTaskName by remember { mutableStateOf("") }
    val priorityWeight = mapOf(
        "High Priority" to 4,
        "Medium Priority" to 3,
        "Low Priority" to 2,
        "No Priority" to 1,
        null to 0
    )
    val todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val filteredTasks = tasks
        .filter { it.dueDate == todayStr || it.dueDate == "Today" }
        .filter { 
            newTaskName.isEmpty() ||
            it.name.contains(newTaskName, ignoreCase = true) || 
            (it.project?.contains(newTaskName, ignoreCase = true) == true) ||
            it.tags.any { tag -> tag.contains(newTaskName, ignoreCase = true) }
        }

    val (completedTasks, activeTasks) = filteredTasks.partition { it.isCompleted }
    
    val sortedActiveTasks = activeTasks.sortedWith(
        compareByDescending<Task> { priorityWeight[it.priority] ?: 0 }
            .thenBy { it.name }
    )
    
    val sortedCompletedTasks = completedTasks.sortedBy { it.name }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Task",
                    style = typography.headlineSmall,
                    color = colorScheme.onSurface
                )
                IconButton(onClick = onAddTask) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }

            OutlinedTextField(
                value = newTaskName,
                onValueChange = { newTaskName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search or add task...") },
                singleLine = true,
                trailingIcon = {
                    if (newTaskName.isNotEmpty()) {
                        IconButton(onClick = { newTaskName = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                if (sortedActiveTasks.isEmpty() && sortedCompletedTasks.isEmpty()) {
                    item {
                        Surface(
                            onClick = {
                                if (tasks.isEmpty()) {
                                    onAction(TimerAction.SelectTask(null))
                                    onDismiss()
                                }
                            },
                            shape = shapes.medium,
                            color = Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (tasks.isEmpty()) "No Task" else "No results found for \"$newTaskName\"",
                                modifier = Modifier.padding(16.dp),
                                style = typography.bodyLarge,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (sortedActiveTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Today Tasks",
                            style = typography.labelLarge,
                            color = colorScheme.outline,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(sortedActiveTasks) { task ->
                        TaskItem(
                            task = task,
                            onSelect = {
                                onAction(TimerAction.SelectTask(task.name))
                                onDismiss()
                            },
                            onDelete = {
                                onAction(TimerAction.DeleteTask(task.name))
                            },
                            onEdit = {
                                onEditTask(task)
                            }
                        )
                    }
                }

                if (sortedCompletedTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Completed Tasks",
                            style = typography.labelLarge,
                            color = colorScheme.outline,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(sortedCompletedTasks) { task ->
                        TaskItem(
                            task = task,
                            onSelect = {
                                onAction(TimerAction.SelectTask(task.name))
                                onDismiss()
                            },
                            onDelete = {
                                onAction(TimerAction.DeleteTask(task.name))
                            },
                            onEdit = {
                                onEditTask(task)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onSelect: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    val borderColor = task.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color(0xFFD3553D)
    
    val today = java.time.LocalDate.now()
    val isOverdue = !task.isCompleted && task.dueDate != null && try {
        val dueDate = java.time.LocalDate.parse(task.dueDate, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        dueDate.isBefore(today)
    } catch (e: Exception) {
        false
    }
    
    val taskTextColor = when {
        isOverdue -> Color.Red
        task.isCompleted || task.isSkipped -> Color.Gray
        else -> Color.Black
    }
    
    val textDecoration = if (task.isCompleted || task.isSkipped) TextDecoration.LineThrough else TextDecoration.None

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F5F3),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            // Left colored border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(if (isOverdue) Color.Red else borderColor)
            )
            
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Status Circle
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = if (task.isCompleted) Color(0xFFD3553D) else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(2.dp, if (isOverdue) Color.Red else Color(0xFFD3553D).copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isCompleted) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.name,
                            style = typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = textDecoration
                            ),
                            color = taskTextColor
                        )
                        if (isOverdue) {
                            Text(
                                text = "Overdue",
                                style = typography.labelSmall,
                                color = Color.Red
                            )
                        } else if (task.isSkipped && !task.isCompleted) {
                            Text(
                                text = "Skipped",
                                style = typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = Color(0xFFD3553D).copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = Color(0xFFD3553D).copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (!task.isCompleted) {
                        Icon(
                            Icons.Default.PlayCircleFilled,
                            contentDescription = "Start Task",
                            tint = if (isOverdue) Color.Red else Color(0xFFD3553D),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                if (task.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 36.dp)
                    ) {
                        task.tags.forEach { tag ->
                            Text(
                                text = "#$tag",
                                style = typography.labelMedium,
                                color = if (isOverdue) Color.Red.copy(alpha = 0.8f) else Color(0xFF6D4C41).copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.padding(start = 36.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pomodoro
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Timer, null, modifier = Modifier.size(16.dp), tint = if (isOverdue) Color.Red else Color(0xFFD3553D))
                        Text("${task.completedPomodoros}/${task.totalPomodoros}", style = typography.labelLarge, color = taskTextColor)
                    }
                    
                    // Due Date
                    task.dueDate?.let { dueDate ->
                        val dueDateColor = if (isOverdue) Color.Red else when (dueDate) {
                            "Today" -> Color(0xFF4CAF50)
                            "Tomorrow" -> Color(0xFF2196F3)
                            "This Week" -> Color(0xFF9C27B0)
                            "Planned" -> Color(0xFFF44336)
                            else -> Color(0xFF4CAF50)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.CalendarMonth, null, modifier = Modifier.size(16.dp), tint = dueDateColor)
                            if (isOverdue) {
                                Text(dueDate, style = typography.labelLarge, color = Color.Red)
                            }
                        }
                    }
                    
                    // Priority
                    val priorityColor = if (isOverdue) Color.Red else when (task.priority) {
                        "High Priority" -> Color(0xFFF44336)
                        "Medium Priority" -> Color(0xFFFFA000)
                        "Low Priority" -> Color(0xFF4CAF50)
                        "No Priority" -> Color(0xFF607D8B)
                        else -> Color(0xFFFFA000)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Flag, null, modifier = Modifier.size(16.dp), tint = priorityColor)
                    }
                    
                    // Project
                    task.project?.let { project ->
                        val projectColor = if (isOverdue) Color.Red else when (project) {
                            "General" -> Color(0xFF4CAF50)
                            "Pomodoro App" -> Color(0xFFD3553D)
                            "Fashion App" -> Color(0xFF8BC34A)
                            "AI Chatbot App" -> Color(0xFF00BCD4)
                            "Dating App" -> Color(0xFFE91E63)
                            "Quiz App" -> Color(0xFF3F51B5)
                            "News App" -> Color(0xFF009688)
                            "Work Project" -> Color(0xFFFFA000)
                            else -> Color(0xFF607D8B)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.BusinessCenter, null, modifier = Modifier.size(16.dp), tint = projectColor)
                            Text(project, style = typography.labelLarge, color = taskTextColor, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimerProgressSection(
    timerState: TimerState,
    progress: () -> Float,
    color: androidx.compose.ui.graphics.Color,
    colorContainer: androidx.compose.ui.graphics.Color,
    widthExpanded: Boolean
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        val sizeDp = if (maxHeight < 400.dp) {
            maxHeight * 0.8f
        } else {
            minOf(maxWidth * 0.9f, 350.dp)
        }

        if (timerState.timerMode == TimerMode.FOCUS) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .size(sizeDp)
                    .aspectRatio(1f),
                color = color,
                trackColor = colorContainer,
                strokeWidth = (sizeDp / 15f).coerceIn(8.dp, 24.dp),
                gapSize = 0.dp
            )
        } else {
            CircularWavyProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .size(sizeDp)
                    .aspectRatio(1f),
                color = color,
                trackColor = colorContainer,
                stroke = Stroke(
                    width = with(LocalDensity.current) { (sizeDp / 15f).coerceIn(8.dp, 24.dp).toPx() },
                    cap = StrokeCap.Round,
                ),
                trackStroke = Stroke(
                    width = with(LocalDensity.current) { (sizeDp / 15f).coerceIn(8.dp, 24.dp).toPx() },
                    cap = StrokeCap.Round,
                ),
                wavelength = sizeDp / 6f,
                gapSize = 0.dp
            )
        }

        Column(horizontalAlignment = CenterHorizontally) {
            Text(
                text = timerState.timeStr,
                style = TextStyle(
                    fontFamily = googleFlex600,
                    fontSize = if (timerState.timeStr.length < 6) {
                        (sizeDp.value * 0.25f).sp
                    } else {
                        (sizeDp.value * 0.2f).sp
                    },
                    letterSpacing = (-2.6).sp,
                    fontFeatureSettings = "tnum"
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            AnimatedVisibility(
                visible = timerState.showBrandTitle,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val fontSize = (sizeDp.value * 0.05f).sp
                Text(
                    text = stringResource(
                        R.string.timer_session_count,
                        timerState.currentFocusCount,
                        timerState.totalFocusCount
                    ),
                    fontFamily = googleFlex600,
                    style = typography.titleMedium.copy(
                        fontSize = if (fontSize.isSpecified && fontSize.value > typography.titleMedium.fontSize.value) fontSize else typography.titleMedium.fontSize
                    ),
                    color = colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height((sizeDp.value * 0.02f).dp))
            UpNextSection(timerState = timerState, sizeDp = sizeDp)
        }
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun TimerControls(
    timerState: TimerState,
    color: androidx.compose.ui.graphics.Color,
    onColor: androidx.compose.ui.graphics.Color,
    colorContainer: androidx.compose.ui.graphics.Color,
    onAction: (TimerAction) -> Unit,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Reset Button
        Surface(
            onClick = {
                onAction(TimerAction.ResetTimer)
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        context.getString(R.string.timer_reset_message),
                        actionLabel = context.getString(R.string.undo),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onAction(TimerAction.UndoReset)
                    }
                }
            },
            shape = shapes.large,
            color = colorContainer,
            modifier = Modifier
                .weight(1f)
                .height(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painterResource(R.drawable.restart_large),
                    contentDescription = stringResource(R.string.restart),
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Main Primary Button (Play/Pause)
        Surface(
            onClick = {
                onAction(TimerAction.ToggleTimer)
                if (!timerState.timerRunning) {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                }
            },
            shape = shapes.extraLarge,
            color = color,
            modifier = Modifier
                .weight(1.5f)
                .height(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(if (timerState.timerRunning) R.drawable.pause_large else R.drawable.play_large),
                    contentDescription = stringResource(if (timerState.timerRunning) R.string.pause else R.string.play),
                    tint = onColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Skip Button
        Surface(
            onClick = {
                onAction(TimerAction.SkipTimer(fromButton = true))
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
            },
            shape = shapes.large,
            color = colorContainer,
            modifier = Modifier
                .weight(1f)
                .height(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painterResource(R.drawable.skip_next_large),
                    contentDescription = stringResource(R.string.skip_to_next),
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun UpNextSection(timerState: TimerState, sizeDp: Dp = 200.dp) {
    val baseScale = sizeDp.value / 350f
    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((4 * baseScale).dp)
    ) {
        Text(
            stringResource(R.string.up_next),
            style = typography.labelLarge.copy(
                fontSize = (typography.labelLarge.fontSize.value * baseScale).coerceAtLeast(10f).sp
            ),
            color = colorScheme.outline
        )
        Text(
            text = timerState.nextTimeStr,
            style = typography.headlineSmall.copy(
                fontFamily = googleFlex600,
                color = if (timerState.nextTimerMode == TimerMode.FOCUS) colorScheme.primary else colorScheme.tertiary,
                fontSize = (typography.headlineSmall.fontSize.value * baseScale).coerceAtLeast(14f).sp
            )
        )
        Text(
            text = when (timerState.nextTimerMode) {
                TimerMode.FOCUS -> stringResource(R.string.focus)
                TimerMode.SHORT_BREAK -> stringResource(R.string.short_break)
                else -> stringResource(R.string.long_break)
            },
            style = typography.titleMedium.copy(
                fontSize = (typography.titleMedium.fontSize.value * baseScale).coerceAtLeast(12f).sp
            ),
            color = colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun TimerScreenPreview() {
    val timerState = TimerState(
        timeStr = "03:34", nextTimeStr = "5:00", timerMode = TimerMode.FOCUS, timerRunning = true
    )
    val viewModel: TimerViewModel = koinInject()
    TomatoTheme {
        Surface {
            SharedTransitionLayout {
                TimerScreen(
                    viewModel = viewModel,
                    timerState = timerState,
                    settingsState = SettingsState(),
                    tasks = emptyList(),
                    projects = emptyList(),
                    tags = emptyList(),
                    isPlus = true,
                    contentPadding = PaddingValues(),
                    progress = { 0.3f },
                    onAction = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewTaskSheet(
    initialTask: Task? = null,
    projectsList: List<Pair<String, Color>>,
    tagsList: List<Pair<String, Color>>,
    onAdd: (String, Int, String?, String?, List<String>, String?) -> Unit,
    onAddProject: (String, Color, ((String) -> Unit)?) -> Unit,
    onEditProject: (String, String, Color) -> Unit,
    onAddTag: (String, Color, ((String) -> Unit)?) -> Unit,
    onEditTag: (String, String, Color) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf(initialTask?.name ?: "") }
    var selectedPomodoros by remember { mutableStateOf(initialTask?.totalPomodoros ?: 1) }
    var dueDate by remember { mutableStateOf<String?>(initialTask?.dueDate ?: "Today") }
    var priority by remember { mutableStateOf<String?>(initialTask?.priority) }
    var selectedTags by remember { mutableStateOf(initialTask?.tags ?: listOf<String>()) }
    var project by remember { mutableStateOf<String?>(initialTask?.project) }
    var showDueDate by remember { mutableStateOf(false) }
    var showPriority by remember { mutableStateOf(false) }
    var showTags by remember { mutableStateOf(false) }
    var showProject by remember { mutableStateOf(false) }
    var showAddNewProject by remember { mutableStateOf(false) }
    var projectToEdit by remember { mutableStateOf<Pair<String, Color>?>(null) }
    var showAddNewTag by remember { mutableStateOf(false) }
    var tagToEdit by remember { mutableStateOf<Pair<String, Color>?>(null) }

    var prevProjectsList by remember { mutableStateOf(projectsList) }
    var prevTagsList by remember { mutableStateOf(tagsList) }

    LaunchedEffect(projectsList) {
        if (projectsList.size > prevProjectsList.size) {
            val newProject = projectsList.find { p -> !prevProjectsList.any { it.first == p.first } }
            newProject?.let { project = it.first }
        }
        prevProjectsList = projectsList
    }

    LaunchedEffect(tagsList) {
        if (tagsList.size > prevTagsList.size) {
            val newTag = tagsList.find { t -> !prevTagsList.any { it.first == t.first } }
            newTag?.let {
                if (!selectedTags.contains(it.first)) {
                    selectedTags = selectedTags + it.first
                }
            }
        }
        prevTagsList = tagsList
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (initialTask != null) "Edit Task" else "Add New Task",
                style = typography.headlineSmall,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text(if (initialTask != null) "Edit Task..." else "Add a Task...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                textStyle = typography.headlineSmall.copy(fontWeight = FontWeight.Medium)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Estimated Pomodoros",
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..8).forEach { num ->
                    val selected = num == selectedPomodoros
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { selectedPomodoros = num }
                            .background(
                                color = if (selected) Color(0xFFD3553D) else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            style = if (selected) typography.titleMedium.copy(fontWeight = FontWeight.Bold) else typography.titleMedium,
                            color = if (selected) Color.White else colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    val tomorrowStr = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    val dueDateIcon = when (dueDate) {
                        "Today", todayStr -> Icons.Outlined.WbSunny
                        "Tomorrow", tomorrowStr -> Icons.Outlined.LightMode
                        "This Week" -> Icons.Outlined.CalendarMonth
                        "Planned" -> Icons.Outlined.EventAvailable
                        null -> Icons.Outlined.WbSunny
                        else -> Icons.Outlined.CalendarMonth
                    }
                    val dueDateColor = when (dueDate) {
                        "Today", todayStr -> Color(0xFF4CAF50)
                        "Tomorrow", tomorrowStr -> Color(0xFF2196F3)
                        "This Week" -> Color(0xFF9C27B0)
                        "Planned" -> Color(0xFFF44336)
                        else -> Color(0xFF4CAF50)
                    }

                    val priorityColor = when (priority) {
                        "High Priority" -> Color(0xFFF44336)
                        "Medium Priority" -> Color(0xFFFFA000)
                        "Low Priority" -> Color(0xFF4CAF50)
                        "No Priority" -> Color(0xFF607D8B)
                        else -> Color(0xFFFFA000)
                    }

                    val projectColor = when (project) {
                        "General" -> Color(0xFF4CAF50)
                        "Pomodoro App" -> Color(0xFFD3553D)
                        "Fashion App" -> Color(0xFF8BC34A)
                        "AI Chatbot App" -> Color(0xFF00BCD4)
                        "Dating App" -> Color(0xFFE91E63)
                        "Quiz App" -> Color(0xFF3F51B5)
                        "News App" -> Color(0xFF009688)
                        "Work Project" -> Color(0xFFFFA000)
                        else -> Color(0xFF607D8B)
                    }

                    OptionIcon(
                        icon = dueDateIcon,
                        selected = dueDate != null,
                        selectedColor = dueDateColor
                    ) {
                        showDueDate = true
                        showPriority = false
                        showTags = false
                        showProject = false
                    }
                    OptionIcon(
                        icon = Icons.Outlined.Flag,
                        selected = priority != null,
                        selectedColor = priorityColor
                    ) {
                        showDueDate = false
                        showPriority = true
                        showTags = false
                        showProject = false
                    }
                    OptionIcon(
                        icon = Icons.Outlined.Tag,
                        selected = selectedTags.isNotEmpty(),
                        selectedColor = Color(0xFF2196F3)
                    ) {
                        showDueDate = false
                        showPriority = false
                        showTags = true
                        showProject = false
                    }
                    OptionIcon(
                        icon = Icons.Outlined.BusinessCenter,
                        selected = project != null,
                        selectedColor = projectColor
                    ) {
                        showDueDate = false
                        showPriority = false
                        showTags = false
                        showProject = true
                    }
                }

                Surface(
                    onClick = {
                        if (title.isNotBlank() && selectedPomodoros > 0) {
                            onAdd(title.trim(), selectedPomodoros, dueDate ?: "Today", priority ?: "No Priority", selectedTags, project ?: "General")
                        }
                    },
                    shape = CircleShape,
                    color = if (title.isNotBlank() && selectedPomodoros > 0) Color(0xFFD3553D) else Color(0xFFD3553D).copy(alpha = 0.5f),
                    modifier = Modifier.height(40.dp)
                ) {
                    Box(Modifier.padding(horizontal = 24.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (initialTask != null) "Update" else "Add",
                            color = Color.White,
                            style = typography.labelLarge
                        )
                    }
                }
            }
        }

        if (showDueDate) {
            DueDateSelectionSheet(
                selected = dueDate,
                onCancel = { showDueDate = false },
                onConfirm = { value -> dueDate = value; showDueDate = false }
            )
        }
        if (showPriority) {
            SimpleSelectionSheet(
                title = "Priority",
                options = listOf("High Priority", "Medium Priority", "Low Priority", "No Priority"),
                selected = priority,
                onCancel = { showPriority = false },
                onConfirm = { value -> priority = value; showPriority = false }
            )
        }
            if (showTags) {
                MultiSelectionSheet(
                    title = "Tags",
                    options = tagsList,
                    selected = selectedTags,
                    onCancel = { showTags = false },
                    onConfirm = { values -> selectedTags = values; showTags = false },
                    onAddOption = { 
                        tagToEdit = null
                        showAddNewTag = true 
                    },
                    onEditOption = { tag ->
                        tagToEdit = tag
                        showAddNewTag = true
                    }
                )
            }
            if (showProject) {
                SimpleSelectionSheet(
                    title = "Project",
                    options = projectsList,
                    selected = project,
                    onCancel = { showProject = false },
                    onConfirm = { value -> project = value; showProject = false },
                    onAddOption = { 
                        projectToEdit = null
                        showAddNewProject = true 
                    },
                    onEditOption = { proj ->
                        projectToEdit = proj
                        showAddNewProject = true
                    }
                )
            }
            if (showAddNewProject) {
                AddNewProjectSheet(
                    initialName = projectToEdit?.first,
                    initialColor = projectToEdit?.second,
                    onCancel = { 
                        showAddNewProject = false
                        projectToEdit = null
                    },
                    onAdd = { name, color ->
                        if (projectToEdit != null) {
                            onEditProject(projectToEdit!!.first, name, color)
                        } else {
                            onAddProject(name, color) { existingName ->
                                project = existingName
                            }
                        }
                        showAddNewProject = false
                        projectToEdit = null
                    }
                )
            }
            if (showAddNewTag) {
                AddNewTagSheet(
                    initialName = tagToEdit?.first,
                    initialColor = tagToEdit?.second,
                    onCancel = { 
                        showAddNewTag = false
                        tagToEdit = null
                    },
                    onAdd = { name, color ->
                        if (tagToEdit != null) {
                            onEditTag(tagToEdit!!.first, name, color)
                        } else {
                            onAddTag(name, color) { existingName ->
                                if (!selectedTags.contains(existingName)) {
                                    selectedTags = selectedTags + existingName
                                }
                            }
                        }
                        showAddNewTag = false
                        tagToEdit = null
                    }
                )
            }
    }
}

@Composable
private fun OptionIcon(
    icon: ImageVector,
    selected: Boolean = false,
    selectedColor: Color = Color(0xFF4CAF50),
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) selectedColor else colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueDateSelectionSheet(
    selected: String?,
    onCancel: () -> Unit,
    onConfirm: (String?) -> Unit
) {
    var current by remember { mutableStateOf(selected ?: "Today") }
    val initialSelectedDate = remember(selected) {
        runCatching { LocalDate.parse(selected, DateTimeFormatter.ofPattern("dd/MM/yyyy")) }.getOrNull()
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Calendar state
    var displayedMonth by remember { mutableStateOf(initialSelectedDate?.withDayOfMonth(1) ?: LocalDate.now().withDayOfMonth(1)) }

    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Due Date",
                    style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickDateButton(
                        "Today",
                        Icons.Outlined.WbSunny,
                        Color(0xFF4CAF50),
                        current == "Today"
                    ) { current = "Today" }
                    QuickDateButton(
                        "Tomorrow",
                        Icons.Outlined.LightMode,
                        Color(0xFF2196F3),
                        current == "Tomorrow"
                    ) { current = "Tomorrow" }
                    QuickDateButton(
                        "This Week",
                        Icons.Outlined.CalendarMonth,
                        Color(0xFF9C27B0),
                        current == "This Week"
                    ) { current = "This Week" }
                    QuickDateButton(
                        "Planned",
                        Icons.Outlined.EventAvailable,
                        Color(0xFFF44336),
                        current == "Planned"
                    ) { current = "Planned" }
                }

                // Calendar Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                ) {
                    // Calendar Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Month")
                        }
                        Text(
                            text = "${
                                displayedMonth.month.getDisplayName(
                                    JavaTextStyle.FULL,
                                    LocalConfiguration.current.locales[0]
                                )
                            } ${displayedMonth.year}",
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Month")
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Days of week
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        val daysOfWeek = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
                        daysOfWeek.forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Days grid
                    val firstDayOfMonth = displayedMonth.dayOfWeek.value // 1 (Mon) to 7 (Sun)
                    val daysInMonth = displayedMonth.lengthOfMonth()
                    val prevMonth = displayedMonth.minusMonths(1)
                    val daysInPrevMonth = prevMonth.lengthOfMonth()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        val rows = 6
                        for (row in 0 until rows) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                for (col in 1..7) {
                                    val dayIndex = row * 7 + col
                                    val dayNumber: Int
                                    val isCurrentMonth: Boolean

                                    if (dayIndex < firstDayOfMonth) {
                                        dayNumber = daysInPrevMonth - (firstDayOfMonth - dayIndex - 1)
                                        isCurrentMonth = false
                                    } else if (dayIndex >= firstDayOfMonth && dayIndex < firstDayOfMonth + daysInMonth) {
                                        dayNumber = dayIndex - firstDayOfMonth + 1
                                        isCurrentMonth = true
                                    } else {
                                        dayNumber = dayIndex - (firstDayOfMonth + daysInMonth - 1)
                                        isCurrentMonth = false
                                    }

                                    val date = if (isCurrentMonth) displayedMonth.withDayOfMonth(dayNumber) else null
                                    val dateStr = date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                    val today = LocalDate.now()
                                    val todayStr = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                    val tomorrowStr = today.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                    val oneWeekLater = today.plusWeeks(1)

                                    val isSelected = isCurrentMonth && (
                                            (current == dateStr) ||
                                                    (current == "Today" && dateStr == todayStr) ||
                                                    (current == "Tomorrow" && dateStr == tomorrowStr) ||
                                                    (current == "This Week" && dateStr == todayStr)
                                            )

                                    val isInRange = isCurrentMonth && current == "This Week" &&
                                            date != null && date.isAfter(today) && date.isBefore(oneWeekLater.plusDays(1))

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .background(
                                                color = when {
                                                    isSelected -> Color(0xFFD3553D)
                                                    isInRange -> Color(0xFFD3553D).copy(alpha = 0.2f)
                                                    else -> Color.Transparent
                                                },
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                if (isCurrentMonth && date != null) {
                                                    current = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                } else {
                                                    if (dayIndex < firstDayOfMonth) {
                                                        displayedMonth = displayedMonth.minusMonths(1)
                                                    } else {
                                                        displayedMonth = displayedMonth.plusMonths(1)
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayNumber.toString(),
                                            style = typography.bodyMedium.copy(fontSize = 12.sp),
                                            color = when {
                                                isSelected -> Color.White
                                                isCurrentMonth -> Color.Black
                                                else -> Color(0xFFCCCCCC)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = onCancel,
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF9EAE6),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Cancel",
                            color = Color(0xFFD3553D),
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Surface(
                    onClick = {
                        val result = when (current) {
                            "Today" -> LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            "Tomorrow" -> LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            else -> current
                        }
                        onConfirm(result)
                    },
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFD3553D),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "OK",
                            color = Color.White,
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickDateButton(
    label: String,
    icon: ImageVector,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Surface(
            shape = CircleShape,
            color = color,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleSelectionSheet(
    title: String,
    options: List<Any>,
    selected: String?,
    onCancel: () -> Unit,
    onConfirm: (String?) -> Unit,
    onAddOption: (() -> Unit)? = null,
    onEditOption: ((Pair<String, Color>) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var current by remember { mutableStateOf(selected) }
    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        title,
                        style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    if (onAddOption != null) {
                        IconButton(
                            onClick = onAddOption,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color(0xFFD3553D)
                            )
                        }
                    }
                }

                options.forEach { option ->
                    val opt = if (option is Pair<*, *>) option.first as String else option as String
                    val isSelected = current == opt
                    val iconAndColor = if (title == "Priority") {
                        val color = when (opt) {
                            "High Priority" -> Color(0xFFF44336)
                            "Medium Priority" -> Color(0xFFFFA000)
                            "Low Priority" -> Color(0xFF4CAF50)
                            "No Priority" -> Color(0xFF607D8B)
                            else -> Color(0xFFFFA000)
                        }
                        Icons.Default.Flag to color
                    } else if (title == "Project") {
                        val color = if (option is Pair<*, *>) option.second as Color else {
                            when (opt) {
                                "General" -> Color(0xFF4CAF50)
                                "Pomodoro App" -> Color(0xFFD3553D)
                                "Fashion App" -> Color(0xFF8BC34A)
                                "AI Chatbot App" -> Color(0xFF00BCD4)
                                "Dating App" -> Color(0xFFE91E63)
                                "Quiz App" -> Color(0xFF3F51B5)
                                "News App" -> Color(0xFF009688)
                                "Work Project" -> Color(0xFFFFA000)
                                else -> Color(0xFF607D8B)
                            }
                        }
                        Icons.Outlined.BusinessCenter to color
                    } else null

                    Surface(
                        onClick = { current = opt },
                        color = if (isSelected) Color(0xFFF9EAE6) else Color.Transparent,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (iconAndColor != null) {
                                        Surface(
                                            shape = CircleShape,
                                            color = iconAndColor.second,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    iconAndColor.first,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.width(16.dp))
                                    }
                                    Text(
                                        opt,
                                        style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                                if (onEditOption != null) {
                                    val optionToEdit = if (option is Pair<*, *>) {
                                        @Suppress("UNCHECKED_CAST")
                                        option as Pair<String, Color>
                                    } else {
                                        Pair(opt, iconAndColor?.second ?: Color.Transparent)
                                    }
                                    IconButton(onClick = { onEditOption(optionToEdit) }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFFD3553D)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = onCancel,
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF9EAE6),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Cancel",
                            color = Color(0xFFD3553D),
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Surface(
                    onClick = { onConfirm(current) },
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFD3553D),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "OK",
                            color = Color.White,
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseEntrySheet(
    initialName: String? = null,
    initialColor: Color? = null,
    title: String,
    label: String,
    placeholder: String,
    icon: ImageVector,
    onCancel: () -> Unit,
    onAdd: (String, Color) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(initialName ?: "") }
    var selectedColor by remember { mutableStateOf(initialColor ?: Color(0xFFF44336)) }

    val colors = listOf(
        Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF3F51B5),
        Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4), Color(0xFF009688), Color(0xFF4CAF50),
        Color(0xFF8BC34A), Color(0xFFCDDC39), Color(0xFFFFEB3B), Color(0xFFFFC107), Color(0xFFFF9800),
        Color(0xFFFF5722), Color(0xFF795548), Color(0xFF9E9E9E), Color(0xFF607D8B), Color.Black
    )

    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
                Text(
                    text = title,
                    style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = { /* More options */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Input Field
            Text(
                text = "Name",
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(icon, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD3553D),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Color Grid
            Text(
                text = "$label Color Mark",
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Using simple Columns/Rows for the 5x4 grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (i in 0 until 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (j in 0 until 5) {
                            val colorIndex = i * 5 + j
                            val color = colors[colorIndex]
                            val isSelected = selectedColor == color

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(color, shape = CircleShape)
                                    .clickable { selectedColor = color },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    onClick = onCancel,
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF9EAE6),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFFD3553D),
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Surface(
                    onClick = { if (name.isNotBlank()) onAdd(name, selectedColor) },
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFD3553D),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (initialName != null) "Update" else "Add",
                            color = Color.White,
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AddNewProjectSheet(
    initialName: String? = null,
    initialColor: Color? = null,
    onCancel: () -> Unit,
    onAdd: (String, Color) -> Unit
) {
    BaseEntrySheet(
        initialName = initialName,
        initialColor = initialColor,
        title = if (initialName != null) "Edit Project" else "Add New Project",
        label = "Project",
        placeholder = "Project Name",
        icon = Icons.Outlined.BusinessCenter,
        onCancel = onCancel,
        onAdd = onAdd
    )
}

@Composable
private fun AddNewTagSheet(
    initialName: String? = null,
    initialColor: Color? = null,
    onCancel: () -> Unit,
    onAdd: (String, Color) -> Unit
) {
    BaseEntrySheet(
        initialName = initialName,
        initialColor = initialColor,
        title = if (initialName != null) "Edit Tag" else "Add New Tag",
        label = "Tag",
        placeholder = "Tag Name",
        icon = Icons.Outlined.Tag,
        onCancel = onCancel,
        onAdd = onAdd
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MultiSelectionSheet(
    title: String,
    options: List<Pair<String, Color>>,
    selected: List<String>,
    onCancel: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    onAddOption: (() -> Unit)? = null,
    onEditOption: ((Pair<String, Color>) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var current by remember { mutableStateOf(selected.toSet()) }
    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        title,
                        style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    if (onAddOption != null) {
                        IconButton(
                            onClick = onAddOption,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color(0xFFD3553D)
                            )
                        }
                    }
                }

                options.forEach { (name, color) ->
                    val checked = current.contains(name)
                    val iconColor = color

                    Surface(
                        onClick = {
                            current = if (checked) {
                                current - name
                            } else {
                                current + name
                            }
                        },
                        color = if (checked) Color(0xFFF9EAE6) else Color.Transparent,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = iconColor,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Outlined.Tag,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Text(
                                        name,
                                        style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                                if (onEditOption != null) {
                                    IconButton(onClick = { onEditOption(Pair(name, color)) }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                if (checked) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFFD3553D)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = onCancel,
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF9EAE6),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Cancel",
                            color = Color(0xFFD3553D),
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Surface(
                    onClick = { onConfirm(current.toList()) },
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFD3553D),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "OK",
                            color = Color.White,
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
