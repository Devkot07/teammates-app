package com.pezont.teammates.ui.screens.questionnaires

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.items.QuestionnaireItem


@Composable
fun QuestionnairesPager(
    pagerState: PagerState,
    questionnaires: List<Questionnaire>,
    navigateToQuestionnaireDetails: () -> Unit,
    viewModel: TeammatesViewModel,
    lastItem: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {


    VerticalPager(
        snapPosition = SnapPosition.Center,
        state = pagerState,
    ) { pageIndex ->
        if (pageIndex < questionnaires.size) {
            val questionnaire = questionnaires[pageIndex]
            QuestionnaireItem(
                navigateToQuestionnaireDetails = {
                    navigateToQuestionnaireDetails()
                    viewModel.updateSelectedQuestionnaire(questionnaire)
                    viewModel.loadAuthor(questionnaire.authorId)
                },
                questionnaire = questionnaire,
                modifier = modifier,
            )
        } else lastItem()
    }
}













