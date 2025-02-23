package com.pezont.teammates.models

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItemContent(
    val contentType: ContentType,
    val icon: ImageVector,
    val text: String
)