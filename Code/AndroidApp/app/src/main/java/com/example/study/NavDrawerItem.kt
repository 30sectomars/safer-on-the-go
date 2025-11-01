package com.example.study

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavDrawerItem(var route: String, var icon: ImageVector, var title: String) {
    object Home : NavDrawerItem("home", Icons.Default.Home, "Home")
    object Vertical : NavDrawerItem("vertical", Icons.Default.Add, "Select Vertical")
    object Horizontal : NavDrawerItem("horizontal", Icons.Default.Add, "Select Horizontal")
    object TwoDimensional : NavDrawerItem("2D", Icons.Default.Add, "Select 2D")
    object TwoDimensionalFS : NavDrawerItem("2DFS", Icons.Default.Add, "Select 2D FS")
    object Settings : NavDrawerItem("settings", Icons.Default.Settings, "Settings")
    object LogUDP : NavDrawerItem("logUDP", Icons.Default.Settings, "Log UDP")
}