package com.pezont.teammates.ui.screen

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
import androidx.compose.ui.unit.dp
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.ObserveState
import com.pezont.teammates.ui.components.LoadingItemWithText
import com.pezont.teammates.ui.screen.questionnaire.QuestionnairesVerticalPager
import com.pezont.teammates.viewmodel.AuthorViewModel
import com.pezont.teammates.viewmodel.QuestionnairesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeammatesHomeScreen(
    authorViewModel: AuthorViewModel,
    questionnairesViewModel: QuestionnairesViewModel,
    questionnaires: List<Questionnaire>,
    navigateToQuestionnaireDetails: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { paddingValues ->

        val isRefreshing by questionnairesViewModel.isRefreshingQuestionnaires.collectAsState()
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { questionnaires.size + 1 })

        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = { questionnairesViewModel.refreshHomeScreen() }
        ) {
            QuestionnairesVerticalPager(
                questionnaires = questionnaires,
                navigateToQuestionnaireDetails = navigateToQuestionnaireDetails,
                authorViewModel = authorViewModel,
                pagerState = pagerState,
                lastItem = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingItemWithText()
                    }
                },
                modifier = Modifier.fillMaxSize()

            )



            ObserveState(pagerState.currentPage) {
                if (pagerState.currentPage == questionnaires.size) {
                    questionnairesViewModel.loadMoreQuestionnaires(
                        currentPage = pagerState.currentPage,
                        questionnairesSize = questionnaires.size
                    )
                }
            }
        }
    }
}
