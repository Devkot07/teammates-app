package com.devkot.teammates.ui.screen.questionnaire


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.ui.components.LoadingItemWithText
import com.devkot.teammates.viewmodel.QuestionnairesViewModel


@Composable
fun QuestionnaireEditScreen(
    questionnaire: Questionnaire,
    questionnairesViewModel: QuestionnairesViewModel,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    navigateUp: () -> Unit,
) {
    val context = LocalContext.current

    when (questionnairesViewModel.selectedQuestionnaireState.collectAsState().value) {
        ContentState.LOADED, ContentState.INITIAL ->
            QuestionnaireEditItem(questionnaire, onSave = { header, desc, game, uri ->
                questionnairesViewModel.updateQuestionnaire(header, desc, game, questionnaireId = questionnaire.questionnaireId, uri, context)
            }, navigateUp = navigateUp)

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



