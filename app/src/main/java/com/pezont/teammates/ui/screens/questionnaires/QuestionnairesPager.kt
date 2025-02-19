package com.pezont.teammates.ui.screens.questionnaires

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pezont.teammates.models.Questionnaire
import com.pezont.teammates.ui.items.QuestionnaireItem


@Composable
fun QuestionnairesPager(
    pagerState: PagerState,
    questionnaires: List<Questionnaire>,
    modifier: Modifier = Modifier,
) {


    VerticalPager(
        snapPosition = SnapPosition.Center,
        state = pagerState,
    ) { pageIndex ->
        if (pageIndex < questionnaires.size) {
            val questionnaire = questionnaires[pageIndex]
            QuestionnaireItem(
                questionnaire = questionnaire,
                modifier = modifier,
            )
        } else {

            //TODO last item
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(100.dp)
                )
            }
        }
    }
}













