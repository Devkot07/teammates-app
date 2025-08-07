package com.devkot.teammates.ui.screen.user

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devkot.teammates.BuildConfig
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.ui.components.LoadingItem
import com.devkot.teammates.ui.components.TeammatesButton
import com.devkot.teammates.ui.components.TeammatesImage
import com.devkot.teammates.ui.theme.TeammatesTheme


@Composable
fun UserProfileItem(
    user: User,
    navigateToUserQuestionnaires: () -> Unit,
    paddingValues: PaddingValues
) {

    val nickname: String = user.nickname
    val description: String = user.description
    val email: String = user.email

    val baseUrl = "${BuildConfig.BASE_URL}${BuildConfig.PORT_3}${BuildConfig.END_URL}"
    val fixedImagePath = user.imagePath.replace("http://localhost:8200", baseUrl)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),


        ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top,

            ) {
            Spacer(modifier = Modifier.height(10.dp))
            UserProfileSection(
                nickname = nickname,
                email = email,
                imagePath = fixedImagePath
            )


            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Description: $description",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Email: $email",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            TeammatesButton(onClick = navigateToUserQuestionnaires, text = "My questionnaires")
        }
    }
}

@Composable
fun UserProfileSection(
    nickname: String,
    email: String,
    imagePath: String,

    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TeammatesImage(
                model = imagePath,

                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .aspectRatio(1f)
                    .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                    .clip(CircleShape),
                loading = { LoadingItem() },
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = nickname,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }
    }
}


@Preview
@Composable
fun UserProfilePreview() {
    TeammatesTheme {
        UserProfileItem(
            User(
                "Bob",
                "111-111-111-111",
                "bob@longcorp.com",
            ),
            {},
            PaddingValues()
        )
    }
}



