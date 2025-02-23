package com.pezont.teammates.ui.screens

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R
import com.pezont.teammates.models.ContentType
import com.pezont.teammates.models.User
import com.pezont.teammates.ui.Dots
import com.pezont.teammates.ui.TeammatesTopAppBar
import com.pezont.teammates.ui.items.TeammatesProfile
import com.pezont.teammates.ui.navigation.NavigationDestination

object ProfileDestination : NavigationDestination {
    override val route = "profile"
    override val titleRes = R.string.profile
}

@Composable
fun ProfileScreen(
    navigateToMyQuestionnaires: () -> Unit,
    logout: () -> Unit,
    user: User,

    bottomBar: @Composable () -> Unit = {}
    ) {
    Scaffold(
        topBar = {
            TeammatesTopAppBar(
                title = stringResource(ProfileDestination.titleRes),
                canNavigateBack = false,
                action = {
                    IconButton(
                        {}
                    ) {
                        Icon(
                            imageVector = Dots,
                            contentDescription = "Localized description",
                        )
                    }

                }
            )
        },

        bottomBar = bottomBar



        ){ paddingValues ->
        TeammatesProfile(
            navigateToMyQuestionnaires,
            logout,
            user,
            paddingValues
        )


    }


    }


