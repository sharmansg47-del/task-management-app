package com.example.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIcons {
    val availableIcons = listOf(
        "School" to Icons.Filled.School,
        "Home" to Icons.Filled.Home,
        "SportsSoccer" to Icons.Filled.SportsSoccer,
        "FitnessCenter" to Icons.Filled.FitnessCenter,
        "Code" to Icons.Filled.Code,
        "Work" to Icons.Filled.Work,
        "MenuBook" to Icons.Filled.MenuBook,
        "Restaurant" to Icons.Filled.Restaurant,
        "WaterDrop" to Icons.Filled.WaterDrop,
        "AutoStories" to Icons.Filled.AutoStories,
        "Favorite" to Icons.Filled.Favorite,
        "Star" to Icons.Filled.Star,
        "ShoppingBag" to Icons.Filled.ShoppingBag,
        "Flight" to Icons.Filled.Flight,
        "DirectionsCar" to Icons.Filled.DirectionsCar,
        "Palette" to Icons.Filled.Palette,
        "Category" to Icons.Filled.Category,
        "TaskAlt" to Icons.Filled.TaskAlt,
        "CheckCircle" to Icons.Filled.CheckCircle,
        "Bookmark" to Icons.Filled.Bookmark
    )

    fun getIcon(name: String): ImageVector {
        return availableIcons.firstOrNull { it.first.equals(name, ignoreCase = true) }?.second
            ?: Icons.Filled.Category
    }
}
