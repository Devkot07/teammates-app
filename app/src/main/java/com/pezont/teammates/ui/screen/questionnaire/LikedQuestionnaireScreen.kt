package com.pezont.teammates.ui.screen.questionnaire

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
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.components.TeammatesTextItem
import com.pezont.teammates.viewmodel.AuthorViewModel
import com.pezont.teammates.viewmodel.QuestionnairesViewModel


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

        //TODO content state
        //TODO move to viewModel
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = { questionnairesViewModel.refreshLikedQuestionnairesScreen() }
        ) {
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
                            likedQuestionnaires.isEmpty() -> TeammatesTextItem(stringResource(R.string.you_haven_t_liked_any_questionnaire))
                            else -> TeammatesTextItem(stringResource(R.string.end))
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}