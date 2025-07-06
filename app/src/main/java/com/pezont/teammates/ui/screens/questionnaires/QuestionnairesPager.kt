package com.pezont.teammates.ui.screens.questionnaires

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.items.QuestionnaireCompactItem
import com.pezont.teammates.ui.items.QuestionnaireItem


@Composable
fun QuestionnairesVerticalPager(
    pagerState: PagerState,
    questionnaires: List<Questionnaire>,
    navigateToQuestionnaireDetails: () -> Unit,
    viewModel: TeammatesViewModel,
    lastItem: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {


    VerticalPager(
        horizontalAlignment = Alignment.CenterHorizontally,
        snapPosition = SnapPosition.Center,
        pageSpacing = 18.dp,
        state = pagerState,
    ) { pageIndex ->
        if (pageIndex < questionnaires.size) {
            val questionnaire = questionnaires[pageIndex]
            QuestionnaireItem(
                navigateToQuestionnaireDetails = {
                    viewModel.updateSelectedQuestionnaire(questionnaire)
                    viewModel.loadAuthor(questionnaire.authorId)
                    navigateToQuestionnaireDetails()
                },
                questionnaire = questionnaire,
                modifier = modifier,
            )
        } else lastItem()
    }
}

@Composable
fun QuestionnairesHorizontalRow(
    questionnaires: List<Questionnaire>,
    navigateToQuestionnaireDetails: () -> Unit,
    viewModel: TeammatesViewModel,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val itemPadding = 18.dp
        val horizontalPadding = 16.dp
        val itemWidth = maxWidth - horizontalPadding * 2

        LazyRow(
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(itemPadding),
        ) {
            items(questionnaires.size) { index ->
                val questionnaire = questionnaires[index]
                QuestionnaireCompactItem(
                    navigateToQuestionnaireDetails = {
                        navigateToQuestionnaireDetails()
                        viewModel.updateSelectedQuestionnaire(questionnaire)
                    },
                    questionnaire = questionnaire,
                    modifier = Modifier
                        .width(itemWidth)
                )
            }
        }
    }
}













