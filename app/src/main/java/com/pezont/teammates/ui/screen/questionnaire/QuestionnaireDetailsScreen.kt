package com.pezont.teammates.ui.screen.questionnaire

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.enums.ContentState


@Composable
fun QuestionnaireDetailsScreen(
    author: User,
    contentState: ContentState,
    questionnaire: Questionnaire,
    navigateToAuthorProfile: () -> Unit,
    topBar: @Composable () -> Unit = {},

    ) {

    Scaffold(
        topBar = topBar
    ) { innerPadding ->
        val authorNickname = author.nickname?: ""
        QuestionnaireDetailsItem( authorNickname, contentState, questionnaire,navigateToAuthorProfile, Modifier.padding(innerPadding))

    }


}


