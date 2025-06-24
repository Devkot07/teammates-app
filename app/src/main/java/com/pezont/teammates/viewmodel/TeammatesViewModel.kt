package com.pezont.teammates.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.R
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
import com.pezont.teammates.domain.usecase.PrepareImageForUploadUseCase
import com.pezont.teammates.domain.usecase.UpdateUserProfileUseCase
import com.pezont.teammates.state.StateManager
import com.pezont.teammates.ui.snackbar.SnackbarController
import com.pezont.teammates.ui.snackbar.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


//TODO crushing viewModel
@HiltViewModel
class TeammatesViewModel @Inject constructor(

    private val stateManager: StateManager,

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

    val prepareImageForUploadUseCase: PrepareImageForUploadUseCase,

    ) : ViewModel() {

    val user = stateManager.user

    val authState = stateManager.authState
    val contentState = stateManager.contentState

    val questionnaires = stateManager.questionnaires
    val likedQuestionnaires = stateManager.likedQuestionnaires
    val userQuestionnaires = stateManager.userQuestionnaires

    val selectedAuthor = stateManager.selectedAuthor
    val selectedQuestionnaire = stateManager.selectedQuestionnaire
    val selectedAuthorQuestionnaires = stateManager.selectedAuthorQuestionnaires


    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        viewModelScope.launch {
            checkAuthentication()
        }
    }

    private suspend fun checkAuthentication() {
        stateManager.updateAuthState(AuthState.LOADING)
        runCatching {
            checkAuthenticationUseCase().first()
        }.onSuccess { authenticated ->
            val newAuthState =
                if (authenticated) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
            stateManager.updateUser(if (authenticated) loadUserUseCase() else User())
            stateManager.updateAuthState(newAuthState)

            if (authenticated) loadLikedQuestionnaires()
        }.onFailure { throwable ->
            Log.e(TAG, throwable.toString())
            stateManager.updateAuthState(AuthState.UNAUTHENTICATED)
        }
    }


    fun login(nickname: String, password: String) {
        viewModelScope.launch {
            stateManager.updateAuthState(AuthState.LOADING)
            loginUseCase(nickname, password).onSuccess {
                stateManager.updateUser(loadUserUseCase())
                stateManager.updateAuthState(AuthState.AUTHENTICATED)

                _uiEvent.tryEmit(UiEvent.LoggedIn)
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                stateManager.updateAuthState(AuthState.UNAUTHENTICATED)
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
                    stateManager.updateQuestionnaires(result)
                } else {
                    stateManager.updateQuestionnaires(questionnaires.value + result)
                }
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                stateManager.updateContentState(ContentState.ERROR)
                handleError(throwable)
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
            handleError(throwable)
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
                    stateManager.updateContentState(ContentState.LOADING)
                    createNewQuestionnaireUseCase(
                        header = header,
                        selectedGame = selectedGame!!,
                        description = description,
                        image = image
                    ).onSuccess {
                        stateManager.updateContentState(ContentState.LOADED)
                        SnackbarController.sendEvent(SnackbarEvent(R.string.questionnaire_created_successfully))
                        _uiEvent.tryEmit(UiEvent.QuestionnaireCreated)
                        onSuccess()
                    }.onFailure { throwable ->
                        stateManager.updateContentState(ContentState.ERROR)
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
                                    handleError(throwable)
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
                        handleError(throwable)
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
                        handleError(throwable)
                    }
                }.onFailure { throwable ->
                    stateManager.updateContentState(ContentState.ERROR)
                    handleError(throwable)
                }
            }
        }
    }

    fun updateSelectedQuestionnaire(questionnaire: Questionnaire) {
        stateManager.updateSelectedQuestionnaire(questionnaire)
    }

    fun logout() {
        viewModelScope.launch {
            stateManager.updateAuthState(AuthState.LOADING)
            logoutUseCase().onSuccess {
                stateManager.updateUser(User())
                stateManager.updateAuthState(AuthState.UNAUTHENTICATED)
                stateManager.updateContentState(ContentState.INITIAL)
                stateManager.updateQuestionnaires(emptyList())
                stateManager.updateLikedQuestionnaires(emptyList())
                stateManager.updateUserQuestionnaires(emptyList())

                stateManager.updateSelectedAuthor(User())
                stateManager.updateSelectedQuestionnaire(Questionnaire())
                stateManager.updateSelectedAuthorQuestionnaires(emptyList())

                _uiEvent.tryEmit(UiEvent.LoggedOut)
                SnackbarController.sendEvent(SnackbarEvent(R.string.logged_out))
            }.onFailure { throwable ->
                stateManager.updateAuthState(AuthState.AUTHENTICATED)
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
                                stateManager.updateUser(User())
                                stateManager.updateAuthState(AuthState.UNAUTHENTICATED)
                                stateManager.updateContentState(ContentState.INITIAL)
                                stateManager.updateQuestionnaires(emptyList())
                                stateManager.updateLikedQuestionnaires(emptyList())
                                stateManager.updateUserQuestionnaires(emptyList())

                                stateManager.updateSelectedAuthor(User())
                                stateManager.updateSelectedQuestionnaire(Questionnaire())
                                stateManager.updateSelectedAuthorQuestionnaires(emptyList())

                                _uiEvent.tryEmit(UiEvent.LoggedOut)
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


sealed class UiEvent {
    data object UserProfileUpdated : UiEvent()
    data object QuestionnaireCreated : UiEvent()
    data object LoggedOut : UiEvent()
    data object LoggedIn : UiEvent()
}