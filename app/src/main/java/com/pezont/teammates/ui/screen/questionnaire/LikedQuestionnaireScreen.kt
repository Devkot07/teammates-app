package com.pezont.teammates.ui.screen.questionnaire

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pezont.teammates.ui.ObserveState
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.components.LoadingItemWithText
import com.pezont.teammates.ui.components.TeammatesTextItem
import com.pezont.teammates.viewmodel.AuthorViewModel
import com.pezont.teammates.viewmodel.QuestionnairesViewModel
import kotlinx.coroutines.launch



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
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
    ) { paddingValues ->

        val pagerState = rememberPagerState(initialPage = 0) {
            likedQuestionnaires.size + 1
        }

        ObserveState(likedQuestionnaires) {
            if (!isLoadingMore && pagerState.currentPage == likedQuestionnaires.size) {
                isLoadingMore = true
                coroutineScope.launch {
                    questionnairesViewModel.loadLikedQuestionnaires()
                    isLoadingMore = false
                }
            }
        }

        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                coroutineScope.launch {
                    questionnairesViewModel.loadLikedQuestionnaires()
                    isRefreshing = false
                }
            }
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
                            isLoadingMore -> LoadingItemWithText()
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