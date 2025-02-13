package com.pezont.teammates.ui.screens.myQuestionnaires

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R
import com.pezont.teammates.models.Games
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.TeammatesViewModel
import com.pezont.teammates.ui.navigation.NavigationDestination
import com.pezont.teammates.ui.screens.TeammatesTopAppBar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object QuestionnaireEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.entry_information
}


// TODO clear
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    createNewQuestionnaireAction: (header: String,
                                   description: String,
                                   selectedGame: Games,
                                   image: MultipartBody.Part?)  -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier
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
        topBar = {
            TeammatesTopAppBar(
                title = stringResource(QuestionnaireEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = header,
                onValueChange = { header = it },
                label = { Text(stringResource(R.string.header)) },
                modifier = Modifier.wrapContentWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.wrapContentWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(text = "Select Image")
            }

            selectedImageUri?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Selected Image: ${it.lastPathSegment}")
            }

            Spacer(modifier = Modifier.height(20.dp))

            val context = LocalContext.current

            Button(
                onClick = {

                    val imagePart = selectedImageUri?.asMultipart("image", context)
                    createNewQuestionnaireAction(
                        header,
                        description,
                        selectedGame,
                        imagePart
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.create))
            }


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
