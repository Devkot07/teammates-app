package com.devkot.teammates.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.devkot.teammates.domain.model.enums.BottomNavItem

data class NavigationItemContent(
    val bottomNavItem: BottomNavItem, val icon: ImageVector, val text: String
)
