package com.pezont.teammates.ui.screen.user


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
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
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.form.UserProfileForm
import com.pezont.teammates.ui.components.LoadingItem
import com.pezont.teammates.ui.components.TeammatesTopAppBar
import com.pezont.teammates.ui.navigation.Destinations
import com.pezont.teammates.viewmodel.UserViewModel


@Composable
fun UserProfileEditItem(
    user: User,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var userProfileForm by remember {
        mutableStateOf(
            UserProfileForm(
                user.nickname ?: "",
                user.email ?: "",
                user.description ?: ""
            )
        )
    }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )


    val model: Any? =
        if (selectedImageUri != null) ImageRequest.Builder(LocalContext.current)
            .data(selectedImageUri).placeholder(R.drawable.ic_loading_image).build()
        else user.imagePath


    Scaffold(
        topBar = {
            TeammatesTopAppBar(
                title = stringResource(Destinations.UserProfileEdit.titleRes),
                canNavigateBack = true,
                navigateUp = navigateUp,
                actions = {
                    IconButton(
                        onClick = {
                            userViewModel.updateUserProfile(
                                userProfileForm.nickname,
                                userProfileForm.description,
                                selectedImageUri,
                                context = context
                            ) {
                                selectedImageUri = null
                                userProfileForm = UserProfileForm(
                                    user.nickname ?: "",
                                    user.email ?: "",
                                    user.description ?: ""
                                )
                            }
                        }
                    ) { Icon(Icons.Filled.Check, null) }
                }
            )
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


                SubcomposeAsyncImage(
                    model = model,
                    loading = { LoadingItem() },
                    error = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Error",
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxSize(),
                        )
                    },
                    modifier = Modifier
                        .size(250.dp)
                        .aspectRatio(1f)
                        .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                        .clip(CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = userProfileForm.nickname,
                    onValueChange = { data ->
                        userProfileForm = userProfileForm.copy(nickname = data)
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
                    value = userProfileForm.description,
                    onValueChange = { data ->
                        userProfileForm = userProfileForm.copy(description = data)
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

            }
        }
    }

}




