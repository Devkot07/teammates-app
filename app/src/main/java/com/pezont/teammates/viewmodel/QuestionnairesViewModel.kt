package com.pezont.teammates.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.ValidationResult
import com.pezont.teammates.domain.model.enums.AuthState
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.model.enums.Games
import com.pezont.teammates.domain.usecase.CreateQuestionnaireUseCase
import com.pezont.teammates.domain.usecase.LoadLikedQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadUserQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.PrepareImageForUploadUseCase
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
class QuestionnairesViewModel @Inject constructor(

    private val stateManager: StateManager,
    private val errorHandler: ErrorHandler,

    private val loadQuestionnairesUseCase: LoadQuestionnairesUseCase,
    private val loadUserQuestionnairesUseCase: LoadUserQuestionnairesUseCase,
    private val loadLikedQuestionnairesUseCase: LoadLikedQuestionnairesUseCase,

    val createNewQuestionnaireUseCase: CreateQuestionnaireUseCase,
    val prepareImageForUploadUseCase: PrepareImageForUploadUseCase,


    ) : ViewModel() {

    val questionnaires = stateManager.questionnaires
    val likedQuestionnaires = stateManager.likedQuestionnaires
    val userQuestionnaires = stateManager.userQuestionnaires


    private val _questionnaireUiEvent =
        MutableSharedFlow<QuestionnaireUiEvent>(extraBufferCapacity = 1)
    val questionnaireUiEvent: SharedFlow<QuestionnaireUiEvent> = _questionnaireUiEvent

    init {
        viewModelScope.launch {
            if (stateManager.authState.value == AuthState.AUTHENTICATED) loadLikedQuestionnaires()
        }
    }


    fun loadQuestionnaires(game: Games? = null, page: Int = 1) {
        viewModelScope.launch {
            loadQuestionnairesUseCase(
                page = page, game = game, authorId = null
            ).onSuccess { result ->
                Log.i(TAG, result.toString())
                if (page == 1) {
                    stateManager.updateQuestionnaires(result)
                } else {
                    stateManager.updateQuestionnaires(questionnaires.value + result)
                }
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                stateManager.updateContentState(ContentState.ERROR)
                errorHandler.handleError(throwable)
            }
        }
    }

    suspend fun loadLikedQuestionnaires() {
        loadLikedQuestionnairesUseCase().onSuccess { result ->
            Log.i(TAG, result.toString())
            stateManager.updateLikedQuestionnaires(result)
        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
            stateManager.updateContentState(ContentState.ERROR)
            errorHandler.handleError(throwable)
        }
    }

    fun loadUserQuestionnaires() {
        viewModelScope.launch {
            stateManager.updateContentState(ContentState.LOADING)
            loadUserQuestionnairesUseCase(game = null).onSuccess { result ->
                Log.i(TAG, result.toString())
                stateManager.updateUserQuestionnaires(result)
                stateManager.updateContentState(ContentState.LOADED)

            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                stateManager.updateContentState(ContentState.ERROR)
                errorHandler.handleError(throwable)
            }
        }
    }

    fun createNewQuestionnaire(
        header: String,
        description: String,
        selectedGame: Games?,
        image: MultipartBody.Part?,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            val validationResult = createNewQuestionnaireUseCase.validateQuestionnaireForm(
                header,
                description,
                selectedGame
            )

            when (validationResult) {
                is ValidationResult.Error -> {
                    val messageRes = validationResult.errorCode.toMessageRes()
                    SnackbarController.sendEvent(SnackbarEvent(messageRes))
                    onError()
                }

                ValidationResult.Success -> {
                    stateManager.updateContentState(ContentState.LOADING)
                    createNewQuestionnaireUseCase(
                        header = header,
                        selectedGame = selectedGame!!,
                        description = description,
                        image = image
                    ).onSuccess {
                        stateManager.updateContentState(ContentState.LOADED)
                        SnackbarController.sendEvent(SnackbarEvent(R.string.questionnaire_created_successfully))
                        _questionnaireUiEvent.tryEmit(QuestionnaireUiEvent.QuestionnaireCreated)
                        onSuccess()
                    }.onFailure { throwable ->
                        stateManager.updateContentState(ContentState.ERROR)
                        errorHandler.handleError(throwable)
                        onError()
                    }
                }
            }
        }
    }

    companion object { const val TAG  = "QVM" }
}


sealed class QuestionnaireUiEvent {
    data object QuestionnaireCreated : QuestionnaireUiEvent()
}




