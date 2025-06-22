package com.pezont.teammates.ui.screens.questionnaires


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pezont.teammates.R
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.UiState
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.model.enums.Games
import com.pezont.teammates.domain.model.form.QuestionnaireForm
import com.pezont.teammates.ui.GamesDropdownMenu
import com.pezont.teammates.ui.buttons.TeammatesButton
import com.pezont.teammates.ui.items.LoadingItemWithText
import com.pezont.teammates.ui.navigation.NavigationDestination

object QuestionnaireCreateDestination : NavigationDestination {
    override val route = "item_create"
    override val titleRes = R.string.entry_information
}


@Composable
fun QuestionnaireCreateScreen(
    modifier: Modifier = Modifier,
    uiState: UiState,
    viewModel: TeammatesViewModel,
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


        if (uiState.contentState == ContentState.LOADING) {
            LoadingItemWithText()
        } else {
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
                    TeammatesButton(
                        text = "Select Image",
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        modifier = Modifier.wrapContentWidth()
                    )
                } else {
                    TeammatesButton(
                        text = "Delete Image",
                        onClick = {
                            selectedImageUri = null
                        },
                        modifier = Modifier.wrapContentWidth()
                    )
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

                    Spacer(modifier = Modifier.width(24.dp))
                    TeammatesButton(
                        enabled = (questionnaireForm.header.isNotEmpty() && questionnaireForm.description.isNotEmpty() && questionnaireForm.selectedGame != null),
                        onClick = {
                            val imagePart = selectedImageUri?.let {
                                viewModel.prepareImageForUploadUseCase(
                                    it,
                                    context
                                )
                            }
                            viewModel.createNewQuestionnaire(
                                questionnaireForm.header,
                                questionnaireForm.description,
                                questionnaireForm.selectedGame,
                                imagePart,
                                onSuccess = {
                                    selectedImageUri = null
                                    questionnaireForm = QuestionnaireForm("", "", null)
                                },
                                onError = {}

                            )
                        },
                        text = stringResource(R.string.create),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(56.dp)
                    )
                }
            }
        }
    }
}


