package com.pezont.teammates.ui.screens


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pezont.teammates.R
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.UiState
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.ui.items.LoadingItemWithText
import com.pezont.teammates.ui.items.UserProfileEditItem
import com.pezont.teammates.ui.navigation.NavigationDestination

object UserProfileEditDestination : NavigationDestination {
    override val route = "user_profile_edit"
    override val titleRes = R.string.profile
}


@Composable
fun UserProfileEditScreen(
    uiState: UiState,
    user: User,
    viewModel: TeammatesViewModel,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    navigateUp: () -> Unit,
) {
    if (uiState.contentState == ContentState.LOADING) {

        Scaffold(
            topBar = topBar,
        ) { paddingValues ->
            LoadingItemWithText(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    } else {
        UserProfileEditItem(user, viewModel, navigateUp = navigateUp)
    }
}



