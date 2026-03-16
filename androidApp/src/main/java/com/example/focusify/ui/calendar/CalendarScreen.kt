package com.example.focusify.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusify.data.Project
import com.example.focusify.data.Tag
import com.example.focusify.data.Task
import com.example.focusify.ui.timerScreen.AddNewTaskSheet
import com.example.focusify.ui.timerScreen.TaskItem
import com.example.focusify.ui.timerScreen.viewModel.TimerAction
import com.example.focusify.ui.timerScreen.viewModel.TimerState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    timerState: TimerState,
    tasks: List<Task>,
    projects: List<Project>,
    tags: List<Tag>,
    onAction: (TimerAction) -> Unit,
    onNavigateToTimer: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var isListView by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showAddTaskSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    val titleText = if (isListView) {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        when (selectedDate) {
            today -> "Today"
            tomorrow -> "Tomorrow"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
                selectedDate.format(formatter)
            }
        }
    } else {
        "Calendar"
    }

    val today = LocalDate.now()
    val todayStr = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val filteredTasks = tasks.filter { 
        val isSelectedDate = it.dueDate == selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val isTodayCase = selectedDate == today && it.dueDate == "Today"
        isSelectedDate || isTodayCase
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text(titleText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color(0xFFF9F5F3), RoundedCornerShape(24.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabButton(
                        text = "List",
                        isSelected = isListView,
                        onClick = { isListView = true },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Month",
                        isSelected = !isListView,
                        onClick = { isListView = false },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskSheet = true },
                containerColor = Color(0xFFD3553D),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).padding(bottom = contentPadding.calculateBottomPadding())) {
            if (isListView) {
                CalendarListView(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    tasks = filteredTasks,
                    projects = projects,
                    tags = tags,
                    onAction = onAction,
                    onNavigateToTimer = onNavigateToTimer,
                    onEditTask = { editingTask = it }
                )
            } else {
                CalendarMonthView(
                    selectedDate = selectedDate,
                    onDateSelected = { 
                        selectedDate = it
                        isListView = true
                    },
                    tasks = tasks,
                    projects = projects
                )
            }
        }
    }
    
    if (editingTask != null) {
        AddNewTaskSheet(
            initialTask = editingTask,
            projectsList = projects.map { Pair(it.name, Color(android.graphics.Color.parseColor(it.colorHex ?: "#808080"))) },
            tagsList = tags.map { Pair(it.name, Color(android.graphics.Color.parseColor(it.colorHex ?: "#808080"))) },
            onDismiss = { editingTask = null },
            onAdd = { name, pomodoros, dueDate, priority, selectedTags, project ->
                onAction(TimerAction.EditTask(
                    originalName = editingTask!!.name,
                    name = name,
                    totalPomodoros = pomodoros,
                    dueDate = dueDate,
                    priority = priority,
                    tags = selectedTags,
                    project = project
                ))
                editingTask = null
            },
            onAddProject = { name, color, _ -> 
                val colorHex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                onAction(TimerAction.AddProject(name, colorHex)) 
            },
            onAddTag = { name, color, _ -> 
                val colorHex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                onAction(TimerAction.AddTag(name, colorHex)) 
            },
            onEditProject = { originalName, name, color -> 
                val colorHex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                onAction(TimerAction.EditProject(originalName, name, colorHex)) 
            },
            onEditTag = { originalName, name, color -> 
                val colorHex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                onAction(TimerAction.EditTag(originalName, name, colorHex)) 
            },
        )
    }

    if (showAddTaskSheet) {
        AddNewTaskSheet(
            projectsList = projects.map { Pair(it.name, Color(android.graphics.Color.parseColor(it.colorHex ?: "#808080"))) },
            tagsList = tags.map { Pair(it.name, Color(android.graphics.Color.parseColor(it.colorHex ?: "#808080"))) },
            onDismiss = { showAddTaskSheet = false },
            onAdd = { name, pomodoros, dueDate, priority, selectedTags, project ->
                onAction(TimerAction.AddTask(name, pomodoros, dueDate, priority, selectedTags, project))
                showAddTaskSheet = false
            },
            onAddProject = { name, color, _ -> 
                val colorHex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                onAction(TimerAction.AddProject(name, colorHex)) 
            },
            onAddTag = { name, color, _ -> 
                val colorHex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                onAction(TimerAction.AddTag(name, colorHex)) 
            },
            onEditProject = { originalName, name, color -> 
                val colorHex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                onAction(TimerAction.EditProject(originalName, name, colorHex)) 
            },
            onEditTag = { originalName, name, color -> 
                val colorHex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
                onAction(TimerAction.EditTag(originalName, name, colorHex)) 
            },
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.White else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color(0xFFD3553D) else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun CalendarListView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    tasks: List<Task>,
    projects: List<Project>,
    tags: List<Tag>,
    onAction: (TimerAction) -> Unit,
    onNavigateToTimer: () -> Unit,
    onEditTask: (Task) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DateScroller(selectedDate, onDateSelected)
        
        if (tasks.isEmpty()) {
            EmptyTasksState()
        } else {
            val priorityWeight = mapOf(
                "High Priority" to 4,
                "Medium Priority" to 3,
                "Low Priority" to 2,
                "No Priority" to 1,
                null to 0
            )
            val (completedTasks, activeTasks) = tasks.partition { it.isCompleted }
            
            val sortedActiveTasks = activeTasks.sortedWith(
                compareByDescending<Task> { priorityWeight[it.priority] ?: 0 }
                    .thenBy { it.name }
            )
            
            val sortedCompletedTasks = completedTasks.sortedBy { it.name }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (sortedActiveTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Tasks",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(sortedActiveTasks) { task ->
                        TaskItem(
                            task = task,
                            onDelete = { onAction(TimerAction.DeleteTask(task.name)) },
                            onSelect = { 
                                onAction(TimerAction.SelectTask(task.name)) 
                                onNavigateToTimer()
                            },
                            onEdit = { onEditTask(task) },
                        )
                    }
                }

                if (sortedCompletedTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Completed Tasks",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(sortedCompletedTasks) { task ->
                        TaskItem(
                            task = task,
                            onDelete = { onAction(TimerAction.DeleteTask(task.name)) },
                            onSelect = { 
                                onAction(TimerAction.SelectTask(task.name)) 
                                onNavigateToTimer()
                            },
                            onEdit = { onEditTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateScroller(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val dates = remember {
        val today = LocalDate.now()
        (-2..7).map { today.plusDays(it.toLong()) }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(dates) { date ->
            DateItem(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date == today
    val locale = LocalConfiguration.current.locales[0]
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
    val dayOfMonth = date.dayOfMonth.toString()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(60.dp)
            .height(80.dp)
            .background(
                if (isSelected) Color(0xFFD3553D) else Color.White,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (isToday && !isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(Color(0xFFD3553D), CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.size(4.dp))
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = dayOfMonth,
            color = if (isSelected) Color.White else Color.Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        
        Text(
            text = dayOfWeek,
            color = if (isSelected) Color.White else Color.Gray,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 12.sp
        )
    }
}

@Composable
fun EmptyTasksState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.EventNote,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "You have not added a task for this date.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun CalendarMonthView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    tasks: List<Task>,
    projects: List<Project>
) {
    var currentMonth by remember { mutableStateOf(selectedDate.withDayOfMonth(1)) }
    
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.dayOfWeek.value % 7 // 0 = Sunday, 1 = Monday
    
    val weeks = (daysInMonth + firstDayOfWeek + 6) / 7

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                }
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            days.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            for (w in 0 until weeks) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (d in 0 until 7) {
                        val dayIndex = w * 7 + d - firstDayOfWeek + 1
                        val date = if (dayIndex in 1..daysInMonth) {
                            currentMonth.withDayOfMonth(dayIndex)
                        } else null
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .background(
                                    if (date == selectedDate) Color(0xFFD3553D).copy(alpha = 0.1f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = date != null) { date?.let { onDateSelected(it) } },
                            contentAlignment = Alignment.Center
                        ) {
                            if (date != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        color = if (date == LocalDate.now()) Color(0xFFD3553D) else Color.Black,
                                        fontWeight = if (date == LocalDate.now()) FontWeight.Bold else FontWeight.Normal
                                    )
                                    
                                    val dayTasks = tasks.filter { it.dueDate == date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) }
                                    if (dayTasks.isNotEmpty()) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            dayTasks.take(3).forEach { task ->
                                                val color = try {
                                                    Color(android.graphics.Color.parseColor(task.colorHex ?: "#808080"))
                                                } catch (e: Exception) {
                                                    Color.Gray
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(color, CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
