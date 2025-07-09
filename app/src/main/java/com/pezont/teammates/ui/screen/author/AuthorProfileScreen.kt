package com.pezont.teammates.ui.screen.author

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.ui.components.LoadingItemWithText
import com.pezont.teammates.viewmodel.AuthorViewModel


@Composable
fun AuthorProfileScreen(
    authorViewModel: AuthorViewModel,
    contentState: ContentState,
    author: User,
    authorQuestionnaires: List<Questionnaire>,
    starAction: () -> Unit,
    navigateToQuestionnaireDetails: () -> Unit,
    topBar: @Composable () -> Unit = {},
    modifier: Modifier,
) {
    Scaffold(
        topBar = topBar,
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (contentState == ContentState.LOADING)
                LoadingItemWithText()
            else {
                AuthorProfileItem(
                    author = author,
                    authorQuestionnaires = authorQuestionnaires,
                    updateSelectedQuestionnaire = authorViewModel::updateSelectedQuestionnaire,
                    navigateToQuestionnaireDetails = navigateToQuestionnaireDetails,
                    starAction = starAction
                )
            }
        }
    }
}


