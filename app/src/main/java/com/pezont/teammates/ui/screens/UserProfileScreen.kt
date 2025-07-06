package com.pezont.teammates.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.pezont.teammates.R
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.ui.Dots
import com.pezont.teammates.ui.DropdownItem
import com.pezont.teammates.ui.TeammatesDropdownMenu
import com.pezont.teammates.ui.TeammatesTopAppBar
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
    navigateToUserProfileEditScreen: () -> Unit,
    viewModel: TeammatesViewModel
) {
    Scaffold(
        topBar = {
            var showDropDownMenu by remember { mutableStateOf(false) }
            val items = listOf(
                DropdownItem(
                    text = stringResource(R.string.logout),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = {
                        viewModel.logout()
                        showDropDownMenu = false
                    }

                ),
                DropdownItem(
                    text = stringResource(R.string.edit_information),
                    icon = Icons.Filled.ModeEditOutline,
                    onClick = {
                        navigateToUserProfileEditScreen()
                        showDropDownMenu = false
                    }
                ),
            )
            TeammatesTopAppBar(
                title = stringResource(UserProfileDestination.titleRes),
                actions = {
                    IconButton(onClick = { showDropDownMenu = !showDropDownMenu }) {
                        Icon(Dots, contentDescription = null)
                    }
                    TeammatesDropdownMenu(
                        expanded = showDropDownMenu,
                        items = items,
                        onDismissRequest = { showDropDownMenu = false },
                    )
                },
                canNavigateBack = false
            )
        }
    ) { paddingValues ->
        UserProfile(
            navigateToMyQuestionnaires = navigateToMyQuestionnaires,
            user = user,
            paddingValues = paddingValues
        )
    }
}
