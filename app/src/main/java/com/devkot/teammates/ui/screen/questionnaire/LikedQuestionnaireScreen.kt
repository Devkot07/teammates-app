package com.devkot.teammates.ui.screen.questionnaire

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.devkot.teammates.R
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.ui.components.LoadingItemWithText
import com.devkot.teammates.ui.components.TeammatesTextItem
import com.devkot.teammates.viewmodel.AuthorViewModel
import com.devkot.teammates.viewmodel.QuestionnairesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedQuestionnairesScreen(
    authorViewModel: AuthorViewModel,
    questionnairesViewModel: QuestionnairesViewModel,
    likedQuestionnaires: List<Questionnaire>,
    navigateToQuestionnaireDetails: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    val isRefreshing by questionnairesViewModel.isRefreshingLikedQuestionnaires.collectAsState()

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
    ) { paddingValues ->

        val pagerState = rememberPagerState(initialPage = 0) {
            likedQuestionnaires.size + 1
        }

        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = { questionnairesViewModel.refreshLikedQuestionnairesScreen() }
        ) {
            when (questionnairesViewModel.likedQuestionnairesState.collectAsState().value) {
                ContentState.LOADED ->
                    QuestionnairesVerticalPager(
                        questionnaires = likedQuestionnaires,
                        pagerState = pagerState,
                        navigateToQuestionnaireDetails = navigateToQuestionnaireDetails,
                        authorViewModel = authorViewModel,
                        lastItem = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    likedQuestionnaires.isEmpty() -> TeammatesTextItem(
                                        stringResource(R.string.you_haven_t_liked_any_questionnaire)
                                    )

                                    else -> TeammatesTextItem(stringResource(R.string.end))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                ContentState.LOADING, ContentState.INITIAL, ContentState.ERROR -> LoadingItemWithText()
            }
        }
    }
}