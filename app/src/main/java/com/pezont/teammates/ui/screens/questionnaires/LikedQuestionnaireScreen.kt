package com.pezont.teammates.ui.screens.questionnaires

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object LikedQuestionnairesDestination : NavigationDestination {
    override val route = "favorite_questionnaires"
    override val titleRes = R.string.favorites
}

//TODO create tryGetLikedQuestionnaires
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedQuestionnairesScreen(
    likedQuestionnaires: List<Questionnaire>,
    onRefresh: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
    ) { paddingValues ->

        val isRefreshing = remember { mutableStateOf(false) }
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { likedQuestionnaires.size + 1 })
        val isLoadingMore = remember { mutableStateOf(false) }

        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = isRefreshing.value,
            onRefresh = {
                isRefreshing.value = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        delay(1000L)
                        // viewModel.tryGetLikedQuestionnaires()
                    } finally {
                        withContext(Dispatchers.Main) {
                            isRefreshing.value = false
                            pagerState.scrollToPage(0)
                        }
                    }
                }
            }
        ) {
            QuestionnairesPager(
                questionnaires = likedQuestionnaires,
                pagerState = pagerState,
                lastItem = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(100.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )



            LaunchedEffect(pagerState.currentPage) {
                if (!isLoadingMore.value && pagerState.currentPage == likedQuestionnaires.size) {
                    isLoadingMore.value = true

                    val newPage =
                        if (likedQuestionnaires.size % 10 == 0) pagerState.currentPage / 10 + 1 else pagerState.currentPage / 10 + 2
                    try {
                        //viewModel.tryGetLikedQuestionnaires()
                    } finally {
                        Log.i("LOGIC", "Loading more items")
                        isLoadingMore.value = false
                    }
                }
            }
        }
    }
}

