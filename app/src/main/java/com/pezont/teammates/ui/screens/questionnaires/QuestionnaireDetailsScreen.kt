package com.pezont.teammates.ui.screens.questionnaires

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pezont.teammates.R
import com.pezont.teammates.UiState
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.items.QuestionnaireDetailsItem
import com.pezont.teammates.ui.navigation.NavigationDestination

object QuestionnaireDetailsDestination : NavigationDestination {
    override val route = "questionnaires_details"
    override val titleRes = R.string.questionnaire
}


@Composable
fun QuestionnaireDetailsScreen(
    viewModel: TeammatesViewModel,
    uiState: UiState,
    questionnaire: Questionnaire,
    navigateToAuthorProfile: () -> Unit,
    topBar: @Composable () -> Unit = {},

    ) {

    Scaffold(
        topBar = topBar
    ) { innerPadding ->
        QuestionnaireDetailsItem(viewModel, uiState, questionnaire,navigateToAuthorProfile, Modifier.padding(innerPadding))

    }


}


