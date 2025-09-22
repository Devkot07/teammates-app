package com.devkot.teammates.ui.screen.questionnaire


import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.devkot.teammates.BuildConfig
import com.devkot.teammates.R
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.enums.Games
import com.devkot.teammates.domain.model.form.QuestionnaireForm
import com.devkot.teammates.ui.components.GamesDropdownMenu
import com.devkot.teammates.ui.components.LoadingItem
import com.devkot.teammates.ui.components.TeammatesButton
import com.devkot.teammates.ui.components.TeammatesDialog
import com.devkot.teammates.ui.components.TeammatesImage
import com.devkot.teammates.ui.components.TeammatesTopAppBar
import com.devkot.teammates.ui.navigation.Destinations


@Composable
fun QuestionnaireEditItem(
    questionnaire: Questionnaire,
    navigateUp: () -> Unit,
    onSave: (String, String, Games?, Uri?) -> Unit,
    modifier: Modifier = Modifier,
) {


    var questionnaireToSave by remember { mutableStateOf(false) }


    val focusManager = LocalFocusManager.current

    var questionnaireForm by remember {
        mutableStateOf(
            QuestionnaireForm(
                questionnaire.header,
                questionnaire.description,
                Games.entries.find { it.nameOfGame == questionnaire.game }
            )
        )
    }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri: Uri? ->
                selectedImageUri = uri
            }
        )


    val model: Any =
        if (selectedImageUri != null) ImageRequest.Builder(LocalContext.current)
            .data(selectedImageUri).build()
        else questionnaire.imagePath.replace(
            "http://localhost:8000",
            "${BuildConfig.BASE_URL}${BuildConfig.PORT_1}${BuildConfig.END_URL}/questionnaire"
        )


    Scaffold(
        topBar = {
            TeammatesTopAppBar(
                title = stringResource(Destinations.QuestionnaireEdit.titleRes),
                canNavigateBack = true,
                navigateUp = navigateUp,
                actions = {
                    IconButton(
                        onClick = {

                            questionnaireToSave = true



                        }
                    ) { Icon(Icons.Filled.Check, null) }
                }
            )

            if (questionnaireToSave) {

                TeammatesDialog(
                    onCancel = { questionnaireToSave = false }
                ) {
                    onSave(
                        questionnaireForm.header,
                        questionnaireForm.description,
                        questionnaireForm.selectedGame,
                        selectedImageUri
                    )
                    questionnaireToSave = false
                }
            }
        }
    ) { paddingValues ->

        Box(Modifier.padding(paddingValues)) {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {

                Spacer(modifier = Modifier.height(10.dp))


                TeammatesImage(
                    model = model,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(32.dp)
                        .aspectRatio(1f)
                        .clip(ShapeDefaults.Medium)
                        .widthIn(400.dp)
                        .border(2.dp, Color.Gray, ShapeDefaults.Medium)
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    loading = { LoadingItem() },
                    error = {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Error",
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxSize(),
                        )
                    },
                    contentScale = ContentScale.Crop
                )

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
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.Small
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.Small
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)

                    ) {
                        val buttonText = if (isDropdownExpanded) {
                            stringResource(R.string.close)
                        } else {
                            questionnaireForm.selectedGame?.nameOfGame
                                ?: stringResource(R.string.select_game)
                        }

                        TeammatesButton(
                            onClick = { isDropdownExpanded = !isDropdownExpanded },
                            text = buttonText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )

                        Spacer(Modifier.height(16.dp))


                        GamesDropdownMenu(
                            expanded = isDropdownExpanded,
                            games = Games.entries,
                            onGameSelected = { selected ->
                                isDropdownExpanded = false
                                questionnaireForm.selectedGame = selected
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.TopStart)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


