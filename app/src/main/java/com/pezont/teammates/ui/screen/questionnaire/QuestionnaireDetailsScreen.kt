package com.pezont.teammates.ui.screen.questionnaire

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.viewmodel.QuestionnairesViewModel


@Composable
fun QuestionnaireDetailsScreen(
    author: User,
    contentState: ContentState,
    questionnaire: Questionnaire,
    likedQuestionnaires: List<Questionnaire>,
    questionnairesViewModel: QuestionnairesViewModel,
    navigateToAuthorProfile: () -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val isLiked = likedQuestionnaires.any { it.questionnaireId == questionnaire.questionnaireId }
    Scaffold(
        topBar = topBar
    ) { innerPadding ->
        val authorNickname = author.nickname
        QuestionnaireDetailsItem(
            authorNickname = authorNickname,
            contentState = contentState,
            questionnaire = questionnaire,
            isLiked = isLiked,
            likeAction = { questionnaire ->
                if (isLiked) {
                    questionnairesViewModel.unlikeQuestionnaire(questionnaire)
                } else {
                    questionnairesViewModel.likeQuestionnaire(questionnaire)
                }
            },
            navigateToAuthorProfile = navigateToAuthorProfile,
            modifier = Modifier.padding(innerPadding)
        )
    }
}


