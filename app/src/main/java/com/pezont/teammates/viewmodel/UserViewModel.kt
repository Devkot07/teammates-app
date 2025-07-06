package com.pezont.teammates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.ValidationResult
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.usecase.PrepareImageForUploadUseCase
import com.pezont.teammates.domain.usecase.UpdateUserProfileUseCase
import com.pezont.teammates.state.StateManager
import com.pezont.teammates.ui.snackbar.SnackbarController
import com.pezont.teammates.ui.snackbar.SnackbarEvent
import com.pezont.teammates.utils.ErrorHandler
import com.pezont.teammates.utils.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(

    private val stateManager: StateManager,

    private val errorHandler: ErrorHandler,

    val updateUserProfileUseCase: UpdateUserProfileUseCase,

    val prepareImageForUploadUseCase: PrepareImageForUploadUseCase,


    ) : ViewModel() {


    val user = stateManager.user

    val contentState = stateManager.contentState


    private val _userUiEvent = MutableSharedFlow<UserUiEvent>(extraBufferCapacity = 1)
    val userUiEvent: SharedFlow<UserUiEvent> = _userUiEvent


    fun updateUserProfile(
        nickname: String,
        description: String,
        image: MultipartBody.Part?,
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
                    stateManager.updateContentState(ContentState.LOADING)
                    updateUserProfileUseCase(
                        nickname = nickname,
                        description = description
                    ).onSuccess { user ->
                        if (image != null) {
                            updateUserProfileUseCase.updateUserAvatar(image)
                                .onSuccess { newImagePath ->

                                    stateManager.updateUser(
                                        stateManager.user.value.copy(
                                            nickname = user.nickname,
                                            description = user.description,
                                            imagePath = newImagePath.imagePath
                                        )
                                    )
                                    stateManager.updateContentState(ContentState.LOADED)
                                    SnackbarController.sendEvent(SnackbarEvent(R.string.photo_update))
                                }.onFailure { throwable ->
                                    stateManager.updateContentState(ContentState.ERROR)
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
                            stateManager.updateContentState(ContentState.LOADED)

                            SnackbarController.sendEvent(SnackbarEvent(R.string.information_update_successfully))
                        }
                        _userUiEvent.tryEmit(UserUiEvent.UserProfileUpdated)
                        onSuccess()
                    }.onFailure { throwable ->
                        stateManager.updateContentState(ContentState.ERROR)
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