package com.pezont.teammates.ui.screens.questionnaires

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.pezont.teammates.R
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.items.QuestionnaireDetailsItem
import com.pezont.teammates.ui.items.TeammatesLoadingItem
import com.pezont.teammates.ui.navigation.NavigationDestination

object QuestionnaireDetailsDestination : NavigationDestination {
    override val route = "questionnaires_details"
    override val titleRes = R.string.questionnaire
}


@Composable
fun QuestionnaireDetailsScreen(
    viewModel: TeammatesViewModel,
    questionnaire: Questionnaire,
    topBar: @Composable () -> Unit = {},

    ) {

    Scaffold(
        topBar = topBar
    ) { innerPadding ->
        QuestionnaireDetailsItem(viewModel, questionnaire, Modifier.padding(innerPadding))
    }


}


