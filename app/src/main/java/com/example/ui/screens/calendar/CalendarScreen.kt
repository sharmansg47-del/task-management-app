package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.ui.common.EmptyStateView
import com.example.ui.common.SwipeableTaskCard
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.PriorityUrgent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

enum class CalendarViewMode(val title: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

@Composable
fun CalendarScreen(
    tasks: List<TaskItem>,
    categories: List<TaskCategory>,
    is24HourFormat: Boolean = false,
    firstDayOfWeek: Int = 1, // 1 = Monday, 7 = Sunday
    onToggleTaskComplete: (TaskItem) -> Unit,
    onTaskClick: (TaskItem) -> Unit,
    onTaskDelete: (TaskItem) -> Unit,
    onTaskEdit: (TaskItem) -> Unit,
    onTaskDuplicate: (TaskItem) -> Unit,
    onAddTaskForDate: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTHLY) }

    val today = LocalDate.now()
    val selectedEpochDay = selectedDate.toEpochDay()
    val tasksForSelectedDate = tasks.filter { it.dueDate == selectedEpochDay }

    // Map epochDay -> tasks count & attributes
    val tasksByDate = remember(tasks) {
        tasks.filter { it.dueDate != null }.groupBy { it.dueDate!! }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header with Month Navigator & View Mode Toggle
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calendar",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Jump to Today
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedDate = today
                                currentYearMonth = YearMonth.now()
                            }
                            .testTag("calendar_today_button"),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Today",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // View Mode Toggle (Daily, Weekly, Monthly)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalendarViewMode.entries.forEach { mode ->
                        val isSelected = viewMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewMode = mode },
                            label = { Text(mode.title, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // 2. Interactive Calendar Views
        when (viewMode) {
            CalendarViewMode.MONTHLY -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Month Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                                }

                                Text(
                                    text = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                IconButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Day of Week Headers
                            val dayHeaders = if (firstDayOfWeek == 1) {
                                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            } else {
                                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                dayHeaders.forEach { header ->
                                    Text(
                                        text = header,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.width(36.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Month Grid Days
                            val firstDayOfMonth = currentYearMonth.atDay(1)
                            val firstDayOfWeekVal = firstDayOfMonth.dayOfWeek.value // 1=Mon..7=Sun
                            val offset = if (firstDayOfWeek == 1) {
                                firstDayOfWeekVal - 1
                            } else {
                                if (firstDayOfWeekVal == 7) 0 else firstDayOfWeekVal
                            }
                            val daysInMonth = currentYearMonth.lengthOfMonth()

                            val totalSlots = ((offset + daysInMonth + 6) / 7) * 7

                            for (week in 0 until (totalSlots / 7)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    for (dayCol in 0..6) {
                                        val slotIndex = week * 7 + dayCol
                                        val dayNumber = slotIndex - offset + 1

                                        if (dayNumber in 1..daysInMonth) {
                                            val dateObj = currentYearMonth.atDay(dayNumber)
                                            val isSelected = dateObj.isEqual(selectedDate)
                                            val isToday = dateObj.isEqual(today)
                                            val epochDay = dateObj.toEpochDay()
                                            val dateTasks = tasksByDate[epochDay] ?: emptyList()

                                            CalendarDayCell(
                                                dayNumber = dayNumber,
                                                isSelected = isSelected,
                                                isToday = isToday,
                                                tasks = dateTasks,
                                                onClick = { selectedDate = dateObj }
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.size(38.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            CalendarViewMode.WEEKLY -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Week header
                            val startOfWeek = selectedDate.minusDays(((selectedDate.dayOfWeek.value - firstDayOfWeek + 7) % 7).toLong())
                            val endOfWeek = startOfWeek.plusDays(6)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { selectedDate = selectedDate.minusWeeks(1) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Week")
                                }
                                Text(
                                    text = "${startOfWeek.format(DateTimeFormatter.ofPattern("d MMM"))} - ${endOfWeek.format(DateTimeFormatter.ofPattern("d MMM yyyy"))}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { selectedDate = selectedDate.plusWeeks(1) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Week")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (i in 0..6) {
                                    val dateObj = startOfWeek.plusDays(i.toLong())
                                    val isSelected = dateObj.isEqual(selectedDate)
                                    val isToday = dateObj.isEqual(today)
                                    val epochDay = dateObj.toEpochDay()
                                    val dateTasks = tasksByDate[epochDay] ?: emptyList()

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { selectedDate = dateObj }
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary else if (isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
                                            )
                                            .padding(horizontal = 8.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = dateObj.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${dateObj.dayOfMonth}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )

                                        // Dot indicators
                                        Spacer(modifier = Modifier.height(4.dp))
                                        TaskDotsRow(tasks = dateTasks, isSelected = isSelected)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            CalendarViewMode.DAILY -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE")),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = selectedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day")
                            }
                        }
                    }
                }
            }
        }

        // 3. Selected Date Section Header with Quick Add
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${tasksForSelectedDate.size} tasks scheduled",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { onAddTaskForDate(selectedEpochDay) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_task_for_date_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Task", fontSize = 12.sp)
                }
            }
        }

        // 4. Tasks scheduled for selected date
        if (tasksForSelectedDate.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No tasks for this day",
                    subtitle = "Tap 'Add Task' to schedule something for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d"))}.",
                    actionButtonText = "+ Schedule Task",
                    onActionClick = { onAddTaskForDate(selectedEpochDay) },
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        } else {
            items(tasksForSelectedDate, key = { it.id }) { task ->
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
private fun CalendarDayCell(
    dayNumber: Int,
    isSelected: Boolean,
    isToday: Boolean,
    tasks: List<TaskItem>,
    onClick: () -> Unit
) {
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick() }
            .then(
                if (isToday && !isSelected) Modifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$dayNumber",
            fontSize = 13.sp,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )

        TaskDotsRow(tasks = tasks, isSelected = isSelected)
    }
}

@Composable
private fun TaskDotsRow(
    tasks: List<TaskItem>,
    isSelected: Boolean
) {
    if (tasks.isNotEmpty()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 1.dp)
        ) {
            val hasCompleted = tasks.any { it.isCompleted }
            val hasPending = tasks.any { !it.isCompleted }
            val hasUrgent = tasks.any { !it.isCompleted && (it.priority == TaskPriority.URGENT || it.priority == TaskPriority.HIGH) }

            if (hasUrgent) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else PriorityUrgent)
                )
            }
            if (hasPending && !hasUrgent) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else PriorityMedium)
                )
            }
            if (hasCompleted) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White.copy(alpha = 0.7f) else PriorityLow)
                )
            }
        }
    } else {
        Spacer(modifier = Modifier.height(4.dp))
    }
}
