package com.pezont.teammates.ui.screens.questionnaires

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pezont.teammates.R
import com.pezont.teammates.models.Games
import com.pezont.teammates.ui.navigation.NavigationDestination
import com.pezont.teammates.ui.theme.TeammatesTheme
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object QuestionnaireEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.entry_information
}


// TODO checking for filled fields
@Composable
fun QuestionnaireEntryScreen(
    modifier: Modifier = Modifier,
    createNewQuestionnaireAction: (
        header: String,
        description: String,
        selectedGame: Games,
        image: MultipartBody.Part?
    ) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    var header by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf<Games>(Games.CS2) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
    ) { innerPadding ->

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(text = "Select Image")
            }

            selectedImageUri?.let {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(selectedImageUri)
                        .placeholder(R.drawable.ic_loading_image)
                        .build(),
                    error = painterResource(R.drawable.ic_broken_image),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .padding(32.dp)
                        .aspectRatio(1f)
                        .clip(ShapeDefaults.Medium)
                        .widthIn(400.dp)
                        .border(2.dp, Color.Gray, ShapeDefaults.Medium),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = header,
                onValueChange = { header = it },
                label = { Text(stringResource(R.string.header)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Box(modifier = Modifier.wrapContentWidth()) {
                    OutlinedButton(
                        onClick = { isDropdownExpanded = true },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(text = selectedGame.name)
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Games.entries.forEach { game ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedGame = game
                                    isDropdownExpanded = false
                                },
                                text = { Text(text = game.name) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))


                val context = LocalContext.current

                OutlinedButton(
                    onClick = {
                        val imagePart = selectedImageUri?.asMultipart("image", context)
                        createNewQuestionnaireAction(
                            header,
                            description,
                            selectedGame,
                            imagePart
                        )
                    },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(text = stringResource(R.string.create))
                }
            }


            Spacer(modifier = Modifier.height(20.dp))


        }
    }
}


fun Uri.asMultipart(name: String, context: Context): MultipartBody.Part? {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(this) ?: return null
    val requestBody = inputStream.readBytes()
        .toRequestBody(contentResolver.getType(this)?.toMediaTypeOrNull())
    inputStream.close()
    return MultipartBody.Part.createFormData(name, this.lastPathSegment, requestBody)
}

@Preview
@Composable
fun PreviewQuestionnaireEntryScreen() {

    TeammatesTheme(darkTheme = true) {
        QuestionnaireEntryScreen(Modifier, { _, _, _, _ -> }, {})
    }

}