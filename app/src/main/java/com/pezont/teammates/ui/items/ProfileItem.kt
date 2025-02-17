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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pezont.teammates.R
import com.pezont.teammates.models.User
import com.pezont.teammates.ui.theme.TeammatesTheme

@Composable
fun TeammatesProfile(
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
            ProfileSection(
                nickname = nickname,
                email = email,
                image = painterResource(id = R.drawable.ic_launcher_foreground)
            )
//            Text(
//                text = "User Profile",
//                style = MaterialTheme.typography.headlineSmall,
//                color = MaterialTheme.colorScheme.primary
//            )

            Spacer(modifier = Modifier.height(16.dp))

//            Text(
//                text = "Nickname: $nickname",
//                style = MaterialTheme.typography.bodyMedium
//            )
            Text(
                text = "Description: $description",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Email: $email",
                style = MaterialTheme.typography.bodyMedium
            )






            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = logout) {
                Text(text = stringResource(id = R.string.logout))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = navigateToMyQuestionnaires

            ) {
                Text(text = "My questionnaires")
            }
        }
    }
}


@Composable
fun ProfileSection(
    nickname: String,
    email: String,

    modifier: Modifier = Modifier,
    image: Painter
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            RoundImage(
                image = image,
                modifier = Modifier
                    .size(150.dp)
                //.weight()
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
            //StatSection(modifier = Modifier.weight(7f))
        }

    }
}


@Composable
fun RoundImage(
    image: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape)
    )
}

@Composable
fun ProfileDescription(
    displayName: String,
    description: String,
    url: String,
    followedBy: List<String>,
    otherCount: Int
) {
    val letterSpacing = 0.5.sp
    val lineHeight = 20.sp
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Text(
            text = displayName,
            fontWeight = FontWeight.Bold,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
        Text(
            text = description,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
        Text(
            text = url,
            color = Color(0xFF00FFC2),
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
        if (followedBy.isNotEmpty()) {
            Text(
                text = buildAnnotatedString {
                    val boldStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    append("Followed by ")
                    followedBy.forEachIndexed { index, name ->
                        pushStyle(boldStyle)
                        append(name)
                        pop()
                        if (index < followedBy.size - 1) {
                            append(", ")
                        }
                    }
                    if (otherCount > 2) {
                        append(" and ")
                        pushStyle(boldStyle)
                        append("$otherCount others")
                    }
                },
                letterSpacing = letterSpacing,
                lineHeight = lineHeight
            )
        }
    }
}


@Preview
@Composable
fun ProfilePreview() {
    TeammatesTheme {
        TeammatesProfile(
            {}, {},
            User(
                "Bob",
                1,
                "bob@longcorp.com",
            ),
            PaddingValues()
        )
    }
}


