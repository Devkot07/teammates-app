package com.pezont.teammates.ui.screens.questionnaires

import android.util.Log
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import com.pezont.teammates.R
import com.pezont.teammates.ui.TeammatesTopAppBar
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MyQuestionnairesDestination : NavigationDestination {
    override val route = "my_questionnaires"
    override val titleRes = R.string.my_questionnaires
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyQuestionnairesScreen(
    onNavigateUp: () -> Unit,
    navigateToQuestionnaireEntry: () -> Unit,
    getUserQuestionnaires: (teammatesUiState: TeammatesUiState.Home, ) -> Unit,

    teammatesUiState: TeammatesUiState.Home,
) {

    Scaffold(
        topBar = {
            TeammatesTopAppBar(
                title = stringResource(MyQuestionnairesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToQuestionnaireEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
    ) { innerPadding ->

        val isRefreshing = remember { mutableStateOf(false) }
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { teammatesUiState.userQuestionnaires.size + 1 })
        val isLoadingMore = remember { mutableStateOf(false) }

        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            isRefreshing = isRefreshing.value,
            onRefresh = {
                isRefreshing.value = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        delay(1000L)
                        getUserQuestionnaires(teammatesUiState)
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
                questionnaires = teammatesUiState.userQuestionnaires,
                pagerState = pagerState,
                modifier = Modifier.fillMaxSize()
            )



            LaunchedEffect(pagerState.currentPage) {
                if (!isLoadingMore.value && pagerState.currentPage == teammatesUiState.userQuestionnaires.size) {
                    isLoadingMore.value = true

                    val newPage =  if (teammatesUiState.questionnaires.size % 10 == 0) pagerState.currentPage / 10 + 1 else pagerState.currentPage / 10 + 2
                    try {
                        getUserQuestionnaires(teammatesUiState)



                    } finally {
                        Log.i("LOGIC", "Loading more items")
                        isLoadingMore.value = false
                    }
                }
            }
        }

    }


}
