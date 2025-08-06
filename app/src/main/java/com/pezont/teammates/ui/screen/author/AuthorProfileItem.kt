package com.pezont.teammates.ui.screen.author

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pezont.teammates.BuildConfig
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.ui.components.Minus
import com.pezont.teammates.ui.components.TeammatesButton
import com.pezont.teammates.ui.components.TeammatesImage
import com.pezont.teammates.ui.screen.questionnaire.QuestionnairesHorizontalRow
import com.pezont.teammates.ui.theme.TeammatesTheme

@Composable
fun AuthorProfileItem(
    author: User,
    authorQuestionnaires: List<Questionnaire>,
    isLiked: Boolean,
    updateSelectedQuestionnaire: (Questionnaire) -> Unit,
    navigateToQuestionnaireDetails: () -> Unit,
    action: (User) -> Unit,
) {

    val baseUrl = "${BuildConfig.BASE_URL}${BuildConfig.PORT_3}${BuildConfig.END_URL}"
    val fixedImagePath = author.imagePath.replace("http://localhost:8200", baseUrl)

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(250.dp)

            ) {
                TeammatesImage(
                    model = fixedImagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .size(250.dp)
                        .aspectRatio(1f)
                        .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                        .clip(CircleShape),
                    loading = {
                        CircularProgressIndicator(Modifier.padding(16.dp))
                    },
                    contentScale = ContentScale.Crop
                )
            }

            if (author.description != "") {
                ProfileInfoRow(
                    icon = Icons.Outlined.Description,
                    value = author.description,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (!isLiked) {
                TeammatesButton(
                    onClick = { action(author) },
                    text = stringResource(R.string.subscribe),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    imageVector = Icons.Filled.Add
                )
            } else {
                TeammatesButton(
                    onClick = { action(author) },
                    text = stringResource(R.string.unsubscribe),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    imageVector = Minus
                )
            }


            Spacer(Modifier.height(8.dp))
            QuestionnairesHorizontalRow(
                authorQuestionnaires,
                navigateToQuestionnaireDetails,
                updateSelectedQuestionnaire,
            )


        }
    }
}


@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Preview
@Composable
fun AuthorProfilePreview() {
    TeammatesTheme(darkTheme = true) {
        AuthorProfileItem(
            User(),
            listOf(Questionnaire()),
            false,
            {},
            {},
            {}
        )
    }
}
