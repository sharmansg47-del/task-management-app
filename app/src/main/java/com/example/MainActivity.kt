package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.MainViewModel
import com.example.ui.screens.addtask.AddEditTaskSheet
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.categories.ManageCategoriesDialog
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.productivity.ProductivityScreen
import com.example.ui.screens.routines.RoutinesScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.tasks.TaskDetailSheet
import com.example.ui.screens.tasks.TasksScreen
import com.example.ui.theme.DayTaskTheme
import kotlinx.coroutines.launch

enum class AppNavigationTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Default.Home, "nav_tab_home"),
    TASKS("Tasks", Icons.Default.ListAlt, "nav_tab_tasks"),
    CALENDAR("Calendar", Icons.Default.CalendarMonth, "nav_tab_calendar"),
    ROUTINES("Routines", Icons.Default.Repeat, "nav_tab_routines"),
    INSIGHTS("Insights", Icons.Default.Insights, "nav_tab_insights"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_tab_settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(application as DayTaskApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userSettings by viewModel.userSettings.collectAsState()

            DayTaskTheme(themeMode = userSettings.themeMode) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val tasks by viewModel.tasks.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val routines by viewModel.routines.collectAsState()
    val routineCompletions by viewModel.routineCompletions.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Dialog & Sheet States
    var showAddTaskSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskItem?>(null) }
    var viewingTaskDetails by remember { mutableStateOf<TaskItem?>(null) }
    var preselectedDateEpoch by remember { mutableStateOf<Long?>(null) }
    var showCategoryManager by remember { mutableStateOf(false) }

    // Android 13+ Notification Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar("Notifications disabled. You can enable them anytime in Settings.")
            }
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            0 -> "DayTask"
                            1 -> "All Tasks"
                            2 -> "Calendar"
                            3 -> "Daily Habits"
                            4 -> "Productivity"
                            5 -> "Settings"
                            else -> "DayTask"
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showCategoryManager = true },
                        modifier = Modifier.testTag("top_bar_category_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Manage Categories",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { selectedTab = 5 },
                        modifier = Modifier.testTag("top_bar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (selectedTab == 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(
                    AppNavigationTab.HOME,
                    AppNavigationTab.TASKS,
                    AppNavigationTab.CALENDAR,
                    AppNavigationTab.ROUTINES,
                    AppNavigationTab.INSIGHTS
                )

                navItems.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab != 5) {
                FloatingActionButton(
                    onClick = {
                        editingTask = null
                        preselectedDateEpoch = null
                        showAddTaskSheet = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("main_add_task_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    HomeScreen(
                        tasks = tasks,
                        categories = categories,
                        routines = routines,
                        routineCompletions = routineCompletions,
                        userName = userSettings.userName,
                        is24HourFormat = userSettings.timeFormat24h,
                        onToggleTaskComplete = { viewModel.toggleTaskComplete(it) },
                        onTaskClick = { viewingTaskDetails = it },
                        onTaskDelete = { viewModel.deleteTask(it) },
                        onTaskEdit = {
                            editingTask = it
                            showAddTaskSheet = true
                        },
                        onTaskDuplicate = { viewModel.duplicateTask(it) },
                        onToggleRoutineComplete = { routine, isDone ->
                            viewModel.toggleRoutineComplete(routine, isDone)
                        },
                        onNavigateToTasks = { selectedTab = 1 },
                        onNavigateToRoutines = { selectedTab = 3 },
                        onQuickAddTask = {
                            editingTask = null
                            preselectedDateEpoch = null
                            showAddTaskSheet = true
                        }
                    )
                }
                1 -> {
                    TasksScreen(
                        tasks = tasks,
                        categories = categories,
                        is24HourFormat = userSettings.timeFormat24h,
                        onToggleTaskComplete = { viewModel.toggleTaskComplete(it) },
                        onTaskClick = { viewingTaskDetails = it },
                        onTaskDelete = { viewModel.deleteTask(it) },
                        onTaskEdit = {
                            editingTask = it
                            showAddTaskSheet = true
                        },
                        onTaskDuplicate = { viewModel.duplicateTask(it) },
                        onQuickAddTask = {
                            editingTask = null
                            preselectedDateEpoch = null
                            showAddTaskSheet = true
                        }
                    )
                }
                2 -> {
                    CalendarScreen(
                        tasks = tasks,
                        categories = categories,
                        is24HourFormat = userSettings.timeFormat24h,
                        firstDayOfWeek = userSettings.firstDayOfWeek,
                        onToggleTaskComplete = { viewModel.toggleTaskComplete(it) },
                        onTaskClick = { viewingTaskDetails = it },
                        onTaskDelete = { viewModel.deleteTask(it) },
                        onTaskEdit = {
                            editingTask = it
                            showAddTaskSheet = true
                        },
                        onTaskDuplicate = { viewModel.duplicateTask(it) },
                        onAddTaskForDate = { dateEpoch ->
                            editingTask = null
                            preselectedDateEpoch = dateEpoch
                            showAddTaskSheet = true
                        }
                    )
                }
                3 -> {
                    RoutinesScreen(
                        routines = routines,
                        routineCompletions = routineCompletions,
                        categories = categories,
                        onToggleCompletion = { routine, isDone ->
                            viewModel.toggleRoutineComplete(routine, isDone)
                        },
                        onAddRoutine = { viewModel.addRoutine(it) },
                        onUpdateRoutine = { viewModel.updateRoutine(it) },
                        onDeleteRoutine = { viewModel.deleteRoutine(it) }
                    )
                }
                4 -> {
                    ProductivityScreen(
                        tasks = tasks,
                        categories = categories,
                        routines = routines,
                        routineCompletions = routineCompletions
                    )
                }
                5 -> {
                    SettingsScreen(
                        userSettings = userSettings,
                        onUpdateThemeMode = { viewModel.setThemeMode(it) },
                        onUpdateDefaultPriority = { viewModel.setDefaultPriority(it) },
                        onUpdateDefaultReminder = { viewModel.setDefaultReminderMinutes(it) },
                        onUpdateFirstDayOfWeek = { viewModel.setFirstDayOfWeek(it) },
                        onUpdateTimeFormat24h = { viewModel.setTimeFormat24h(it) },
                        onUpdateNotificationsEnabled = { viewModel.setNotificationsEnabled(it) },
                        onUpdateSoundEnabled = { viewModel.setSoundEnabled(it) },
                        onUpdateVibrateEnabled = { viewModel.setVibrateEnabled(it) },
                        onUpdateUserName = { viewModel.setUserName(it) },
                        onManageCategories = { showCategoryManager = true },
                        onClearCompletedTasks = {
                            viewModel.clearCompletedTasks()
                            scope.launch {
                                snackbarHostState.showSnackbar("Completed tasks cleared.")
                            }
                        },
                        onResetToSampleData = {
                            viewModel.resetToSampleData()
                            scope.launch {
                                snackbarHostState.showSnackbar("Database reset to sample template data.")
                            }
                        },
                        onExportDataJson = {
                            scope.launch {
                                val json = viewModel.generateExportJson()
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_SUBJECT, "DayTask Backup")
                                    putExtra(Intent.EXTRA_TEXT, json)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Export DayTask Backup"))
                            }
                        }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Add/Edit Task
    if (showAddTaskSheet) {
        AddEditTaskSheet(
            initialTask = editingTask,
            preselectedDateEpochDay = preselectedDateEpoch,
            categories = categories,
            defaultPriority = userSettings.defaultPriority,
            defaultReminderMinutes = userSettings.defaultReminderMinutes,
            is24HourFormat = userSettings.timeFormat24h,
            onSaveTask = { task ->
                if (editingTask != null) {
                    viewModel.updateTask(task)
                    scope.launch { snackbarHostState.showSnackbar("Task updated!") }
                } else {
                    viewModel.insertTask(task)
                    scope.launch { snackbarHostState.showSnackbar("Task created successfully!") }
                }
                showAddTaskSheet = false
                editingTask = null
                preselectedDateEpoch = null
            },
            onDismiss = {
                showAddTaskSheet = false
                editingTask = null
                preselectedDateEpoch = null
            },
            onOpenCategoryManager = {
                showCategoryManager = true
            }
        )
    }

    // Modal Bottom Sheet for Viewing Task Details
    viewingTaskDetails?.let { task ->
        val category = categories.firstOrNull { it.id == task.categoryId }
        TaskDetailSheet(
            task = task,
            category = category,
            is24HourFormat = userSettings.timeFormat24h,
            onToggleComplete = { viewModel.toggleTaskComplete(it) },
            onEditTask = {
                viewingTaskDetails = null
                editingTask = it
                showAddTaskSheet = true
            },
            onDuplicateTask = {
                viewingTaskDetails = null
                viewModel.duplicateTask(it)
                scope.launch { snackbarHostState.showSnackbar("Task duplicated!") }
            },
            onDeleteTask = {
                viewingTaskDetails = null
                viewModel.deleteTask(it)
                scope.launch { snackbarHostState.showSnackbar("Task deleted.") }
            },
            onDismiss = { viewingTaskDetails = null }
        )
    }

    // Modal Dialog for Category Management
    if (showCategoryManager) {
        ManageCategoriesDialog(
            categories = categories,
            onAddCategory = { name, icon, color ->
                viewModel.addCategory(name, icon, color)
                scope.launch { snackbarHostState.showSnackbar("Category created!") }
            },
            onUpdateCategory = { cat ->
                viewModel.updateCategory(cat)
                scope.launch { snackbarHostState.showSnackbar("Category updated.") }
            },
            onDeleteCategory = { cat ->
                viewModel.deleteCategory(cat)
                scope.launch { snackbarHostState.showSnackbar("Category deleted.") }
            },
            onDismiss = { showCategoryManager = false }
        )
    }
}
