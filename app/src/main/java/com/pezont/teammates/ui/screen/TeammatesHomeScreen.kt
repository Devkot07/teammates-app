package com.pezont.teammates.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pezont.teammates.ObserveState
import com.pezont.teammates.R
import com.pezont.teammates.viewmodel.TeammatesViewModel
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.components.LoadingItemWithText
import com.pezont.teammates.ui.navigation.NavigationDestination
import com.pezont.teammates.ui.screen.questionnaire.QuestionnairesVerticalPager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeammatesHomeScreen(
    viewModel: TeammatesViewModel,
    questionnaires: List<Questionnaire>,
    navigateToQuestionnaireDetails: () -> Unit,
    onRefresh: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { paddingValues ->

        val isRefreshing = remember { mutableStateOf(false) }
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { questionnaires.size + 1 })
        val isLoadingMore = remember { mutableStateOf(false) }

        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = isRefreshing.value,
            onRefresh = {
                isRefreshing.value = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        delay(1000L)
                        onRefresh()
                    } finally {
                        withContext(Dispatchers.Main) {
                            isRefreshing.value = false
                            pagerState.scrollToPage(0)
                        }
                    }
                }
            }
        ) {
            QuestionnairesVerticalPager(
                questionnaires = questionnaires,
                navigateToQuestionnaireDetails = navigateToQuestionnaireDetails,
                viewModel = viewModel,
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
                if (!isLoadingMore.value && pagerState.currentPage == questionnaires.size) {
                    isLoadingMore.value = true

                    val newPage =
                        if (questionnaires.size % 10 == 0) pagerState.currentPage / 10 + 1 else pagerState.currentPage / 10 + 2
                    try {
                        viewModel.loadQuestionnaires(
                            page = newPage
                        )
                    } finally {
                        isLoadingMore.value = false
                    }
                }
            }
        }
    }
}
