package com.devkot.teammates.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devkot.teammates.R
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.ValidationResult
import com.devkot.teammates.domain.model.enums.AuthState
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.domain.model.enums.Games
import com.devkot.teammates.state.StateManager
import com.devkot.teammates.domain.usecase.CreateQuestionnaireUseCase
import com.devkot.teammates.domain.usecase.DeleteQuestionnaireUseCase
import com.devkot.teammates.domain.usecase.LikeQuestionnaireUseCase
import com.devkot.teammates.domain.usecase.LoadLikedQuestionnairesUseCase
import com.devkot.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.devkot.teammates.domain.usecase.LoadSelectedQuestionnaireUseCase
import com.devkot.teammates.domain.usecase.LoadUserQuestionnairesUseCase
import com.devkot.teammates.domain.usecase.PrepareImageForUploadUseCase
import com.devkot.teammates.domain.usecase.UnlikeQuestionnaireUseCase
import com.devkot.teammates.domain.usecase.UpdateQuestionnaireUseCase
import com.devkot.teammates.ui.snackbar.SnackbarController
import com.devkot.teammates.ui.snackbar.SnackbarEvent
import com.devkot.teammates.utils.ErrorHandler
import com.devkot.teammates.utils.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private val loadSelectedQuestionnaireUseCase: LoadSelectedQuestionnaireUseCase,

    private val loadLikedQuestionnairesUseCase: LoadLikedQuestionnairesUseCase,
    private val likeQuestionnaireUseCase: LikeQuestionnaireUseCase,
    private val unlikeQuestionnaireUseCase: UnlikeQuestionnaireUseCase,

    val createNewQuestionnaireUseCase: CreateQuestionnaireUseCase,
    val updateQuestionnaireUseCase: UpdateQuestionnaireUseCase,
    val deleteQuestionnaireUseCase: DeleteQuestionnaireUseCase,

    val prepareImageForUploadUseCase: PrepareImageForUploadUseCase,


    ) : ViewModel() {

    val questionnaires = stateManager.questionnaires
    val likedQuestionnaires = stateManager.likedQuestionnaires
    val userQuestionnaires = stateManager.userQuestionnaires


    val likedQuestionnairesState = stateManager.likedQuestionnairesState
    val userQuestionnairesState = stateManager.userQuestionnairesState
    val newQuestionnaireState = stateManager.newQuestionnaireState


    val selectedQuestionnaireState = stateManager.selectedQuestionnaireState
    val authorState = stateManager.authorState


    private val _questionnaireUiEvent =
        MutableSharedFlow<QuestionnaireUiEvent>(extraBufferCapacity = 1)
    val questionnaireUiEvent: SharedFlow<QuestionnaireUiEvent> = _questionnaireUiEvent


    private val _isRefreshingQuestionnaires = MutableStateFlow(false)
    val isRefreshingQuestionnaires: StateFlow<Boolean> = _isRefreshingQuestionnaires.asStateFlow()

    private val _isRefreshingLikedQuestionnaires = MutableStateFlow(false)
    val isRefreshingLikedQuestionnaires: StateFlow<Boolean> = _isRefreshingLikedQuestionnaires.asStateFlow()

    private val _isRefreshingUserQuestionnaires = MutableStateFlow(false)
    val isRefreshingUserQuestionnaires: StateFlow<Boolean> = _isRefreshingUserQuestionnaires.asStateFlow()

    private val _isRefreshingSelectedQuestionnaire = MutableStateFlow(false)
    val isRefreshingSelectedQuestionnaire: StateFlow<Boolean> = _isRefreshingSelectedQuestionnaire.asStateFlow()

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
            page = page, game = game
        ).onSuccess { (result, throwable) ->
            Log.d(TAG, result.toString())
            if (page == 1) {
                stateManager.updateQuestionnaires(result.shuffled())
            } else {
                stateManager.updateQuestionnaires(questionnaires.value + result)
            }
            throwable?.let { errorHandler.handleError(it) }
        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
            errorHandler.handleError(throwable)
        }

    }

    private suspend fun loadSelectedQuestionnaire() {
        stateManager.updateSelectedQuestionnaireState(ContentState.LOADING)
        loadSelectedQuestionnaireUseCase(
            questionnaireId = stateManager.selectedQuestionnaire.value.questionnaireId
        ).onSuccess { (result, throwable) ->
            Log.d(TAG, result.toString())
            val questionnaire = result.firstOrNull()
            if (questionnaire == null) {
                stateManager.deleteQuestionnaire(stateManager.selectedQuestionnaire.value.questionnaireId)
                stateManager.updateSelectedQuestionnaireState(ContentState.ERROR)
            } else {
                stateManager.updateQuestionnaire(questionnaire)
                stateManager.updateSelectedQuestionnaireState(ContentState.LOADED)
            }
            throwable?.let { errorHandler.handleError(it) }
        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
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
        stateManager.updateUserQuestionnairesState(ContentState.LOADING)
        loadUserQuestionnairesUseCase(game = null).onSuccess { (result, throwable) ->
            Log.d(TAG, result.toString())
            stateManager.updateUserQuestionnaires(result)
            stateManager.updateUserQuestionnairesState(ContentState.LOADED)
            throwable?.let { errorHandler.handleError(it) }

        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
            stateManager.updateUserQuestionnairesState(ContentState.ERROR)
            errorHandler.handleError(throwable)
        }
    }

    private suspend fun loadLikedQuestionnaires() {
        stateManager.updateLikedQuestionnairesState(ContentState.LOADING)
        loadLikedQuestionnairesUseCase().onSuccess { (result, throwable) ->
            Log.d(TAG, result.toString())
            stateManager.updateLikedQuestionnaires(result)
            stateManager.updateLikedQuestionnairesState(ContentState.LOADED)
            throwable?.let { errorHandler.handleError(it) }
        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
            stateManager.updateLikedQuestionnairesState(ContentState.ERROR)
            errorHandler.handleError(throwable)
        }
    }

    fun likeQuestionnaire(likedQuestionnaire: Questionnaire) {
        viewModelScope.launch {
            stateManager.updateSelectedQuestionnaireState(ContentState.LOADING)
            likeQuestionnaireUseCase(
                questionnaire = likedQuestionnaire
            ).onSuccess { result ->
                Log.d(TAG, result.toString())
                val likedQuestionnaires = likedQuestionnaires.value.plus(likedQuestionnaire)
                stateManager.updateLikedQuestionnaires(likedQuestionnaires = likedQuestionnaires)
                stateManager.updateSelectedQuestionnaireState(ContentState.LOADED)
                _questionnaireUiEvent.tryEmit(QuestionnaireUiEvent.QuestionnaireLiked)
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                stateManager.updateSelectedQuestionnaireState(ContentState.ERROR)
                loadLikedQuestionnaires()
                errorHandler.handleError(throwable)
                stateManager.updateSelectedQuestionnaireState(ContentState.INITIAL)
            }
        }
    }

    fun unlikeQuestionnaire(unlikedQuestionnaire: Questionnaire) {
        viewModelScope.launch {
            stateManager.updateSelectedQuestionnaireState(ContentState.LOADING)
            unlikeQuestionnaireUseCase(
                questionnaire = unlikedQuestionnaire
            ).onSuccess { result ->
                Log.d(TAG, result.toString())
                val likedQuestionnaires = likedQuestionnaires.value.filter { it.questionnaireId != unlikedQuestionnaire.questionnaireId }
                stateManager.updateLikedQuestionnaires(likedQuestionnaires = likedQuestionnaires)
                stateManager.updateSelectedQuestionnaireState(ContentState.LOADED)
                _questionnaireUiEvent.tryEmit(QuestionnaireUiEvent.QuestionnaireUnliked)
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                stateManager.updateSelectedQuestionnaireState(ContentState.ERROR)
                loadLikedQuestionnaires()
                errorHandler.handleError(throwable)
                stateManager.updateSelectedQuestionnaireState(ContentState.INITIAL)
            }
        }
    }

    fun createNewQuestionnaire(
        header: String,
        description: String,
        selectedGame: Games?,
        uri: Uri?,
        context: Context
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
                }

                ValidationResult.Success -> {
                    stateManager.updateNewQuestionnaireState(ContentState.LOADING)
                    createNewQuestionnaireUseCase(
                        header = header,
                        selectedGame = selectedGame!!,
                        description = description,
                        image = prepareImageForUploadUseCase(uri, context)
                    ).onSuccess {
                        stateManager.updateNewQuestionnaireState(ContentState.LOADED)
                        SnackbarController.sendEvent(SnackbarEvent(R.string.questionnaire_created_successfully))
                        _questionnaireUiEvent.tryEmit(QuestionnaireUiEvent.QuestionnaireCreated)
                    }.onFailure { throwable ->
                        stateManager.updateNewQuestionnaireState(ContentState.ERROR)
                        errorHandler.handleError(throwable)
                        stateManager.updateNewQuestionnaireState(ContentState.INITIAL)
                    }
                }
            }
        }
    }

    fun updateQuestionnaire(
        header: String,
        description: String,
        selectedGame: Games?,
        questionnaireId: String,
        uri: Uri?,
        context: Context,
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
                }

                ValidationResult.Success -> {
                    stateManager.updateSelectedQuestionnaireState(ContentState.LOADING)
                    updateQuestionnaireUseCase(
                        header = header,
                        selectedGame = selectedGame!!,
                        description = description,
                        questionnaireId = questionnaireId,
                        image = prepareImageForUploadUseCase(uri, context)
                    ).onSuccess { result ->
                        stateManager.updateQuestionnaire(result)
                        stateManager.updateSelectedQuestionnaireState(ContentState.LOADED)
                        SnackbarController.sendEvent(SnackbarEvent(R.string.questionnaire_updated_successfully))
                        _questionnaireUiEvent.tryEmit(QuestionnaireUiEvent.QuestionnaireUpdated)
                    }.onFailure { throwable ->
                        stateManager.updateSelectedQuestionnaireState(ContentState.ERROR)
                        errorHandler.handleError(throwable)
                    }
                }
            }
        }
    }

    fun deleteQuestionnaire(questionnaireId: String) {
        viewModelScope.launch {

            stateManager.updateSelectedQuestionnaireState(ContentState.LOADING)
            deleteQuestionnaireUseCase(questionnaireId = questionnaireId).onSuccess {
                stateManager.deleteQuestionnaire(questionnaireId)
                stateManager.updateSelectedQuestionnaireState(ContentState.INITIAL)
                SnackbarController.sendEvent(SnackbarEvent(R.string.questionnaire_deleted_successfully))
                _questionnaireUiEvent.tryEmit(QuestionnaireUiEvent.QuestionnaireDeleted)
            }.onFailure { throwable ->
                stateManager.updateSelectedQuestionnaireState(ContentState.ERROR)
                errorHandler.handleError(throwable)
            }
        }
            
    }

    fun refreshHomeScreen() {
        viewModelScope.launch {
            _isRefreshingQuestionnaires.value = true
            loadQuestionnaires()
            delay(100)
            _isRefreshingQuestionnaires.value = false
        }
    }

    fun refreshUserQuestionnairesScreen() {
        viewModelScope.launch {
            stateManager.updateSelectedQuestionnaireState(ContentState.INITIAL)
            _isRefreshingUserQuestionnaires.value = true
            loadUserQuestionnaires()
            delay(100)
            _isRefreshingUserQuestionnaires.value = false
        }
    }

    fun refreshLikedQuestionnairesScreen() {
        viewModelScope.launch {
            _isRefreshingLikedQuestionnaires.value = true
            loadLikedQuestionnaires()
            delay(100)
            _isRefreshingLikedQuestionnaires.value = false
        }
    }


    fun refreshSelectedQuestionnaire() {
        viewModelScope.launch {
            _isRefreshingSelectedQuestionnaire.value = true
            loadSelectedQuestionnaire()
            loadLikedQuestionnaires()
            delay(100)
            _isRefreshingSelectedQuestionnaire.value = false
        }
    }

    fun resetSelectedQuestionnaireState() {
        viewModelScope.launch {
            delay(1000)
            stateManager.updateSelectedQuestionnaireState(ContentState.INITIAL)
        }
    }

    fun resetNewQuestionnaireState() {
        stateManager.updateNewQuestionnaireState(ContentState.INITIAL)
    }

    companion object {
        const val TAG = "QVM"
    }
}


sealed class QuestionnaireUiEvent {
    data object QuestionnaireCreated : QuestionnaireUiEvent()
    data object QuestionnaireUpdated : QuestionnaireUiEvent()
    data object QuestionnaireDeleted : QuestionnaireUiEvent()
    data object QuestionnaireLiked : QuestionnaireUiEvent()
    data object QuestionnaireUnliked : QuestionnaireUiEvent()
}




