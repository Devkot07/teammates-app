package com.pezont.teammates.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.ValidationError
import com.pezont.teammates.domain.model.ValidationResult
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.usecase.LoadAuthorProfileUseCase
import com.pezont.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.PrepareImageForUploadUseCase
import com.pezont.teammates.domain.usecase.UpdateUserProfileUseCase
import com.pezont.teammates.state.StateManager
import com.pezont.teammates.ui.snackbar.SnackbarController
import com.pezont.teammates.ui.snackbar.SnackbarEvent
import com.pezont.teammates.utils.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject


//TODO crushing viewModel
@HiltViewModel
class TeammatesViewModel @Inject constructor(

    private val stateManager: StateManager,

    private val errorHandler: ErrorHandler,


    private val loadQuestionnairesUseCase: LoadQuestionnairesUseCase,

    private val loadAuthorProfileUseCase: LoadAuthorProfileUseCase,


    val updateUserProfileUseCase: UpdateUserProfileUseCase,

    val prepareImageForUploadUseCase: PrepareImageForUploadUseCase,

    ) : ViewModel() {

    val user = stateManager.user

    val contentState = stateManager.contentState


    val selectedAuthor = stateManager.selectedAuthor
    val selectedQuestionnaire = stateManager.selectedQuestionnaire
    val selectedAuthorQuestionnaires = stateManager.selectedAuthorQuestionnaires


    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent


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
                        _uiEvent.tryEmit(UiEvent.UserProfileUpdated)
                        onSuccess()
                    }.onFailure { throwable ->
                        stateManager.updateContentState(ContentState.ERROR)
                        errorHandler.handleError(throwable)
                    }
                }
            }
        }
    }

    fun loadAuthor(authorId: String) {
        if (selectedAuthor.value.publicId != authorId) {
            viewModelScope.launch {
                stateManager.updateContentState(ContentState.LOADING)
                stateManager.updateSelectedAuthor(User())
                loadAuthorProfileUseCase(authorId).onSuccess { author ->

                    stateManager.updateSelectedAuthor(author)

                    loadQuestionnairesUseCase(
                        game = null,
                        limit = 100,
                        authorId = author.publicId
                    ).onSuccess { authorQuestionnaires ->
                        Log.i(TAG, authorQuestionnaires.toString())

                        stateManager.updateSelectedAuthorQuestionnaires(authorQuestionnaires)
                        stateManager.updateContentState(ContentState.LOADED)

                    }.onFailure { throwable ->
                        Log.e(TAG, throwable.toString())

                        stateManager.updateContentState(ContentState.ERROR)
                        errorHandler.handleError(throwable)
                    }
                }.onFailure { throwable ->
                    stateManager.updateContentState(ContentState.ERROR)
                    errorHandler.handleError(throwable)
                }
            }
        }
    }

    fun updateSelectedQuestionnaire(questionnaire: Questionnaire) {
        stateManager.updateSelectedQuestionnaire(questionnaire)
    }


    companion object {
        const val TAG: String = "ViewModel"
    }
}

fun ValidationError.toMessageRes(): Int = when (this) {

    ValidationError.HEADER_TOO_SHORT -> R.string.the_header_must_contain_at_least_3_characters
    ValidationError.HEADER_TOO_LONG -> R.string.the_maximum_length_of_the_header_is_80_characters
    ValidationError.DESCRIPTION_TOO_LONG -> R.string.the_maximum_length_of_the_description_is_300_characters
    ValidationError.GAME_NOT_SELECTED -> R.string.please_select_a_game
    ValidationError.NICKNAME_TOO_SHORT -> R.string.the_nickname_must_contain_at_least_3_characters
    ValidationError.NICKNAME_TOO_LONG -> R.string.the_maximum_length_of_the_nickname_is_20_characters
}


sealed class UiEvent {
    data object UserProfileUpdated : UiEvent()
}