package com.pezont.teammates

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.domain.model.AuthState
import com.pezont.teammates.domain.model.ContentState
import com.pezont.teammates.domain.model.Games
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.usecase.CheckAuthenticationUseCase
import com.pezont.teammates.domain.usecase.CreateQuestionnaireUseCase
import com.pezont.teammates.domain.usecase.LoadAuthorProfileUseCase
import com.pezont.teammates.domain.usecase.LoadLikedQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadUserQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadUserUseCase
import com.pezont.teammates.domain.usecase.LoginUseCase
import com.pezont.teammates.domain.usecase.LogoutUseCase
import com.pezont.teammates.ui.snackbar.SnackbarController
import com.pezont.teammates.ui.snackbar.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TeammatesViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkAuthenticationUseCase: CheckAuthenticationUseCase,

    private val loadUserUseCase: LoadUserUseCase,

    private val loadQuestionnairesUseCase: LoadQuestionnairesUseCase,
    private val loadUserQuestionnairesUseCase: LoadUserQuestionnairesUseCase,
    private val loadLikedQuestionnairesUseCase: LoadLikedQuestionnairesUseCase,

    private val loadAuthorProfileUseCase: LoadAuthorProfileUseCase,

    val createNewQuestionnaireUseCase: CreateQuestionnaireUseCase,

    ) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        viewModelScope.launch {
            checkAuthentication()
        }
    }

    private suspend fun checkAuthentication() {
        _uiState.update { it.copy(authState = AuthState.LOADING) }
        runCatching {
            checkAuthenticationUseCase().first()
        }.onSuccess { authenticated ->
            val newAuthState =
                if (authenticated) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
            _uiState.update {
                it.copy(
                    authState = newAuthState,
                    user = if (authenticated) loadUserUseCase() else User()
                )
            }
            if (authenticated) loadLikedQuestionnaires()
        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
            _uiState.update { it.copy(authState = AuthState.UNAUTHENTICATED) }
        }
    }


    fun login(nickname: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.LOADING) }
            loginUseCase(nickname, password).onSuccess {
                _uiState.update {
                    it.copy(authState = AuthState.AUTHENTICATED, user = loadUserUseCase())
                }
                _uiEvent.tryEmit(UiEvent.LoggedIn)
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                _uiState.update { it.copy(authState = AuthState.UNAUTHENTICATED) }
                SnackbarController.sendEvent(SnackbarEvent(R.string.you_aren_t_logged_in))
            }
        }
    }


    fun loadQuestionnaires(game: Games? = null, page: Int = 1) {
        viewModelScope.launch {
            loadQuestionnairesUseCase(
                page = page, game = game, authorId = null
            ).onSuccess { result ->
                Log.i(TAG, result.toString())
                if (page == 1) {
                    _uiState.update {
                        it.copy(questionnaires = result)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            questionnaires = uiState.value.questionnaires + result,
                        )
                    }
                }
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                _uiState.update { it.copy(contentState = ContentState.ERROR) }
                handleError(throwable)
            }
        }
    }

    suspend fun loadLikedQuestionnaires() {
        loadLikedQuestionnairesUseCase().onSuccess { result ->
            Log.i(TAG, result.toString())
            _uiState.update {
                it.copy(
                    likedQuestionnaires = result,
                )
            }
        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
            _uiState.update { it.copy(contentState = ContentState.ERROR) }
            handleError(throwable)
        }
    }

    fun loadUserQuestionnaires() {
        viewModelScope.launch {
            _uiState.update { it.copy(contentState = ContentState.LOADING) }
            loadUserQuestionnairesUseCase(game = null).onSuccess { result ->
                Log.i(TAG, result.toString())
                _uiState.update {
                    it.copy(
                        userQuestionnaires = result,
                        contentState = ContentState.LOADED
                    )
                }
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                _uiState.update { it.copy(contentState = ContentState.ERROR) }
                handleError(throwable)
            }
        }
    }

    fun createNewQuestionnaire(
        header: String, description: String, selectedGame: Games, image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(contentState = ContentState.LOADING) }
            createNewQuestionnaireUseCase(
                header = header,
                selectedGame = selectedGame,
                description = description,
                image = image
            ).onSuccess {
                _uiState.update { it.copy(contentState = ContentState.LOADED) }
                _uiEvent.tryEmit(UiEvent.QuestionnaireCreated)
                SnackbarController.sendEvent(SnackbarEvent(R.string.questionnaire_created_successfully))
            }.onFailure { throwable ->
                _uiState.update { it.copy(contentState = ContentState.ERROR) }
                handleError(throwable)
            }
        }
    }

    fun loadAuthor(authorId: String) {
        _uiState.update {
            it.copy(
                selectedAuthor = User(),
                contentState = ContentState.LOADING
            )
        }
        viewModelScope.launch {
            loadAuthorProfileUseCase(authorId).onSuccess { author ->
                _uiState.update {
                    it.copy(
                        selectedAuthor = author,
                        contentState = ContentState.LOADED
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { it.copy(contentState = ContentState.ERROR) }
                handleError(throwable)
            }
        }
    }

    fun updateSelectedQuestionnaire(questionnaire: Questionnaire) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedQuestionnaire = questionnaire) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.LOADING) }
            logoutUseCase()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            user = User(),
                            authState = AuthState.UNAUTHENTICATED,
                            questionnaires = emptyList(),
                            likedQuestionnaires = emptyList(),
                            userQuestionnaires = emptyList(),
                            selectedQuestionnaire = Questionnaire(),
                            selectedAuthor = User(),
                            contentState = ContentState.INITIAL
                        )
                    }
                    _uiEvent.tryEmit(UiEvent.LoggedOut)
                    SnackbarController.sendEvent(SnackbarEvent(R.string.logged_out))
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(authState = AuthState.AUTHENTICATED) }
                    handleError(throwable)
                }
        }
    }

    private fun handleError(error: Throwable?) {
        Log.e(TAG, "error: $error")
        viewModelScope.launch {
            when (error) {
                is IOException -> {
                    SnackbarController.sendEvent(
                        SnackbarEvent(R.string.network_error_please_check_your_connection)
                    )
                }

                is HttpException -> {
                    Log.e(TAG, "http error: $error")
                    val errorCode = error.code()

                    if (errorCode == 401) {
                        viewModelScope.launch {

                            logoutUseCase()
                                .onSuccess {
                                    _uiState.update {
                                        it.copy(
                                            user = User(),
                                            authState = AuthState.UNAUTHENTICATED,
                                            questionnaires = emptyList(),
                                            likedQuestionnaires = emptyList(),
                                            userQuestionnaires = emptyList(),
                                            selectedQuestionnaire = Questionnaire(),
                                            selectedAuthor = User(),
                                            contentState = ContentState.INITIAL
                                        )
                                    }
                                    _uiEvent.tryEmit(UiEvent.LoggedOut)
                                }
                                .onFailure { throwable ->
                                    _uiState.update { it.copy(authState = AuthState.AUTHENTICATED) }
                                    handleError(throwable)
                                }
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.authentication_error_please_log_in_again)
                            )
                        }
                        return@launch
                    }

                    SnackbarController.sendEvent(
                        SnackbarEvent(R.string.server_error)
                    )
                }

                else -> {
                    SnackbarController.sendEvent(
                        SnackbarEvent(R.string.unknown_error_occurred)
                    )
                }
            }
        }
    }

    companion object {
        const val TAG: String = "ViewModel"
    }
}

data class UiState(
    val user: User = User(),
    val authState: AuthState = AuthState.INITIAL,
    val contentState: ContentState = ContentState.INITIAL,

    val questionnaires: List<Questionnaire> = emptyList(),
    val likedQuestionnaires: List<Questionnaire> = emptyList(),
    val userQuestionnaires: List<Questionnaire> = emptyList(),

    val selectedQuestionnaire: Questionnaire = Questionnaire(),
    val selectedAuthor: User = User()
)

sealed class UiEvent {
    data object QuestionnaireCreated : UiEvent()
    data object LoggedOut : UiEvent()
    data object LoggedIn : UiEvent()
}