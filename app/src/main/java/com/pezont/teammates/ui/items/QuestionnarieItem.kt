package com.pezont.teammates.ui.items

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.pezont.teammates.BuildConfig
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.theme.TeammatesTheme

@Composable
fun QuestionnaireItem(
    questionnaire: Questionnaire,
    navigateToQuestionnaireDetails: () -> Unit,
    modifier: Modifier,
) {

    Box(
        modifier = modifier
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        QuestionnaireCard(
            navigateToQuestionnaireDetails,
            questionnaire = questionnaire,
            modifier = Modifier
                .widthIn(max = 450.dp)
                .heightIn(max = 800.dp)
                .padding(top = 8.dp, start = 32.dp, end = 32.dp, bottom = 16.dp)
                .fillMaxSize()
        )
    }
}

@Composable
fun QuestionnaireCard(
    navigateToQuestionnaireDetails: () -> Unit,
    questionnaire: Questionnaire,
    modifier: Modifier = Modifier

) {
    val baseUrl = "${BuildConfig.BASE_URL}${BuildConfig.PORT_1}${BuildConfig.END_URL}"
    val fixedImagePath = questionnaire.imagePath.replace("http://localhost:8000", baseUrl)

    Card(
        modifier = modifier.clickable {
            navigateToQuestionnaireDetails()
        }

        ,
        shape = ShapeDefaults.Large,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Scaffold(
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(16.dp)
            ) {


                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Log.i("Image", fixedImagePath)
                    SubcomposeAsyncImage(
                        model = fixedImagePath,
                        loading = {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(100.dp)
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
                            .aspectRatio(1f)
                            .clip(ShapeDefaults.Medium)
                            .border(2.dp, Color.Gray, ShapeDefaults.Medium),
                        contentScale = ContentScale.Crop
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
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = questionnaire.game,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
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

}


@Preview
@Composable
fun QuestionnaireCardPreview() {
    TeammatesTheme(darkTheme = true) {
        QuestionnaireCard({}, Questionnaire("Header", "Game", "", "1", "1", "https"))
    }
}