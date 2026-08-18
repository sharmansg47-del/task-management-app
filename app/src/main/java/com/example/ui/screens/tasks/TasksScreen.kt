package com.example.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.ui.common.EmptyStateView
import com.example.ui.common.SwipeableTaskCard
import com.example.ui.common.isTaskOverdue
import java.time.LocalDate

enum class TaskSortOption(val title: String) {
    DUE_DATE("Due Date"),
    PRIORITY("Priority"),
    CREATED_DATE("Creation Date"),
    ALPHABETICAL("Alphabetical (A-Z)")
}

enum class DateFilter(val title: String) {
    ALL("All Dates"),
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    UPCOMING("Upcoming"),
    OVERDUE("Overdue")
}

enum class StatusFilter(val title: String) {
    ALL("All Status"),
    PENDING("Pending Only"),
    COMPLETED("Completed Only")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    tasks: List<TaskItem>,
    categories: List<TaskCategory>,
    is24HourFormat: Boolean = false,
    onToggleTaskComplete: (TaskItem) -> Unit,
    onTaskClick: (TaskItem) -> Unit,
    onTaskDelete: (TaskItem) -> Unit,
    onTaskEdit: (TaskItem) -> Unit,
    onTaskDuplicate: (TaskItem) -> Unit,
    onQuickAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf(DateFilter.ALL) }
    var selectedStatusFilter by remember { mutableStateOf(StatusFilter.ALL) }
    var selectedPriorityFilter by remember { mutableStateOf<TaskPriority?>(null) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var currentSort by remember { mutableStateOf(TaskSortOption.DUE_DATE) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showFiltersSheet by remember { mutableStateOf(false) }

    val todayEpoch = LocalDate.now().toEpochDay()
    val tomorrowEpoch = LocalDate.now().plusDays(1).toEpochDay()

    // Filter tasks
    val filteredTasks = tasks.filter { task ->
        // 1. Search filter
        val matchesSearch = if (searchQuery.isBlank()) true else {
            task.title.contains(searchQuery, ignoreCase = true) ||
            task.description.contains(searchQuery, ignoreCase = true) ||
            task.categoryName.contains(searchQuery, ignoreCase = true) ||
            task.notes.contains(searchQuery, ignoreCase = true)
        }

        // 2. Date filter
        val matchesDate = when (selectedDateFilter) {
            DateFilter.ALL -> true
            DateFilter.TODAY -> task.dueDate == todayEpoch
            DateFilter.TOMORROW -> task.dueDate == tomorrowEpoch
            DateFilter.UPCOMING -> task.dueDate != null && task.dueDate > todayEpoch
            DateFilter.OVERDUE -> isTaskOverdue(task)
        }

        // 3. Status filter
        val matchesStatus = when (selectedStatusFilter) {
            StatusFilter.ALL -> true
            StatusFilter.PENDING -> !task.isCompleted
            StatusFilter.COMPLETED -> task.isCompleted
        }

        // 4. Priority filter
        val matchesPriority = if (selectedPriorityFilter == null) true else task.priority == selectedPriorityFilter

        // 5. Category filter
        val matchesCategory = if (selectedCategoryId == null) true else task.categoryId == selectedCategoryId

        matchesSearch && matchesDate && matchesStatus && matchesPriority && matchesCategory
    }.sortedWith { a, b ->
        when (currentSort) {
            TaskSortOption.DUE_DATE -> {
                // Completed at the bottom
                val completedCompare = a.isCompleted.compareTo(b.isCompleted)
                if (completedCompare != 0) return@sortedWith completedCompare
                val aDue = a.dueDate ?: Long.MAX_VALUE
                val bDue = b.dueDate ?: Long.MAX_VALUE
                aDue.compareTo(bDue)
            }
            TaskSortOption.PRIORITY -> {
                val completedCompare = a.isCompleted.compareTo(b.isCompleted)
                if (completedCompare != 0) return@sortedWith completedCompare
                b.priority.level.compareTo(a.priority.level)
            }
            TaskSortOption.CREATED_DATE -> {
                b.createdAtMillis.compareTo(a.createdAtMillis)
            }
            TaskSortOption.ALPHABETICAL -> {
                a.title.compareTo(b.title, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tasks_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header & Search Bar
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
                        text = "Task Center",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Sort Button & Menu
                    Box {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showSortMenu = true }
                                .testTag("sort_menu_button"),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = currentSort.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            TaskSortOption.entries.forEach { sortOption ->
                                DropdownMenuItem(
                                    text = { Text(sortOption.title) },
                                    onClick = {
                                        currentSort = sortOption
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search tasks, notes, categories...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = if (searchQuery.isNotBlank()) {
                        {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_search_field")
                )
            }
        }

        // 2. Horizontal Filter Strip
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Date Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateFilter.entries.forEach { filter ->
                        val isSelected = selectedDateFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDateFilter = filter },
                            label = { Text(filter.title, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Status & Priority Quick Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusFilter.entries.forEach { filter ->
                        val isSelected = selectedStatusFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedStatusFilter = filter },
                            label = { Text(filter.title, fontSize = 12.sp) }
                        )
                    }

                    // Category Pill Filters
                    categories.forEach { cat ->
                        val isSelected = selectedCategoryId == cat.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryId = if (isSelected) null else cat.id
                            },
                            label = { Text(cat.name, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // 3. Results Header & Count
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
                    text = "Results (${filteredTasks.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (selectedDateFilter != DateFilter.ALL || selectedStatusFilter != StatusFilter.ALL || selectedPriorityFilter != null || selectedCategoryId != null || searchQuery.isNotBlank()) {
                    Text(
                        text = "Reset Filters",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                searchQuery = ""
                                selectedDateFilter = DateFilter.ALL
                                selectedStatusFilter = StatusFilter.ALL
                                selectedPriorityFilter = null
                                selectedCategoryId = null
                            }
                            .padding(4.dp)
                    )
                }
            }
        }

        // 4. Task Items List
        if (filteredTasks.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No tasks found",
                    subtitle = "Try adjusting your search query or filters.",
                    actionButtonText = "+ Add New Task",
                    onActionClick = onQuickAddTask,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
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
