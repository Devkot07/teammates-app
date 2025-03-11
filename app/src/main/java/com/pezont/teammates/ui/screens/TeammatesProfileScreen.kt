package com.pezont.teammates.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.ui.items.TeammatesProfile
import com.pezont.teammates.ui.navigation.NavigationDestination

object ProfileDestination : NavigationDestination {
    override val route = "profile"
    override val titleRes = R.string.profile
}

@Composable
fun ProfileScreen(
    user: User,
    navigateToMyQuestionnaires: () -> Unit,
    logout: () -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = topBar,
    ) { paddingValues ->
        TeammatesProfile(
            navigateToMyQuestionnaires, logout, user, paddingValues
        )
    }
}


