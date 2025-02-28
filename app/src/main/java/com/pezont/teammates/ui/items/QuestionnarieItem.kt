package com.pezont.teammates.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.pezont.teammates.R
import com.pezont.teammates.models.Questionnaire
import com.pezont.teammates.ui.buttons.LikeButton
import com.pezont.teammates.ui.theme.TeammatesTheme

@Composable
fun QuestionnaireItem(
    questionnaire: Questionnaire,
    modifier: Modifier,
) {

    Box(
        modifier = modifier
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        QuestionnaireCard(
            questionnaire = questionnaire,
            modifier = Modifier
                .widthIn(max = 450.dp)
                .heightIn(max = 800.dp)
                .padding(top = 16.dp, start = 32.dp, end = 32.dp, bottom = 24.dp)
                .fillMaxSize()
        )
    }
}

@Composable
fun QuestionnaireCard(
    questionnaire: Questionnaire,
    modifier: Modifier = Modifier

) {
    val baseUrl = "https://potential-robot-4jg4wjjqp5vv2qx7w-8000.app.github.dev"
    val fixedImagePath = questionnaire.imagePath.replace("http:localhost:8000", baseUrl)
    Card(
        modifier = modifier,
        shape = ShapeDefaults.Large,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .padding()
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    SubcomposeAsyncImage(
                        model = fixedImagePath,
                        loading = {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(60.dp)
                            )
                        },
                        error = {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error"
                            )
                        },
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(ShapeDefaults.Medium)
                            .border(2.dp, Color.Gray, ShapeDefaults.Medium),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = questionnaire.header,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = questionnaire.game.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
            Text(
                text = questionnaire.imagePath,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            )
            HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
            Text(
                text = questionnaire.description,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            )
            HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                ) {
                LikeButton(modifier = Modifier.wrapContentHeight())
                Spacer(modifier = Modifier.weight(1f))
                LikeButton(modifier = Modifier.wrapContentHeight())
            }
        }
    }
}


@Preview
@Composable
fun QuestionnaireCardPreview() {
    TeammatesTheme (darkTheme = true) {
        QuestionnaireCard(Questionnaire("Hehe", "Game", "description", "1","1"))
    }
}