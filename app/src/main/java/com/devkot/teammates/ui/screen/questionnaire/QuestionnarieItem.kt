package com.devkot.teammates.ui.screen.questionnaire

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devkot.teammates.BuildConfig
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.ui.components.LoadingItem
import com.devkot.teammates.ui.components.TeammatesImage
import com.devkot.teammates.ui.theme.TeammatesTheme

@Composable
fun QuestionnaireItem(
    questionnaire: Questionnaire,
    navigateToQuestionnaireDetails: () -> Unit,
    modifier: Modifier,
) {

    Box(
        modifier = modifier ,
        contentAlignment = Alignment.Center,

        ) {
        QuestionnaireCard(
            navigateToQuestionnaireDetails,
            questionnaire = questionnaire,
            modifier = Modifier
                .wrapContentSize()
                .widthIn(max = 450.dp)
                .heightIn(max = 800.dp)
                .padding(top = 8.dp, start = 32.dp, end = 32.dp, bottom = 16.dp)
        )
    }
}

@Composable
fun QuestionnaireCard(
    navigateToQuestionnaireDetails: () -> Unit,
    questionnaire: Questionnaire,
    modifier: Modifier = Modifier

) {
    val baseUrl = "${BuildConfig.BASE_URL}${BuildConfig.PORT_1}${BuildConfig.END_URL}/questionnaire"
    val fixedImagePath = questionnaire.imagePath.replace("http://localhost:8000", baseUrl)

    Card(
        modifier = modifier
            .wrapContentHeight()
            .clickable {
                navigateToQuestionnaireDetails()
            },
        shape = ShapeDefaults.ExtraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .padding(16.dp)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TeammatesImage(
                    model = fixedImagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(ShapeDefaults.Small)
                        .border(1.dp, Color.Gray, ShapeDefaults.Small),
                    loading = { LoadingItem() },
                    contentScale = ContentScale.Crop,
                )

            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {


                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Top,
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = questionnaire.header,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = questionnaire.game,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
            }

        }
    }
}



@Composable
fun QuestionnaireCompactItem(
    questionnaire: Questionnaire,
    navigateToQuestionnaireDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseUrl = "${BuildConfig.BASE_URL}${BuildConfig.PORT_1}${BuildConfig.END_URL}/questionnaire"
    val fixedImagePath = questionnaire.imagePath.replace("http://localhost:8000", baseUrl)

    Card(
        modifier = modifier
            .height(200.dp)
            .clickable { navigateToQuestionnaireDetails() },
        shape = ShapeDefaults.Large,
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .padding(8.dp)
        ) {

            TeammatesImage(
                model = fixedImagePath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(ShapeDefaults.Small)
                    .border(1.dp, Color.Gray, ShapeDefaults.Small),
                loading = { LoadingItem() },
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = questionnaire.header,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = questionnaire.game,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,

                )
            }
        }
    }
}

@Preview
@Composable
fun QuestionnaireCompactItemPreview() {
    TeammatesTheme(darkTheme = true) {
        QuestionnaireCompactItem(
            questionnaire = Questionnaire("Some f\nme f\nme f\n f f f \n f f \nheader", "Cool Game", "", "1", "1", "https"),
            navigateToQuestionnaireDetails = {}
        )
    }
}





@Preview
@Composable
fun QuestionnaireCardPreview() {
    TeammatesTheme(darkTheme = true) {
        QuestionnaireCard({}, Questionnaire("Header", "Game", "", "1", "1", "https"))
    }
}