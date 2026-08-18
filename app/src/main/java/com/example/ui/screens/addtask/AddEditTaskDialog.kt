package com.example.ui.screens.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RepeatFrequency
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.ui.common.AppDatePickerDialog
import com.example.ui.common.AppTimePickerDialog
import com.example.ui.common.CategoryChip
import com.example.ui.common.PriorityBadge
import com.example.ui.common.formatTaskDate
import com.example.ui.common.formatTaskTime
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTaskSheet(
    initialTask: TaskItem? = null,
    preselectedDateEpochDay: Long? = null,
    categories: List<TaskCategory>,
    defaultPriority: TaskPriority = TaskPriority.MEDIUM,
    defaultReminderMinutes: Int = 10,
    is24HourFormat: Boolean = false,
    onSaveTask: (TaskItem) -> Unit,
    onDismiss: () -> Unit,
    onOpenCategoryManager: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var dueDate by remember {
        mutableStateOf(initialTask?.dueDate ?: preselectedDateEpochDay ?: LocalDate.now().toEpochDay())
    }
    var dueHour by remember { mutableStateOf(initialTask?.dueTimeHour ?: 12) }
    var dueMinute by remember { mutableStateOf(initialTask?.dueTimeMinute ?: 0) }
    var hasTime by remember { mutableStateOf(initialTask?.dueTimeHour != null) }

    var priority by remember { mutableStateOf(initialTask?.priority ?: defaultPriority) }
    var selectedCategory by remember {
        mutableStateOf(
            categories.firstOrNull { it.id == initialTask?.categoryId }
                ?: categories.firstOrNull { it.name.equals(initialTask?.categoryName, ignoreCase = true) }
                ?: categories.firstOrNull()
        )
    }

    var reminderMinutes by remember {
        mutableStateOf(initialTask?.reminderMinutesBefore ?: if (initialTask == null) defaultReminderMinutes else null)
    }
    var repeatFrequency by remember { mutableStateOf(initialTask?.repeatFrequency ?: RepeatFrequency.NONE) }
    var repeatCustomDays by remember { mutableStateOf(initialTask?.repeatCustomDays ?: "1,3,5") }
    var notes by remember { mutableStateOf(initialTask?.notes ?: "") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("add_edit_task_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialTask == null) "Create New Task" else "Edit Task",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Task Title
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (titleError && it.isNotBlank()) titleError = false
                },
                label = { Text("Task Title *") },
                placeholder = { Text("e.g., Study for algorithms exam") },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Title is required") }
                } else null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_title_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Task Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                placeholder = { Text("Add any context, goals, or sub-points") },
                modifier = Modifier.fillMaxWidth().testTag("task_description_input"),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "+ Manage",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onOpenCategoryManager() }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory?.id == cat.id
                    CategoryChip(
                        category = cat,
                        isSelected = isSelected,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Priority Section
            Text(
                text = "Priority Level",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.entries.forEach { p ->
                    val isSelected = priority == p
                    FilterChip(
                        selected = isSelected,
                        onClick = { priority = p },
                        label = {
                            PriorityBadge(priority = p, showLabel = true)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Date and Time Row
            Text(
                text = "Schedule & Deadline",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date Chip
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                        .testTag("date_picker_button"),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Date",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatTaskDate(dueDate),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Time Chip
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            hasTime = true
                            showTimePicker = true
                        }
                        .testTag("time_picker_button"),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Time",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (hasTime) formatTaskTime(dueHour, dueMinute, is24HourFormat) else "Set time",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (hasTime) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear time",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { hasTime = false },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Reminder Presets
            Text(
                text = "Reminder",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            val reminderOptions = listOf(
                null to "No reminder",
                0 to "At time",
                10 to "10m before",
                30 to "30m before",
                60 to "1h before",
                1440 to "1d before"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reminderOptions.forEach { (mins, label) ->
                    val isSelected = reminderMinutes == mins
                    FilterChip(
                        selected = isSelected,
                        onClick = { reminderMinutes = mins },
                        label = { Text(label, fontSize = 12.sp) },
                        leadingIcon = if (isSelected && mins != null) {
                            { Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Repeat Frequency
            Text(
                text = "Repeat Frequency",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RepeatFrequency.entries.forEach { freq ->
                    val isSelected = repeatFrequency == freq
                    FilterChip(
                        selected = isSelected,
                        onClick = { repeatFrequency = freq },
                        label = { Text(freq.title, fontSize = 12.sp) },
                        leadingIcon = if (isSelected && freq != RepeatFrequency.NONE) {
                            { Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )
                }
            }

            // If custom repeat frequency selected, show day toggles (Mon-Sun)
            if (repeatFrequency == RepeatFrequency.CUSTOM) {
                Spacer(modifier = Modifier.height(10.dp))
                val daysOfWeek = listOf(
                    1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat", 7 to "Sun"
                )
                val activeDays = repeatCustomDays.split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .toMutableSet()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEach { (dayInt, label) ->
                        val isDaySelected = activeDays.contains(dayInt)
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (isDaySelected) {
                                        activeDays.remove(dayInt)
                                    } else {
                                        activeDays.add(dayInt)
                                    }
                                    repeatCustomDays = activeDays.sorted().joinToString(",")
                                },
                            color = if (isDaySelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDaySelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Notes field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes & Checkpoints") },
                placeholder = { Text("Add any extra instructions or links") },
                modifier = Modifier.fillMaxWidth().testTag("task_notes_input"),
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Large Action Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@Button
                    }
                    val item = TaskItem(
                        id = initialTask?.id ?: 0,
                        title = title.trim(),
                        description = description.trim(),
                        dueDate = dueDate,
                        dueTimeHour = if (hasTime) dueHour else null,
                        dueTimeMinute = if (hasTime) dueMinute else null,
                        priority = priority,
                        categoryId = selectedCategory?.id,
                        categoryName = selectedCategory?.name ?: "General",
                        reminderMinutesBefore = reminderMinutes,
                        repeatFrequency = repeatFrequency,
                        repeatCustomDays = repeatCustomDays,
                        isCompleted = initialTask?.isCompleted ?: false,
                        completedAtMillis = initialTask?.completedAtMillis,
                        createdAtMillis = initialTask?.createdAtMillis ?: System.currentTimeMillis(),
                        notes = notes.trim(),
                        parentRecurringId = initialTask?.parentRecurringId
                    )
                    onSaveTask(item)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("create_task_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = if (initialTask == null) Icons.Default.Add else Icons.Default.Notes,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (initialTask == null) "Create Task" else "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDatePicker) {
        AppDatePickerDialog(
            initialEpochDay = dueDate,
            onDateSelected = { selectedDate ->
                dueDate = selectedDate
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        AppTimePickerDialog(
            initialHour = dueHour,
            initialMinute = dueMinute,
            is24Hour = is24HourFormat,
            onTimeSelected = { h, m ->
                dueHour = h
                dueMinute = m
            },
            onDismiss = { showTimePicker = false }
        )
    }
}
