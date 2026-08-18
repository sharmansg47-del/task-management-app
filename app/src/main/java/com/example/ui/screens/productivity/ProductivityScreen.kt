package com.example.ui.screens.productivity

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.common.parseHexColor
import com.example.ui.theme.PriorityLow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ProductivityScreen(
    tasks: List<TaskItem>,
    categories: List<TaskCategory>,
    routines: List<RoutineItem>,
    routineCompletions: List<RoutineCompletion>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val todayEpoch = today.toEpochDay()

    // 1. Overall Metrics
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val overallRate = if (totalTasks > 0) ((completedTasks.toFloat() / totalTasks.toFloat()) * 100).toInt() else 0

    // 2. This Week's Metrics (Last 7 days: today - 6 to today)
    val last7Days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val last7DaysEpochs = last7Days.map { it.toEpochDay() }

    val tasksLast7Days = tasks.filter { it.dueDate != null && last7DaysEpochs.contains(it.dueDate) }
    val completedLast7Days = tasksLast7Days.count { it.isCompleted }
    val totalLast7Days = tasksLast7Days.size
    val weeklyRate = if (totalLast7Days > 0) ((completedLast7Days.toFloat() / totalLast7Days.toFloat()) * 100).toInt() else 0

    // 3. Category Breakdown
    val tasksByCategory = tasks.groupBy { it.categoryId ?: 0L }

    // 4. Streak Calculation
    var currentStreak = 0
    var checkDate = today
    while (true) {
        val checkEpoch = checkDate.toEpochDay()
        val dayTasks = tasks.filter { it.dueDate == checkEpoch }
        val dayRoutines = routineCompletions.filter { it.dateEpochDay == checkEpoch }

        // If user completed at least 1 task or routine on this day
        val hadActivity = dayTasks.any { it.isCompleted } || dayRoutines.isNotEmpty()

        if (hadActivity) {
            currentStreak++
            checkDate = checkDate.minusDays(1)
        } else {
            // If today has no completed items yet, don't break streak if yesterday was active
            if (checkDate == today && currentStreak == 0) {
                checkDate = checkDate.minusDays(1)
                continue
            }
            break
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("productivity_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Productivity Insights",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Track your consistency, completions, and habits",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Streak & High-Level Hero Card
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
                                    Color(0xFFF97316).copy(alpha = 0.12f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFF97316),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Current Streak",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentStreak > 0) "$currentStreak Days in a row! Keep going 🔥" else "Complete today's task to ignite your streak!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF97316).copy(alpha = 0.2f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$currentStreak d",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color(0xFFF97316)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3 Metric Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            label = "Completion Rate",
                            value = "$overallRate%",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Tasks Done",
                            value = "$completedTasks",
                            color = PriorityLow,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Pending",
                            value = "${totalTasks - completedTasks}",
                            color = Color(0xFFE11D48),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Weekly Activity Chart (Last 7 Days Bar Graph)
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
                        .padding(18.dp)
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
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Weekly Activity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "$completedLast7Days/$totalLast7Days completed ($weeklyRate%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 7-day visual columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        last7Days.forEach { date ->
                            val epoch = date.toEpochDay()
                            val isCurrentDay = date == today
                            val dayTasks = tasks.filter { it.dueDate == epoch }
                            val total = dayTasks.size
                            val completed = dayTasks.count { it.isCompleted }
                            val heightRatio = if (total > 0) completed.toFloat() / total.toFloat() else 0f

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                // Task count tag
                                Text(
                                    text = "$completed",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (completed > 0) PriorityLow else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Bar
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height(70.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    if (total > 0) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height((70 * heightRatio.coerceAtLeast(0.15f)).dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (isCurrentDay) MaterialTheme.colorScheme.primary else PriorityLow
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Day Label
                                Text(
                                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrentDay) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isCurrentDay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Breakdown Section
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
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Insights, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Tasks by Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (categories.isEmpty() || totalTasks == 0) {
                        Text(
                            text = "No category data available yet.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        categories.forEach { category ->
                            val catTasks = tasks.filter { it.categoryId == category.id || it.categoryName.equals(category.name, ignoreCase = true) }
                            val count = catTasks.size
                            val catCompleted = catTasks.count { it.isCompleted }
                            val catPercent = if (totalTasks > 0) ((count.toFloat() / totalTasks.toFloat()) * 100).toInt() else 0
                            val color = parseHexColor(category.colorHex)

                            if (count > 0) {
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                                            Text(
                                                text = category.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Text(
                                            text = "$catCompleted/$count done ($catPercent%)",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    LinearProgressIndicator(
                                        progress = { if (count > 0) catCompleted.toFloat() / count.toFloat() else 0f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = color,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
