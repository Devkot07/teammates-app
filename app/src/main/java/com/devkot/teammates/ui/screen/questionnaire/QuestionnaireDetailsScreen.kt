package com.devkot.teammates.ui.screen.questionnaire

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.devkot.teammates.R
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.ui.components.Dots
import com.devkot.teammates.ui.components.DropdownItem
import com.devkot.teammates.ui.components.LoadingItemWithText
import com.devkot.teammates.ui.components.TeammatesDialog
import com.devkot.teammates.ui.components.TeammatesDropdownMenu
import com.devkot.teammates.ui.components.TeammatesTopAppBar
import com.devkot.teammates.ui.navigation.Destinations
import com.devkot.teammates.viewmodel.QuestionnairesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireDetailsScreen(
    author: User,
    questionnaire: Questionnaire,
    userQuestionnaires: List<Questionnaire>,
    likedQuestionnaires: List<Questionnaire>,
    questionnairesViewModel: QuestionnairesViewModel,
    navigateToAuthorProfile: () -> Unit,
    navigateToQuestionnaireEdit: () -> Unit,
    navigateUp: () -> Unit
) {

    val isRefreshing by questionnairesViewModel.isRefreshingSelectedQuestionnaire.collectAsState()
    val isLiked = likedQuestionnaires.any { it.questionnaireId == questionnaire.questionnaireId }
    Scaffold(
        topBar = {


            var questionnaireToDelete by remember { mutableStateOf(false) }
            val isUserQuestionnaire =
                userQuestionnaires.any { it.questionnaireId == questionnaire.questionnaireId }
            var showDropDownMenu by remember { mutableStateOf(false) }
            val items = listOf(
                DropdownItem(
                    text = stringResource(R.string.edit_questionnaire),
                    icon = Icons.Filled.ModeEditOutline,
                    onClick = {
                        navigateToQuestionnaireEdit()
                        showDropDownMenu = false
                    }
                ),
                DropdownItem(
                    text = stringResource(R.string.delete_questionnaire),
                    icon = Icons.Outlined.Delete,
                    onClick = {
                        questionnaireToDelete = true
                        showDropDownMenu = false
                    }
                ),
            )
            TeammatesTopAppBar(
                title = stringResource(Destinations.QuestionnaireDetails.titleRes),
                actions = {
                    if (isUserQuestionnaire) {
                        IconButton(onClick = { showDropDownMenu = !showDropDownMenu }) {
                            Icon(Dots, contentDescription = null)
                        }
                        TeammatesDropdownMenu(
                            expanded = showDropDownMenu,
                            items = items,
                            onDismissRequest = { showDropDownMenu = false },
                        )
                    }
                },
                canNavigateBack = true,
                navigateUp = {
                    questionnairesViewModel.resetSelectedQuestionnaireState()
                    navigateUp()
                },
            )
            

            if (questionnaireToDelete) {

                TeammatesDialog(
                    onCancel = { questionnaireToDelete = false }
                ) {
                    questionnairesViewModel.deleteQuestionnaire(questionnaire.questionnaireId)
                    questionnaireToDelete = false
                }
            }
        }


    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            isRefreshing = isRefreshing,
            onRefresh = { questionnairesViewModel.refreshSelectedQuestionnaire() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                when (questionnairesViewModel.selectedQuestionnaireState.collectAsState().value) {
                    ContentState.LOADED, ContentState.INITIAL ->

                        QuestionnaireDetailsItem(
                            authorNickname = author.nickname,
                            authorState = questionnairesViewModel.authorState.collectAsState().value,
                            questionnaire = questionnaire,
                            isLiked = isLiked,
                            likeAction = { questionnaire ->
                                if (isLiked) {
                                    questionnairesViewModel.unlikeQuestionnaire(questionnaire)
                                } else {
                                    questionnairesViewModel.likeQuestionnaire(questionnaire)
                                }
                            },
                            navigateToAuthorProfile = navigateToAuthorProfile,
                            modifier = Modifier
                        )


                    ContentState.LOADING, ContentState.ERROR -> LoadingItemWithText()
                }
            }

        }
    }
}
