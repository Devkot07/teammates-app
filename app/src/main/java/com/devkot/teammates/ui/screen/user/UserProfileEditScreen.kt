package com.devkot.teammates.ui.screen.user


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.ui.components.LoadingItemWithText
import com.devkot.teammates.viewmodel.UserViewModel


@Composable
fun UserProfileEditScreen(
    user: User,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    navigateUp: () -> Unit,
) {

    when (userViewModel.userProfileInfoState.collectAsState().value) {
        ContentState.LOADED, ContentState.INITIAL ->
            UserProfileEditItem(user, userViewModel, navigateUp = navigateUp)

        ContentState.LOADING, ContentState.ERROR ->
            Scaffold(topBar = topBar) { paddingValues ->
                LoadingItemWithText(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
    }
}



