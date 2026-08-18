package com.example.ui.screens.routines

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoutineCompletion
import com.example.data.model.RoutineItem
import com.example.data.model.TaskCategory
import com.example.ui.common.CategoryIcons
import com.example.ui.common.EmptyStateView
import com.example.ui.theme.PriorityLow
import java.time.LocalDate

@Composable
fun RoutinesScreen(
    routines: List<RoutineItem>,
    routineCompletions: List<RoutineCompletion>,
    categories: List<TaskCategory>,
    onToggleCompletion: (RoutineItem, Boolean) -> Unit,
    onAddRoutine: (RoutineItem) -> Unit,
    onUpdateRoutine: (RoutineItem) -> Unit,
    onDeleteRoutine: (RoutineItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val todayEpoch = LocalDate.now().toEpochDay()
    val completedIdsForToday = remember(routineCompletions) {
        routineCompletions.filter { it.dateEpochDay == todayEpoch }.map { it.routineId }.toSet()
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingRoutine by remember { mutableStateOf<RoutineItem?>(null) }

    val completedCount = routines.count { completedIdsForToday.contains(it.id) }
    val totalCount = routines.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    // Group routines by time of day
    val timeOfDayOrder = listOf("Morning", "Afternoon", "Evening", "Night")
    val groupedRoutines = routines.groupBy { it.timeOfDay }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("routines_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Add Routine Button
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
                        text = "Routines & Habits",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("add_routine_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Routine", fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Daily Routine Progress & Streak Banner
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
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF97316).copy(alpha = 0.08f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF97316).copy(alpha = 0.15f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = Color(0xFFF97316),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "Daily Habit Completion",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "$completedCount of $totalCount habits done today",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Color(0xFFF97316)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFFF97316),
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // 3. Routines by Time of Day
        if (routines.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No routines yet",
                    subtitle = "Build daily habits like drinking water, morning exercise, and study sessions.",
                    actionButtonText = "+ Create First Routine",
                    onActionClick = { showAddDialog = true },
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            timeOfDayOrder.forEach { timeSlot ->
                val slotRoutines = groupedRoutines[timeSlot] ?: emptyList()
                if (slotRoutines.isNotEmpty()) {
                    item {
                        Text(
                            text = "$timeSlot Routines",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(top = 6.dp)
                        )
                    }

                    items(slotRoutines, key = { it.id }) { routine ->
                        val isDone = completedIdsForToday.contains(routine.id)
                        val icon = CategoryIcons.getIcon(routine.iconName)

                        var showItemMenu by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isDone) 0.dp else 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleCompletion(routine, !isDone) }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Checkbox circle
                                    Surface(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .clickable { onToggleCompletion(routine, !isDone) }
                                            .testTag("routine_checkbox_${routine.id}"),
                                        shape = CircleShape,
                                        color = if (isDone) PriorityLow else Color.Transparent,
                                        border = if (isDone) null else androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                                    ) {
                                        if (isDone) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                    }

                                    // Routine Icon
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = routine.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isDone) FontWeight.Normal else FontWeight.SemiBold,
                                            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                                            color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = routine.scheduledTime,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (routine.categoryName.isNotBlank()) {
                                                Text(
                                                    text = "• ${routine.categoryName}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }

                                // Routine Action Menu
                                Box {
                                    IconButton(
                                        onClick = { showItemMenu = true },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription = "Options",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = showItemMenu,
                                        onDismissRequest = { showItemMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Edit Habit") },
                                            onClick = {
                                                showItemMenu = false
                                                editingRoutine = routine
                                            },
                                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Delete Habit", color = MaterialTheme.colorScheme.error) },
                                            onClick = {
                                                showItemMenu = false
                                                onDeleteRoutine(routine)
                                            },
                                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
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

    if (showAddDialog || editingRoutine != null) {
        RoutineEditDialog(
            initialRoutine = editingRoutine,
            categories = categories,
            onSave = { routine ->
                if (editingRoutine != null) {
                    onUpdateRoutine(routine)
                } else {
                    onAddRoutine(routine)
                }
                showAddDialog = false
                editingRoutine = null
            },
            onDismiss = {
                showAddDialog = false
                editingRoutine = null
            }
        )
    }
}

@Composable
private fun RoutineEditDialog(
    initialRoutine: RoutineItem?,
    categories: List<TaskCategory>,
    onSave: (RoutineItem) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialRoutine?.title ?: "") }
    var timeOfDay by remember { mutableStateOf(initialRoutine?.timeOfDay ?: "Morning") }
    var scheduledTime by remember { mutableStateOf(initialRoutine?.scheduledTime ?: "08:00 AM") }
    var selectedCategoryName by remember { mutableStateOf(initialRoutine?.categoryName ?: "Personal") }
    var selectedIconName by remember { mutableStateOf(initialRoutine?.iconName ?: "CheckCircle") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialRoutine == null) "New Habit / Routine" else "Edit Habit",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title") },
                    placeholder = { Text("e.g., Read 15 pages, Drink water") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("routine_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Time of Day selector
                Text("Time of Day", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Morning", "Afternoon", "Evening", "Night").forEach { slot ->
                        val isSelected = timeOfDay == slot
                        FilterChip(
                            selected = isSelected,
                            onClick = { timeOfDay = slot },
                            label = { Text(slot, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = scheduledTime,
                    onValueChange = { scheduledTime = it },
                    label = { Text("Scheduled Time") },
                    placeholder = { Text("07:30 AM") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Icon Picker
                Text("Pick Icon", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CategoryIcons.availableIcons) { (iconKey, vector) ->
                        val isSelected = selectedIconName == iconKey
                        Surface(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable { selectedIconName = iconKey },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = vector,
                                    contentDescription = iconKey,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val item = RoutineItem(
                            id = initialRoutine?.id ?: 0,
                            title = title.trim(),
                            timeOfDay = timeOfDay,
                            scheduledTime = scheduledTime.trim(),
                            categoryName = selectedCategoryName,
                            iconName = selectedIconName,
                            createdAtMillis = initialRoutine?.createdAtMillis ?: System.currentTimeMillis()
                        )
                        onSave(item)
                    }
                }
            ) {
                Text("Save Habit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
