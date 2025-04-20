package com.pezont.teammates.ui.items

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.ui.theme.TeammatesTheme

//TODO AuthorProfile
@Composable
fun AuthorProfile(
    starAction: () -> Unit,
    author: User,
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(250.dp)

            ) {
                SubcomposeAsyncImage(
                    model = author.imagePath,
                    loading = {
                        CircularProgressIndicator(Modifier.padding(16.dp))
                    },
                    error = {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(250.dp)
                        .aspectRatio(1f)
                        .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            if (author.description != null) {
                ProfileInfoRow(
                    icon = Icons.Outlined.Description,
                    value = author.description!!,
                    modifier = Modifier.padding(16.dp)
                )
            }

            OutlinedButton(
                onClick = starAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = ShapeDefaults.Small,
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.subscribe),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
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


@Composable
fun UserProfile(
    navigateToMyQuestionnaires: () -> Unit,
    logout: () -> Unit,
    user: User,
    paddingValues: PaddingValues
) {

    val nickname: String = user.nickname ?: ""
    val description: String = user.description ?: ""
    val email: String = user.email ?: ""

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
                image = painterResource(id = R.drawable.ic_launcher_foreground)
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
            OutlinedButton(onClick = logout) {
                Text(text = stringResource(id = R.string.logout))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = navigateToMyQuestionnaires

            ) {
                Text(text = "My questionnaires")
            }
        }
    }
}

@Composable
fun UserProfileSection(
    nickname: String,
    email: String,

    modifier: Modifier = Modifier,
    image: Painter
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .aspectRatio(1f)
                    .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                    .clip(CircleShape)
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
        UserProfile(
            {}, {},
            User(
                "Bob",
                "111-111-111-111",
                "bob@longcorp.com",
            ),
            PaddingValues()
        )
    }
}



