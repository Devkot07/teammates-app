package com.devkot.teammates.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devkot.teammates.BuildConfig
import com.devkot.teammates.R
import com.devkot.teammates.domain.model.ValidationResult
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.domain.state.StateManager
import com.devkot.teammates.domain.usecase.ImageUseCase
import com.devkot.teammates.domain.usecase.PrepareImageForUploadUseCase
import com.devkot.teammates.domain.usecase.UpdateUserProfileUseCase
import com.devkot.teammates.ui.snackbar.SnackbarController
import com.devkot.teammates.ui.snackbar.SnackbarEvent
import com.devkot.teammates.utils.ErrorHandler
import com.devkot.teammates.utils.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(

    private val stateManager: StateManager,

    private val errorHandler: ErrorHandler,

    val updateUserProfileUseCase: UpdateUserProfileUseCase,

    val prepareImageForUploadUseCase: PrepareImageForUploadUseCase,

    private val imageUseCase: ImageUseCase

    ) : ViewModel() {


    val user = stateManager.user

    val userProfileInfoState = stateManager.userProfileInfoState


    private val _userUiEvent = MutableSharedFlow<UserUiEvent>(extraBufferCapacity = 1)
    val userUiEvent: SharedFlow<UserUiEvent> = _userUiEvent


    fun updateUserProfile(
        nickname: String,
        description: String,
        uri: Uri?,
        context: Context,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            val validationResult =
                updateUserProfileUseCase.validateUserProfileForm(nickname, description)
            when (validationResult) {
                is ValidationResult.Error -> {
                    val messageRes = validationResult.errorCode.toMessageRes()
                    SnackbarController.sendEvent(SnackbarEvent(messageRes))
                }

                ValidationResult.Success -> {
                    stateManager.updateUserProfileInfoState(ContentState.LOADING)
                    updateUserProfileUseCase(
                        nickname = nickname,
                        description = description
                    ).onSuccess { user ->

                        val image = prepareImageForUploadUseCase(uri, context)
                        if (image != null) {
                            updateUserProfileUseCase.updateUserAvatar(image)
                                .onSuccess { result ->

                                    stateManager.updateUser(
                                        stateManager.user.value.copy(
                                            nickname = user.nickname,
                                            description = user.description,
                                            imagePath = result.imagePath
                                        )
                                    )
                                    val newUrl = result.imagePath.replace("http://localhost:8200","${BuildConfig.BASE_URL}${BuildConfig.PORT_3}${BuildConfig.END_URL}")
                                    imageUseCase.updateImageCache(newUrl)
                                    stateManager.updateUserProfileInfoState(ContentState.LOADED)
                                    SnackbarController.sendEvent(SnackbarEvent(R.string.photo_update))
                                }.onFailure { throwable ->
                                    stateManager.updateUserProfileInfoState(ContentState.ERROR)
                                    errorHandler.handleError(throwable)
                                    return@onFailure
                                }
                        } else {

                            stateManager.updateUser(
                                stateManager.user.value.copy(
                                    nickname = user.nickname,
                                    description = user.description,
                                    imagePath = user.imagePath
                                )
                            )
                            stateManager.updateUserProfileInfoState(ContentState.LOADED)

                            SnackbarController.sendEvent(SnackbarEvent(R.string.information_update_successfully))
                        }
                        _userUiEvent.tryEmit(UserUiEvent.UserProfileUpdated)
                        onSuccess()
                    }.onFailure { throwable ->
                        stateManager.updateUserProfileInfoState(ContentState.ERROR)
                        errorHandler.handleError(throwable)
                    }
                }
            }
        }
    }




}


sealed class UserUiEvent {
    data object UserProfileUpdated : UserUiEvent()
}