package com.pezont.teammates.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pezont.teammates.R
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.TeammatesViewModel
import com.pezont.teammates.ui.navigation.NavigationDestination

object ProfileDestination : NavigationDestination {
    override val route = "profile"
    override val titleRes = R.string.profile
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateToMyQuestionnaires: () -> Unit,
    teammatesUiState: TeammatesUiState.Home,
    viewModel: TeammatesViewModel,

) {
    Scaffold(
        topBar = {
            TeammatesTopAppBar(
                title = stringResource(ProfileDestination.titleRes),
                canNavigateBack = false,
            )
        }){ paddingValues ->
        Profile(
            navigateToMyQuestionnaires, teammatesUiState, viewModel, paddingValues
        )



    }


}


