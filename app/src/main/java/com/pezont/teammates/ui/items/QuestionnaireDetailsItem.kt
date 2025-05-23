package com.pezont.teammates.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.SubcomposeAsyncImage
import com.pezont.teammates.BuildConfig
import com.pezont.teammates.UiState
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.domain.model.ContentState
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.ui.buttons.LikeButton

@Composable
fun QuestionnaireDetailsItem(
    viewModel: TeammatesViewModel,
    uiState: UiState,
    questionnaire: Questionnaire,
    navigateToAuthorProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {


    val authorNickname: String = uiState.selectedAuthor.nickname.toString()


    val baseUrl = "${BuildConfig.BASE_URL}${BuildConfig.PORT_1}${BuildConfig.END_URL}"
    val fixedImagePath = questionnaire.imagePath.replace("http://localhost:8000", baseUrl)

    var imageLoadingError by remember { mutableStateOf(false) }

    Scaffold(floatingActionButton = {
        LikeButton(
            modifier = modifier
                .wrapContentHeight()
                .background(Color.Transparent)
        )
    }) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.contentState != ContentState.LOADED) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 50.dp, end = 50.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(100.dp)
                    )
                }

            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (questionnaire.imagePath.isNotEmpty() && !imageLoadingError) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            SubcomposeAsyncImage(
                                model = fixedImagePath,
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.padding(100.dp)
                                    )
                                },
                                error = {
                                    imageLoadingError = true
                                },
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent, Color.Black.copy(alpha = 0.6f)
                                            )
                                        )
                                    )
                                    .align(Alignment.BottomCenter)
                            )

                            Row {
                                Text(
                                    text = questionnaire.game,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .zIndex(1f)
                                )
                                Spacer(Modifier.weight(1f))
                                AuthorNickname(authorNickname, navigateToAuthorProfile)
                            }
                        }
                    } else {
                        Row {
                            Text(
                                text = questionnaire.game,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .zIndex(1f)
                            )
                            Spacer(Modifier.weight(1f))
                            AuthorNickname(authorNickname, navigateToAuthorProfile)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = questionnaire.header,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = questionnaire.description,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

        }
    }
}

@Composable
fun AuthorNickname(authorNickname: String, action: () -> Unit) {
    Text(
        text = authorNickname,
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
        modifier = Modifier
            .padding(16.dp)
            .zIndex(1f)
            .clickable{
                action()
            }
    )
}