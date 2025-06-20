package com.pezont.teammates

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.ValidationError
import com.pezont.teammates.domain.model.ValidationResult
import com.pezont.teammates.domain.model.enums.AuthState
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.model.enums.Games
import com.pezont.teammates.domain.usecase.CheckAuthenticationUseCase
import com.pezont.teammates.domain.usecase.CreateQuestionnaireUseCase
import com.pezont.teammates.domain.usecase.LoadAuthorProfileUseCase
import com.pezont.teammates.domain.usecase.LoadLikedQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadUserQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadUserUseCase
import com.pezont.teammates.domain.usecase.LoginUseCase
import com.pezont.teammates.domain.usecase.LogoutUseCase
import com.pezont.teammates.domain.usecase.UpdateUserProfileUseCase
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
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
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

    val updateUserProfileUseCase: UpdateUserProfileUseCase,

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
                        userQuestionnaires = result, contentState = ContentState.LOADED
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
                    _uiState.update { it.copy(contentState = ContentState.LOADING) }
                    createNewQuestionnaireUseCase(
                        header = header,
                        selectedGame = selectedGame!!,
                        description = description,
                        image = image
                    ).onSuccess {
                        _uiState.update { it.copy(contentState = ContentState.LOADED) }
                        SnackbarController.sendEvent(SnackbarEvent(R.string.questionnaire_created_successfully))
                        _uiEvent.tryEmit(UiEvent.QuestionnaireCreated)
                        onSuccess()
                    }.onFailure { throwable ->
                        _uiState.update { it.copy(contentState = ContentState.ERROR) }
                        handleError(throwable)
                        onError()
                    }
                }
            }
        }
    }

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
                    _uiState.update { it.copy(contentState = ContentState.LOADING) }
                    updateUserProfileUseCase(
                        nickname = nickname,
                        description = description
                    ).onSuccess { user ->
                        if (image != null) {
                            updateUserProfileUseCase.updateUserAvatar(image)
                                .onSuccess { newImagePath ->
                                    _uiState.update {
                                        it.copy(
                                            contentState = ContentState.LOADED,
                                            user = uiState.value.user.copy(
                                                nickname = user.nickname,
                                                description = user.description,
                                                imagePath = newImagePath.imagePath
                                            )
                                        )
                                    }
                                    SnackbarController.sendEvent(SnackbarEvent(R.string.photo_update))
                                }.onFailure { throwable ->
                                _uiState.update { it.copy(contentState = ContentState.ERROR) }
                                handleError(throwable)
                                return@onFailure
                            }
                        } else {

                            _uiState.update {
                                it.copy(
                                    contentState = ContentState.LOADED,
                                    user = uiState.value.user.copy(
                                        nickname = user.nickname,
                                        description = user.description,
                                        imagePath = user.imagePath
                                    )
                                )
                            }
                            SnackbarController.sendEvent(SnackbarEvent(R.string.information_update_successfully))
                        }
                        _uiEvent.tryEmit(UiEvent.UserProfileUpdated)
                        onSuccess()
                    }.onFailure { throwable ->
                        _uiState.update { it.copy(contentState = ContentState.ERROR) }
                        handleError(throwable)
                    }
                }
            }
        }
    }

    fun loadAuthor(authorId: String) {
        if (uiState.value.selectedAuthor.publicId != authorId) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        selectedAuthor = User(),
                        contentState = ContentState.LOADING
                    )
                }
                loadAuthorProfileUseCase(authorId).onSuccess { author ->
                    _uiState.update { it.copy(selectedAuthor = author) }
                    loadQuestionnairesUseCase(
                        game = null,
                        limit = 100,
                        authorId = author.publicId
                    ).onSuccess { result ->
                        Log.i(TAG, result.toString())
                        _uiState.update {
                            it.copy(
                                selectedAuthorQuestionnaires = result,
                                contentState = ContentState.LOADED
                            )
                        }
                    }.onFailure { throwable ->
                        Log.e(TAG, throwable.toString())
                        _uiState.update { it.copy(contentState = ContentState.ERROR) }
                        handleError(throwable)
                    }
                }.onFailure { throwable ->
                    _uiState.update { it.copy(contentState = ContentState.ERROR) }
                    handleError(throwable)
                }
            }
        }
    }

    fun updateSelectedQuestionnaire(questionnaire: Questionnaire) {
        _uiState.update { it.copy(selectedQuestionnaire = questionnaire) }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.LOADING) }
            logoutUseCase().onSuccess {
                _uiState.update {
                    it.copy(
                        user = User(),
                        authState = AuthState.UNAUTHENTICATED,
                        questionnaires = emptyList(),
                        likedQuestionnaires = emptyList(),
                        userQuestionnaires = emptyList(),
                        selectedAuthor = User(),
                        selectedQuestionnaire = Questionnaire(),
                        selectedAuthorQuestionnaires = emptyList(),
                        contentState = ContentState.INITIAL
                    )
                }
                _uiEvent.tryEmit(UiEvent.LoggedOut)
                SnackbarController.sendEvent(SnackbarEvent(R.string.logged_out))
            }.onFailure { throwable ->
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
                    when (error) {
                        is UnknownHostException -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.no_internet_connection)
                            )
                        }

                        is SocketTimeoutException -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.connection_timeout)
                            )
                        }

                        is ConnectException -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.server_unavailable)
                            )
                        }

                        else -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.network_error_please_check_your_connection)
                            )
                        }
                    }
                }

                is HttpException -> {
                    Log.e(TAG, "http error: $error")
                    val errorBody = error.response()?.errorBody()?.string()

                    when {
                        errorBody?.contains(
                            "Not authenticated",
                            ignoreCase = true
                        ) == true || error.code() == 401 -> {
                            logoutUseCase().onSuccess {
                                _uiState.update {
                                    it.copy(
                                        user = User(),
                                        authState = AuthState.UNAUTHENTICATED,
                                        questionnaires = emptyList(),
                                        likedQuestionnaires = emptyList(),
                                        userQuestionnaires = emptyList(),
                                        selectedAuthor = User(),
                                        selectedQuestionnaire = Questionnaire(),
                                        selectedAuthorQuestionnaires = emptyList(),
                                        contentState = ContentState.INITIAL
                                    )
                                }
                                _uiEvent.tryEmit(UiEvent.LoggedOut)
                            }
                                .onFailure { throwable ->
                                    _uiState.update { it.copy(authState = AuthState.AUTHENTICATED) }
                                    handleError(throwable)
                                }
                        }

                        error.code() == 400 -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.invalid_request_error)
                            )
                        }

                        error.code() == 401 -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.authorization_error_authorization_failed)
                            )
                        }

                        error.code() == 403 -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.access_denied_error)
                            )
                        }

                        error.code() == 404 -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.resource_not_found_error)
                            )
                        }

                        error.code() in 500..599 -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.server_error_please_try_again_later)
                            )
                        }

                        else -> {
                            SnackbarController.sendEvent(
                                SnackbarEvent(R.string.server_communication_error)
                            )
                        }
                    }
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

fun ValidationError.toMessageRes(): Int = when (this) {

    ValidationError.HEADER_TOO_SHORT -> R.string.the_header_must_contain_at_least_3_characters
    ValidationError.HEADER_TOO_LONG -> R.string.the_maximum_length_of_the_header_is_80_characters
    ValidationError.DESCRIPTION_TOO_LONG -> R.string.the_maximum_length_of_the_description_is_300_characters
    ValidationError.GAME_NOT_SELECTED -> R.string.please_select_a_game
    ValidationError.NICKNAME_TOO_SHORT -> R.string.the_nickname_must_contain_at_least_3_characters
    ValidationError.NICKNAME_TOO_LONG -> R.string.the_maximum_length_of_the_nickname_is_20_characters
}


data class UiState(
    val user: User = User(),
    val authState: AuthState = AuthState.INITIAL,
    val contentState: ContentState = ContentState.INITIAL,

    val questionnaires: List<Questionnaire> = emptyList(),
    val likedQuestionnaires: List<Questionnaire> = emptyList(),
    val userQuestionnaires: List<Questionnaire> = emptyList(),

    val selectedAuthor: User = User(),
    val selectedQuestionnaire: Questionnaire = Questionnaire(),
    val selectedAuthorQuestionnaires: List<Questionnaire> = emptyList(),

    )

sealed class UiEvent {
    data object UserProfileUpdated : UiEvent()
    data object QuestionnaireCreated : UiEvent()
    data object LoggedOut : UiEvent()
    data object LoggedIn : UiEvent()
}