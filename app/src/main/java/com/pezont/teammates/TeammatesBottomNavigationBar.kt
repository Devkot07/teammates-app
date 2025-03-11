package com.pezont.teammates

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pezont.teammates.domain.model.BottomNavItem
import com.pezont.teammates.ui.screens.NavigationItemContent

@Composable
fun TeammatesBottomNavigationBar(
    currentTab: BottomNavItem,
    onTabPressed: ((BottomNavItem) -> Unit),
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier
) {
    Column {
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary)


        NavigationBar(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            for (navItem in navigationItemContentList) {
                NavigationBarItem(
                    selected = currentTab == navItem.bottomNavItem,
                    onClick = { onTabPressed(navItem.bottomNavItem) },
                    icon = {
                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = navItem.text
                        )
                    }
                )
            }
        }
    }
}