package com.devkot.teammates.ui.screen.questionnaire

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.ui.components.LoadingItemWithText
import com.devkot.teammates.ui.components.TeammatesButton
import com.devkot.teammates.viewmodel.AuthorViewModel
import com.devkot.teammates.viewmodel.QuestionnairesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserQuestionnairesScreen(
    authorViewModel: AuthorViewModel,
    questionnairesViewModel: QuestionnairesViewModel,
    userQuestionnaires: List<Questionnaire>,
    navigateToQuestionnaireDetails: () -> Unit,
    navigateToQuestionnaireCreate: () -> Unit,
    topBar: @Composable () -> Unit = {},

    ) {

    Scaffold(
        topBar = topBar
    ) { innerPadding ->

        val isRefreshing by questionnairesViewModel.isRefreshingUserQuestionnaires.collectAsState()
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { userQuestionnaires.size + 1 })

        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            isRefreshing = isRefreshing,
            onRefresh = { questionnairesViewModel.refreshUserQuestionnairesScreen() }
        ) {
            when (questionnairesViewModel.userQuestionnairesState.collectAsState().value) {
                ContentState.LOADED -> {
                    QuestionnairesVerticalPager(
                        questionnaires = userQuestionnaires,
                        pagerState = pagerState,
                        navigateToQuestionnaireDetails = navigateToQuestionnaireDetails,
                        authorViewModel = authorViewModel,
                        lastItem = { CreateButton(navigateToQuestionnaireCreate) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                ContentState.LOADING, ContentState.INITIAL, ContentState.ERROR -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingItemWithText()
                    }
                }
            }
        }
    }
}

@Composable
fun CreateButton(
    navigateToQuestionnaireEntry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TeammatesButton(
            onClick = navigateToQuestionnaireEntry,
            modifier = Modifier
                .size(80.dp),
            imageVector = Icons.Default.Add
        )
        Spacer(Modifier.height(10.dp))
        Text(text = ("Create new questionnaire"))
    }
}
