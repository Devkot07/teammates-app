package com.pezont.teammates.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.pezont.teammates.domain.model.enums.BottomNavItem

data class NavigationItemContent(
    val bottomNavItem: BottomNavItem, val icon: ImageVector, val text: String
)
