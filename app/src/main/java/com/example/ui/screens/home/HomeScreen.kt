package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoutineCompletion
import com.example.data.model.RoutineItem
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.ui.common.CategoryIcons
import com.example.ui.common.EmptyStateView
import com.example.ui.common.SwipeableTaskCard
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.TealAccent
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun getGreeting(): String {
    val hour = LocalTime.now().hour
    return when {
        hour in 5..11 -> "Good Morning 👋"
        hour in 12..16 -> "Good Afternoon ☀️"
        hour in 17..21 -> "Good Evening 🌙"
        else -> "Good Night 🌌"
    }
}

@Composable
fun HomeScreen(
    tasks: List<TaskItem>,
    categories: List<TaskCategory>,
    routines: List<RoutineItem>,
    routineCompletions: List<RoutineCompletion>,
    userName: String = "Achiever",
    is24HourFormat: Boolean = false,
    onToggleTaskComplete: (TaskItem) -> Unit,
    onTaskClick: (TaskItem) -> Unit,
    onTaskDelete: (TaskItem) -> Unit,
    onTaskEdit: (TaskItem) -> Unit,
    onTaskDuplicate: (TaskItem) -> Unit,
    onToggleRoutineComplete: (RoutineItem, Boolean) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToRoutines: () -> Unit,
    onQuickAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayEpoch = LocalDate.now().toEpochDay()
    val todayDateFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM"))

    val todayTasks = tasks.filter { it.dueDate == todayEpoch }
    val completedTodayCount = todayTasks.count { it.isCompleted }
    val totalTodayCount = todayTasks.size
    val pendingTodayCount = totalTodayCount - completedTodayCount

    val progressPercent = if (totalTodayCount > 0) {
        ((completedTodayCount.toFloat() / totalTodayCount.toFloat()) * 100).toInt()
    } else {
        0
    }

    val upcomingTasks = tasks.filter {
        it.dueDate != null && it.dueDate > todayEpoch && !it.isCompleted
    }.sortedBy { it.dueDate }.take(5)

    val todayCompletedRoutineIds = routineCompletions
        .filter { it.dateEpochDay == todayEpoch }
        .map { it.routineId }
        .toSet()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Greeting
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = getGreeting(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = todayDateFormatted,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. Today's Progress Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("today_progress_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.04f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Today's Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (totalTodayCount == 0) {
                                    "No tasks scheduled for today"
                                } else if (pendingTodayCount == 0) {
                                    "All tasks completed! Humanity survives 🎉"
                                } else {
                                    "$pendingTodayCount pending • $completedTodayCount completed"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Percentage badge / circle
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$progressPercent%",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { if (totalTodayCount > 0) completedTodayCount.toFloat() / totalTodayCount.toFloat() else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Metric Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricPill(
                            label = "Total Tasks",
                            value = "$totalTodayCount",
                            color = MaterialTheme.colorScheme.primary
                        )
                        MetricPill(
                            label = "Completed",
                            value = "$completedTodayCount",
                            color = PriorityLow
                        )
                        MetricPill(
                            label = "Pending",
                            value = "$pendingTodayCount",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // 3. Daily Habits / Routine Quick Bar
        if (routines.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Daily Habits & Routines",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "View All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { onNavigateToRoutines() }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(routines.take(6)) { routine ->
                            val isCompleted = todayCompletedRoutineIds.contains(routine.id)
                            val icon = CategoryIcons.getIcon(routine.iconName)

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onToggleRoutineComplete(routine, !isCompleted) }
                                    .testTag("routine_quick_chip_${routine.id}"),
                                shape = RoundedCornerShape(14.dp),
                                color = if (isCompleted) PriorityLow.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.dp, PriorityLow.copy(alpha = 0.4f)) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCompleted) Icons.Default.CheckCircle else icon,
                                        contentDescription = null,
                                        tint = if (isCompleted) PriorityLow else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = routine.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${routine.timeOfDay} • ${routine.scheduledTime}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Today's Tasks Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Tasks (${todayTasks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "See all",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onNavigateToTasks() }
                        .padding(4.dp)
                )
            }
        }

        // 5. Today's Tasks List
        if (todayTasks.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No tasks for today 🎉",
                    subtitle = "Relax, or tap below to plan something productive!",
                    icon = Icons.Default.DoneAll,
                    actionButtonText = "+ Add Task",
                    onActionClick = onQuickAddTask,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(todayTasks, key = { it.id }) { task ->
                val category = categories.firstOrNull { it.id == task.categoryId }
                SwipeableTaskCard(
                    task = task,
                    category = category,
                    is24HourFormat = is24HourFormat,
                    onToggleComplete = onToggleTaskComplete,
                    onTaskClick = onTaskClick,
                    onTaskDelete = onTaskDelete,
                    onTaskEdit = onTaskEdit,
                    onTaskDuplicate = onTaskDuplicate,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        // 6. Upcoming Tasks Section
        if (upcomingTasks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            items(upcomingTasks, key = { "upcoming_${it.id}" }) { task ->
                val category = categories.firstOrNull { it.id == task.categoryId }
                SwipeableTaskCard(
                    task = task,
                    category = category,
                    is24HourFormat = is24HourFormat,
                    onToggleComplete = onToggleTaskComplete,
                    onTaskClick = onTaskClick,
                    onTaskDelete = onTaskDelete,
                    onTaskEdit = onTaskEdit,
                    onTaskDuplicate = onTaskDuplicate,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
