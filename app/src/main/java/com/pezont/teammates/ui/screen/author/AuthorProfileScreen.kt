package com.pezont.teammates.ui.screen.author

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.ui.components.LoadingItemWithText
import com.pezont.teammates.viewmodel.AuthorViewModel


@Composable
fun AuthorProfileScreen(
    author: User,
    authorQuestionnaires: List<Questionnaire>,
    likedAuthors: List<User>,
    authorViewModel: AuthorViewModel,
    navigateToQuestionnaireDetails: () -> Unit,
    topBar: @Composable () -> Unit = {},
    modifier: Modifier,
) {
    val isLiked = likedAuthors.any { it.publicId == author.publicId }
    Scaffold(
        topBar = topBar,
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (authorViewModel.authorState.collectAsState().value) {
                ContentState.LOADED, ContentState.INITIAL ->
                    AuthorProfileItem(
                        author = author,
                        authorQuestionnaires = authorQuestionnaires,
                        isLiked = isLiked,
                        updateSelectedQuestionnaire = authorViewModel::updateSelectedQuestionnaire,
                        navigateToQuestionnaireDetails = navigateToQuestionnaireDetails,
                        action = { user ->
                            if (isLiked) {
                                authorViewModel.unlikeAuthor(user)
                            } else {
                                authorViewModel.likeAuthor(user)
                            }
                        }
                    )

                ContentState.LOADING, ContentState.ERROR -> LoadingItemWithText()
            }
        }
    }
}


