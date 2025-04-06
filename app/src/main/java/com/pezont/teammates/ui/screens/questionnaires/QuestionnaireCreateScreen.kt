package com.pezont.teammates.ui.screens.questionnaires

import android.content.Context
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.Games
import com.pezont.teammates.domain.model.QuestionnaireForm
import com.pezont.teammates.ui.navigation.NavigationDestination
import com.pezont.teammates.ui.theme.TeammatesTheme
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object QuestionnaireCreateDestination : NavigationDestination {
    override val route = "item_create"
    override val titleRes = R.string.entry_information
}


@Composable
fun QuestionnaireCreateScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    createNewQuestionnaireAction: (
        header: String,
        description: String,
        selectedGame: Games,
        image: MultipartBody.Part?
    ) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var questionnaireForm by remember { mutableStateOf(QuestionnaireForm("", "", null)) }
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
            if (selectedImageUri == null) {
                OutlinedButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier.wrapContentWidth()
                ) { Text(text = "Select Image") }
            } else {
                OutlinedButton(
                    onClick = {
                        selectedImageUri = null
                    },
                    modifier = Modifier.wrapContentWidth()
                ) { Text(text = "Delete Image") }
            }


            selectedImageUri?.let {
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

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = questionnaireForm.header,
                onValueChange = { data ->
                    questionnaireForm = questionnaireForm.copy(header = data)
                },
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                label = { Text(stringResource(R.string.header)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = questionnaireForm.description,
                onValueChange = { data ->
                    questionnaireForm = questionnaireForm.copy(description = data)
                },
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.wrapContentWidth()) {
                    OutlinedButton(
                        onClick = { isDropdownExpanded = true },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = questionnaireForm.selectedGame?.nameOfGame
                                ?: stringResource(R.string.select_game)
                        )
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Games.entries.forEach { game ->
                            DropdownMenuItem(
                                onClick = {
                                    questionnaireForm.selectedGame = game
                                    isDropdownExpanded = false
                                },
                                text = { Text(text = game.nameOfGame) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedButton(
                    onClick = {
                        if (questionnaireForm.isNotEmpty()) {
                            when {
                                questionnaireForm.header.length > 100 -> {
                                    Toast.makeText(
                                        context,
                                        "Max value of header is 100",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                questionnaireForm.description.length > 500 -> {
                                    Toast.makeText(
                                        context,
                                        "Max value of description is 500",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                questionnaireForm.selectedGame == null -> {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.select_game),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    val imagePart = selectedImageUri?.asMultipart("image", context)
                                    navigateToHome()
                                    createNewQuestionnaireAction(
                                        questionnaireForm.header,
                                        questionnaireForm.description,
                                        questionnaireForm.selectedGame!!,
                                        imagePart
                                    )
                                }
                            }
                        }

                    },
                    modifier = Modifier.wrapContentWidth()
                ) { Text(text = stringResource(R.string.create)) }
            }
            Spacer(modifier = Modifier.height(20.dp))


        }
    }
}

// TODO extract
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
        QuestionnaireCreateScreen(Modifier, {}, { _, _, _, _ -> }, {})
    }

}