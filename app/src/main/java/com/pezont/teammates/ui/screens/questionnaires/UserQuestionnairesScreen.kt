package com.pezont.teammates.ui.screens.questionnaires

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.items.TeammatesLoadingItem
import com.pezont.teammates.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object UserQuestionnairesDestination : NavigationDestination {
    override val route = "user_questionnaires"
    override val titleRes = R.string.user_questionnaires
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserQuestionnairesScreen(
    viewModel: TeammatesViewModel,
    userQuestionnaires: List<Questionnaire>,
    onRefresh: () -> Unit,
    navigateToQuestionnaireDetails: () -> Unit,
    navigateToQuestionnaireCreate: () -> Unit,
    topBar: @Composable () -> Unit = {},

    ) {

    Scaffold(
        topBar = topBar
    ) { innerPadding ->

        val isRefreshing = remember { mutableStateOf(false) }
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { userQuestionnaires.size + 1 })

        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
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
                questionnaires = userQuestionnaires,
                pagerState = pagerState,
                navigateToQuestionnaireDetails = navigateToQuestionnaireDetails,
                viewModel = viewModel,
                lastItem = {
                    if (userQuestionnaires.isEmpty()) {
                        TeammatesLoadingItem()
                    } else {
                        CreateButton(navigateToQuestionnaireCreate)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
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
        Button(
            onClick = navigateToQuestionnaireEntry,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(text = ("Create new questionnaire"))
    }
}
