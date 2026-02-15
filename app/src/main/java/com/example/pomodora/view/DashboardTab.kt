package com.example.pomodora.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector

enum class DashboardTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val index: Int
) {
    Home("Home", Icons.Filled.Home, Icons.Outlined.Home, 0),
    Focus("Focus", Icons.Filled.Timer, Icons.Outlined.Timer, 1),
    Profile("Profile", Icons.Filled.Person, Icons.Outlined.Person,2);

    // Helper to get tab by index
    companion object {
        fun getByIndex(index: Int): DashboardTab = entries.getOrElse(index) { Home }
    }
}