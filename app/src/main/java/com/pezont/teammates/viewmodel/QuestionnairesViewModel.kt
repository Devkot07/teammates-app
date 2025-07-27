package com.pezont.teammates.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.ValidationResult
import com.pezont.teammates.domain.model.enums.AuthState
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.model.enums.Games
import com.pezont.teammates.domain.state.StateManager
import com.pezont.teammates.domain.usecase.CreateQuestionnaireUseCase
import com.pezont.teammates.domain.usecase.LikeQuestionnaireUseCase
import com.pezont.teammates.domain.usecase.LoadLikedQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadUserQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.PrepareImageForUploadUseCase
import com.pezont.teammates.domain.usecase.UnlikeQuestionnaireUseCase
import com.pezont.teammates.ui.snackbar.SnackbarController
import com.pezont.teammates.ui.snackbar.SnackbarEvent
import com.pezont.teammates.utils.ErrorHandler
import com.pezont.teammates.utils.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionnairesViewModel @Inject constructor(

    private val stateManager: StateManager,
    private val errorHandler: ErrorHandler,

    private val loadQuestionnairesUseCase: LoadQuestionnairesUseCase,
    private val loadUserQuestionnairesUseCase: LoadUserQuestionnairesUseCase,

    private val loadLikedQuestionnairesUseCase: LoadLikedQuestionnairesUseCase,
    private val likeQuestionnaireUseCase: LikeQuestionnaireUseCase,
    private val unlikeQuestionnaireUseCase: UnlikeQuestionnaireUseCase,

    val createNewQuestionnaireUseCase: CreateQuestionnaireUseCase,
    val prepareImageForUploadUseCase: PrepareImageForUploadUseCase,


    ) : ViewModel() {

    val questionnaires = stateManager.questionnaires
    val likedQuestionnaires = stateManager.likedQuestionnaires
    val userQuestionnaires = stateManager.userQuestionnaires


    private val _questionnaireUiEvent =
        MutableSharedFlow<QuestionnaireUiEvent>(extraBufferCapacity = 1)
    val questionnaireUiEvent: SharedFlow<QuestionnaireUiEvent> = _questionnaireUiEvent


    private val _isRefreshingQuestionnaires = MutableStateFlow(false)
    val isRefreshingQuestionnaires: StateFlow<Boolean> = _isRefreshingQuestionnaires.asStateFlow()

    private val _isRefreshingLikedQuestionnaires = MutableStateFlow(false)
    val isRefreshingLikedQuestionnaires: StateFlow<Boolean> = _isRefreshingLikedQuestionnaires.asStateFlow()

    private val _isRefreshingUserQuestionnaires = MutableStateFlow(false)
    val isRefreshingUserQuestionnaires: StateFlow<Boolean> = _isRefreshingUserQuestionnaires.asStateFlow()

    private val _isLoadingMoreQuestionnaires = MutableStateFlow(false)
    private val isLoadingMoreQuestionnaires: StateFlow<Boolean> = _isLoadingMoreQuestionnaires.asStateFlow()

    init {
        viewModelScope.launch {
            stateManager.authState.collect { authState ->
                if (authState == AuthState.AUTHENTICATED) {
                    loadLikedQuestionnaires()
                    loadUserQuestionnaires()
                }
            }
        }
    }


    private suspend fun loadQuestionnaires(game: Games? = null, page: Int = 1) {
        loadQuestionnairesUseCase(
            page = page, game = game, authorId = null
        ).onSuccess { result ->
            Log.d(TAG, result.toString())
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

    fun loadMoreQuestionnaires(currentPage: Int, questionnairesSize: Int) {
        if (isLoadingMoreQuestionnaires.value) return

        viewModelScope.launch(Dispatchers.IO) {

            _isLoadingMoreQuestionnaires.value = true
            val newPage = if (questionnairesSize % 10 == 0) {
                currentPage / 10 + 1
            } else {
                currentPage / 10 + 2
            }
            loadQuestionnaires(page = newPage)
            _isLoadingMoreQuestionnaires.value = false
        }
    }

    suspend fun loadUserQuestionnaires() {
        stateManager.updateContentState(ContentState.LOADING)
        loadUserQuestionnairesUseCase(game = null).onSuccess { result ->
            Log.d(TAG, result.toString())
            stateManager.updateUserQuestionnaires(result)
            stateManager.updateContentState(ContentState.LOADED)

        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
            stateManager.updateContentState(ContentState.ERROR)
            errorHandler.handleError(throwable)
        }
    }

    private suspend fun loadLikedQuestionnaires() {
        loadLikedQuestionnairesUseCase().onSuccess { result ->
            Log.d(TAG, result.toString())
            stateManager.updateLikedQuestionnaires(result)
        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
            stateManager.updateContentState(ContentState.ERROR)
            errorHandler.handleError(throwable)
        }
    }

    fun likeQuestionnaire(likedQuestionnaire: Questionnaire) {
        viewModelScope.launch {
            stateManager.updateContentState(ContentState.LOADING)
            likeQuestionnaireUseCase(
                likedQuestionnaireId = likedQuestionnaire.questionnaireId
            ).onSuccess { result ->
                Log.d(AuthorViewModel.TAG, result.toString())
                val likedQuestionnaires = likedQuestionnaires.value.plus(likedQuestionnaire)
                stateManager.updateLikedQuestionnaires(likedQuestionnaires = likedQuestionnaires)
                stateManager.updateContentState(ContentState.LOADED)
                _questionnaireUiEvent.tryEmit(QuestionnaireUiEvent.QuestionnaireLiked)
            }.onFailure { throwable ->
                Log.e(AuthorViewModel.TAG, throwable.toString())
                stateManager.updateContentState(ContentState.ERROR)
                errorHandler.handleError(throwable)
            }
        }
    }

    fun unlikeQuestionnaire(unlikedQuestionnaire: Questionnaire) {
        viewModelScope.launch {
            stateManager.updateContentState(ContentState.LOADING)
            unlikeQuestionnaireUseCase(
                unlikedQuestionnaireId = unlikedQuestionnaire.questionnaireId
            ).onSuccess { result ->
                Log.d(AuthorViewModel.TAG, result.toString())
                val likedQuestionnaires = likedQuestionnaires.value.minus(unlikedQuestionnaire)
                stateManager.updateLikedQuestionnaires(likedQuestionnaires = likedQuestionnaires)
                stateManager.updateContentState(ContentState.LOADED)
                _questionnaireUiEvent.tryEmit(QuestionnaireUiEvent.QuestionnaireUnliked)
            }.onFailure { throwable ->
                Log.e(AuthorViewModel.TAG, throwable.toString())
                stateManager.updateContentState(ContentState.ERROR)
                errorHandler.handleError(throwable)
            }
        }
    }

    fun createNewQuestionnaire(
        header: String,
        description: String,
        selectedGame: Games?,
        uri: Uri?,
        context: Context,
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
                        image = prepareImageForUploadUseCase(uri, context)
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

    fun refreshHomeScreen() {
        viewModelScope.launch {
            _isRefreshingQuestionnaires.value = true
            loadQuestionnaires()
            _isRefreshingQuestionnaires.value = false
        }
    }

    fun refreshUserQuestionnairesScreen() {
        viewModelScope.launch {
            _isRefreshingUserQuestionnaires.value = true
            loadUserQuestionnaires()
            _isRefreshingUserQuestionnaires.value = false
        }
    }

    fun refreshLikedQuestionnairesScreen() {
        viewModelScope.launch {
            _isRefreshingLikedQuestionnaires.value = true
            loadLikedQuestionnaires()
            _isRefreshingLikedQuestionnaires.value = false
        }
    }

    companion object {
        const val TAG = "QVM"
    }
}


sealed class QuestionnaireUiEvent {
    data object QuestionnaireCreated : QuestionnaireUiEvent()
    data object QuestionnaireLiked : QuestionnaireUiEvent()
    data object QuestionnaireUnliked : QuestionnaireUiEvent()
}




