package com.pezont.teammates.ui.screen.user


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.ui.components.LoadingItemWithText
import com.pezont.teammates.viewmodel.UserViewModel


@Composable
fun UserProfileEditScreen(
    contentState: ContentState,
    user: User,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    navigateUp: () -> Unit,
) {
    if (contentState == ContentState.LOADING) {

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
        UserProfileEditItem(user, userViewModel, navigateUp = navigateUp)
    }
}



