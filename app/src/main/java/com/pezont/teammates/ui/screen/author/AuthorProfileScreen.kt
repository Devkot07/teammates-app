package com.pezont.teammates.ui.screen.author

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R
import com.pezont.teammates.viewmodel.TeammatesViewModel
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.ui.items.AuthorProfile
import com.pezont.teammates.ui.components.LoadingItemWithText
import com.pezont.teammates.ui.navigation.NavigationDestination
import com.pezont.teammates.ui.screen.questionnaire.QuestionnairesHorizontalRow

object AuthorProfileDestination : NavigationDestination {
    override val route = "author_profile"
    override val titleRes = R.string.profile
}

@Composable
fun AuthorProfileScreen(
    viewModel: TeammatesViewModel,
    contentState: ContentState,
    author: User,
    authorQuestionnaires: List<Questionnaire>,
    starAction: () -> Unit,
    navigateToQuestionnaireDetails: () -> Unit,
    topBar: @Composable () -> Unit = {},
    modifier: Modifier,
) {
    Scaffold(
        topBar = topBar,
    ) { paddingValues ->
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (contentState == ContentState.LOADING)
                LoadingItemWithText()
            else {
                AuthorProfile(
                    starAction, author
                )
                Spacer(modifier.height(8.dp))
                QuestionnairesHorizontalRow(
                    authorQuestionnaires,
                    navigateToQuestionnaireDetails,
                    viewModel,
                    )
            }
        }
    }
}


