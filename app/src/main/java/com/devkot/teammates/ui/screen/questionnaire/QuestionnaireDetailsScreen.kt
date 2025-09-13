package com.devkot.teammates.ui.screen.questionnaire

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.ui.components.LoadingItemWithText
import com.devkot.teammates.viewmodel.QuestionnairesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireDetailsScreen(
    author: User,
    questionnaire: Questionnaire,
    likedQuestionnaires: List<Questionnaire>,
    questionnairesViewModel: QuestionnairesViewModel,
    navigateToAuthorProfile: () -> Unit,
    topBar: @Composable () -> Unit = {},
) {

    val isRefreshing by questionnairesViewModel.isRefreshingSelectedQuestionnaire.collectAsState()
    val isLiked = likedQuestionnaires.any { it.questionnaireId == questionnaire.questionnaireId }
    Scaffold(
        topBar = topBar
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            isRefreshing = isRefreshing,
            onRefresh = { questionnairesViewModel.refreshSelectedQuestionnaire() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()

                ) {
                when (questionnairesViewModel.selectedQuestionnaireState.collectAsState().value) {
                    ContentState.LOADED, ContentState.INITIAL ->

                            QuestionnaireDetailsItem(
                                authorNickname = author.nickname,
                                authorState = questionnairesViewModel.authorState.collectAsState().value,
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
                                modifier = Modifier
                            )


                    ContentState.LOADING, ContentState.ERROR -> LoadingItemWithText()
                }
            }

        }
    }
}
