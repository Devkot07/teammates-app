package com.pezont.teammates.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.ui.items.AuthorProfile
import com.pezont.teammates.ui.items.UserProfile
import com.pezont.teammates.ui.navigation.NavigationDestination

object AuthorProfileDestination : NavigationDestination {
    override val route = "author_profile"
    override val titleRes = R.string.profile
}

//TODO Screen
@Composable
fun AuthorProfileScreen(
    author: User,
    navigateToMyQuestionnaires: () -> Unit,
    logout: () -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = topBar,
    ) { paddingValues ->
        AuthorProfile(
            navigateToMyQuestionnaires, logout, author, paddingValues
        )
        // TODO Horizontal Pager
    }
}


