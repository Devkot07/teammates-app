package com.pezont.teammates.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.ui.items.UserProfile
import com.pezont.teammates.ui.navigation.NavigationDestination

object UserProfileDestination : NavigationDestination {
    override val route = "user_profile"
    override val titleRes = R.string.profile
}

@Composable
fun UserProfileScreen(
    user: User,
    navigateToMyQuestionnaires: () -> Unit,
    logout: () -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = topBar,
    ) { paddingValues ->
        UserProfile(
            navigateToMyQuestionnaires, logout, user, paddingValues
        )
    }
}


