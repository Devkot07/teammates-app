package com.pezont.teammates.ui.screens.questionnaires

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pezont.teammates.R
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.items.TeammatesLoadingItem
import com.pezont.teammates.ui.items.TeammatesTextItem
import com.pezont.teammates.ui.navigation.NavigationDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object LikedQuestionnairesDestination : NavigationDestination {
    override val route = "favorite_questionnaires"
    override val titleRes = R.string.favorites
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedQuestionnairesScreen(
    viewModel: TeammatesViewModel,
    likedQuestionnaires: List<Questionnaire>,
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

        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                coroutineScope.launch {
                    viewModel.loadLikedQuestionnaires()
                    delay(300)
                    isRefreshing = false
                    pagerState.scrollToPage(0)
                }
            }
        ) {
            LaunchedEffect(pagerState.currentPage) {
                if (!isLoadingMore && pagerState.currentPage == likedQuestionnaires.size) {
                    isLoadingMore = true
                    viewModel.loadLikedQuestionnaires()
                    isLoadingMore = false
                }
            }

            QuestionnairesPager(
                questionnaires = likedQuestionnaires,
                pagerState = pagerState,
                lastItem = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isLoadingMore -> TeammatesLoadingItem()
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