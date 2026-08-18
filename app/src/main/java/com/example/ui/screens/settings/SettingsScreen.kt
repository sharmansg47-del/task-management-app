package com.example.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskPriority
import com.example.data.model.UserSettings

@Composable
fun SettingsScreen(
    userSettings: UserSettings,
    onUpdateThemeMode: (String) -> Unit,
    onUpdateDefaultPriority: (TaskPriority) -> Unit,
    onUpdateDefaultReminder: (Int) -> Unit,
    onUpdateFirstDayOfWeek: (Int) -> Unit,
    onUpdateTimeFormat24h: (Boolean) -> Unit,
    onUpdateNotificationsEnabled: (Boolean) -> Unit,
    onUpdateSoundEnabled: (Boolean) -> Unit,
    onUpdateVibrateEnabled: (Boolean) -> Unit,
    onUpdateUserName: (String) -> Unit,
    onManageCategories: () -> Unit,
    onClearCompletedTasks: () -> Unit,
    onResetToSampleData: () -> Unit,
    onExportDataJson: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNameDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showPriorityDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen_content"),
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
                    text = "Settings & Preferences",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Personalize appearance, notifications, and defaults",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Profile / Display Name
        item {
            SettingsSectionHeader(title = "Profile")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                SettingsClickableRow(
                    icon = Icons.Default.Person,
                    title = "Display Name",
                    subtitle = userSettings.userName,
                    onClick = { showNameDialog = true }
                )
            }
        }

        // 2. Appearance Section
        item {
            SettingsSectionHeader(title = "Appearance & Layout")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsClickableRow(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = when (userSettings.themeMode) {
                            "light" -> "Light Theme"
                            "dark" -> "Dark Theme"
                            else -> "System Default"
                        },
                        onClick = { showThemeDialog = true }
                    )

                    SettingsSwitchRow(
                        icon = Icons.Default.Schedule,
                        title = "24-Hour Time Format",
                        subtitle = if (userSettings.timeFormat24h) "e.g. 14:30" else "e.g. 02:30 PM",
                        checked = userSettings.timeFormat24h,
                        onCheckedChange = onUpdateTimeFormat24h
                    )

                    SettingsClickableRow(
                        icon = Icons.Default.Schedule,
                        title = "First Day of the Week",
                        subtitle = if (userSettings.firstDayOfWeek == 1) "Monday" else "Sunday",
                        onClick = {
                            onUpdateFirstDayOfWeek(if (userSettings.firstDayOfWeek == 1) 7 else 1)
                        }
                    )
                }
            }
        }

        // 3. Task Defaults
        item {
            SettingsSectionHeader(title = "Task & Reminder Defaults")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsClickableRow(
                        icon = Icons.Default.Category,
                        title = "Manage Categories",
                        subtitle = "Add, edit, or customize category icons and colors",
                        onClick = onManageCategories
                    )

                    SettingsClickableRow(
                        icon = Icons.Default.Schedule,
                        title = "Default Priority",
                        subtitle = userSettings.defaultPriority.title,
                        onClick = { showPriorityDialog = true }
                    )

                    SettingsClickableRow(
                        icon = Icons.Default.Notifications,
                        title = "Default Reminder Time",
                        subtitle = if (userSettings.defaultReminderMinutes == 0) "At exact task time" else "${userSettings.defaultReminderMinutes} minutes before",
                        onClick = { showReminderDialog = true }
                    )
                }
            }
        }

        // 4. Notifications
        item {
            SettingsSectionHeader(title = "Notification Alerts")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsSwitchRow(
                        icon = Icons.Default.Notifications,
                        title = "Task Reminder Notifications",
                        subtitle = "Receive alert reminders for scheduled deadlines",
                        checked = userSettings.notificationsEnabled,
                        onCheckedChange = onUpdateNotificationsEnabled
                    )

                    SettingsSwitchRow(
                        icon = Icons.Default.VolumeUp,
                        title = "Sound Alerts",
                        subtitle = "Play sound when reminders trigger",
                        checked = userSettings.soundEnabled,
                        onCheckedChange = onUpdateSoundEnabled
                    )

                    SettingsSwitchRow(
                        icon = Icons.Default.Vibration,
                        title = "Vibrate Alerts",
                        subtitle = "Vibrate device for reminders",
                        checked = userSettings.vibrateEnabled,
                        onCheckedChange = onUpdateVibrateEnabled
                    )
                }
            }
        }

        // 5. Data & Maintenance
        item {
            SettingsSectionHeader(title = "Data Management")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsClickableRow(
                        icon = Icons.Default.Download,
                        title = "Export Backup Data",
                        subtitle = "Save a JSON backup of your tasks and routines",
                        onClick = onExportDataJson
                    )

                    SettingsClickableRow(
                        icon = Icons.Default.DeleteSweep,
                        title = "Clear Completed Tasks",
                        subtitle = "Remove past completed tasks to declutter",
                        onClick = { showClearConfirmDialog = true }
                    )

                    SettingsClickableRow(
                        icon = Icons.Default.RestartAlt,
                        title = "Reset to Sample Data",
                        subtitle = "Restore default categories and initial template tasks",
                        onClick = { showResetConfirmDialog = true },
                        isDestructive = true
                    )
                }
            }
        }

        // Version Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "DayTask v1.0 • Offline First & Fast",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Dialog: Edit Name
    if (showNameDialog) {
        var tempName by remember { mutableStateOf(userSettings.userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Display Name") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("user_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            onUpdateUserName(tempName.trim())
                            showNameDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Theme Mode
    if (showThemeDialog) {
        val themes = listOf("system" to "System Default", "light" to "Light Mode", "dark" to "Dark Mode")
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    themes.forEach { (mode, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = userSettings.themeMode == mode,
                                onClick = {
                                    onUpdateThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Text(text = label, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Dialog: Default Priority
    if (showPriorityDialog) {
        AlertDialog(
            onDismissRequest = { showPriorityDialog = false },
            title = { Text("Default Priority") },
            text = {
                Column {
                    TaskPriority.entries.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateDefaultPriority(p)
                                    showPriorityDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = userSettings.defaultPriority == p,
                                onClick = {
                                    onUpdateDefaultPriority(p)
                                    showPriorityDialog = false
                                }
                            )
                            Text(text = p.title, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPriorityDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Dialog: Default Reminder
    if (showReminderDialog) {
        val options = listOf(0 to "At task time", 5 to "5 minutes before", 10 to "10 minutes before", 15 to "15 minutes before", 30 to "30 minutes before", 60 to "1 hour before")
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = { Text("Default Reminder Time") },
            text = {
                Column {
                    options.forEach { (mins, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateDefaultReminder(mins)
                                    showReminderDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = userSettings.defaultReminderMinutes == mins,
                                onClick = {
                                    onUpdateDefaultReminder(mins)
                                    showReminderDialog = false
                                }
                            )
                            Text(text = label, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showReminderDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Dialog: Confirm Clear Completed
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear Completed Tasks?") },
            text = { Text("This will permanently delete all completed tasks from the database.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearCompletedTasks()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Completed")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Confirm Reset
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset to Sample Data?") },
            text = { Text("This will replace all current data with sample starter tasks, categories, and daily habits.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetToSampleData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp)
    )
}

@Composable
private fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
