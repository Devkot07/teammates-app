package com.pezont.teammates

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.data.repository.UserDataRepositoryImpl
import com.pezont.teammates.domain.model.Games
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.usecase.CheckAuthenticationUseCase
import com.pezont.teammates.domain.usecase.LoadLikedQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadUserQuestionnairesUseCase
import com.pezont.teammates.domain.usecase.LoadUserUseCase
import com.pezont.teammates.domain.usecase.LoginUseCase
import com.pezont.teammates.domain.usecase.LogoutUseCase
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

//TODO DI
@HiltViewModel
class TeammatesViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkAuthenticationUseCase: CheckAuthenticationUseCase,

    private val loadUserUseCase: LoadUserUseCase,

    private val loadQuestionnairesUseCase: LoadQuestionnairesUseCase,
    private val loadUserQuestionnairesUseCase: LoadUserQuestionnairesUseCase,
    private val loadLikedQuestionnairesUseCase: LoadLikedQuestionnairesUseCase,

    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepositoryImpl,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _teammatesAppState = MutableStateFlow(TeammatesUiState())
    val teammatesAppState: StateFlow<TeammatesUiState> = _teammatesAppState.asStateFlow()

    private val _authToastCode = MutableSharedFlow<Int?>(extraBufferCapacity = 1)
    val authToastCode: SharedFlow<Int?> = _authToastCode

    private val _questionnairesToastCode = MutableSharedFlow<Int?>(extraBufferCapacity = 1)
    val questionnairesToastCode: SharedFlow<Int?> = _questionnairesToastCode


    init {
        viewModelScope.launch {
            checkAuthentication()
        }
    }

    private suspend fun checkAuthentication() {
        _teammatesAppState.update { it.copy(isLoading = true) }
        runCatching {
            checkAuthenticationUseCase().first()
        }.onSuccess { isAuthenticated ->
            _teammatesAppState.update { it.copy(isAuthenticated = isAuthenticated) }
        }.onFailure { handleError(it) }
        _teammatesAppState.update { it.copy(isLoading = false, user = loadUserUseCase()) }
    }


    fun login(nickname: String, password: String) {
        viewModelScope.launch {
            _teammatesAppState.update { it.copy(isLoading = true) }
            loginUseCase(nickname, password)
                .onSuccess {
                    _teammatesAppState.update {
                        it.copy(
                            isAuthenticated = true,
                            user = loadUserUseCase()
                        )
                    }
                }
                .onFailure { handleError(it) }
            _teammatesAppState.update { it.copy(isLoading = false) }
        }
    }


    fun loadQuestionnaires(game: Games? = null, page: Int = 1) {
        viewModelScope.launch {
            loadQuestionnairesUseCase(page = page, game = game, authorId = null)
                .onSuccess { result ->
                    Log.i(TAG, result.toString())
                    if (page == 1) {
                        _teammatesAppState.update { it.copy(questionnaires = result) }
                    } else {
                        _teammatesAppState.update { it.copy(questionnaires = teammatesAppState.value.questionnaires + result) }
                    }
                }.onFailure { handleError(it) }
        }
    }

    suspend fun loadLikedQuestionnaires() {
            loadLikedQuestionnairesUseCase()
                .onSuccess { result ->
                    Log.i(TAG, result.toString())
                    _teammatesAppState.update { it.copy(likedQuestionnaires = result) }
                }.onFailure { handleError(it) }
    }

    fun loadUserQuestionnaires() {
        viewModelScope.launch {
            loadUserQuestionnairesUseCase(game = null)
                .onSuccess { result ->
                    Log.i(TAG, result.toString())
                    _teammatesAppState.update { it.copy(userQuestionnaires = result) }
                }.onFailure { handleError(it) }

        }
    }

    fun createNewQuestionnaire(
        header: String, description: String, selectedGame: Games, image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _teammatesAppState.update { it.copy(isLoading = true) }

            try {
                val user = userDataRepository.user.first()
                if (user.publicId == null) {
                    _teammatesAppState.update { it.copy(isAuthenticated = false) }
                    return@launch
                } else {
                    val createQuestionnaireResult = questionnairesRepository.createQuestionnaire(
                        token = userDataRepository.accessToken.first(),
                        header = header,
                        game = selectedGame,
                        description = description,
                        authorId = user.publicId!!,
                        image = image,
                    )

                    if (createQuestionnaireResult.isSuccess) {
                        loadUserQuestionnaires()
                        _questionnairesToastCode.tryEmit(200)
                    } else {
                        handleError(createQuestionnaireResult.exceptionOrNull())
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _teammatesAppState.update { it.copy(isLoading = false) }

            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
                .onSuccess {
                    _teammatesAppState.update {
                        it.copy(
                            user = User(),
                            isAuthenticated = false,
                            isLoading = false,
                            questionnaires = emptyList(),
                            likedQuestionnaires = emptyList(),
                            userQuestionnaires = emptyList()
                        )
                    }
                    _authToastCode.tryEmit(1)
                }
        }
    }

    fun clearError() {
        _teammatesAppState.update { it.copy(errorState = ErrorState()) }
    }


    private fun handleError(error: Throwable?) {
        Log.e(TAG, "error: $error")
        _teammatesAppState.update { currentState ->
            when (error) {
                is IOException -> currentState.copy(
                    errorState = ErrorState(
                        isNetworkError = true,
                        errorMessage = "Network error. Please check your connection."
                    )
                )

                is HttpException -> {
                    Log.e(TAG, "http error: $error")
                    val errorCode = error.code()

                    if (errorCode == 401) {
                        _authToastCode.tryEmit(errorCode)
                        logout()
                        return@update currentState.copy(isAuthenticated = false)
                    }

                    _questionnairesToastCode.tryEmit(errorCode)

                    currentState.copy(
                        errorState = ErrorState(
                            errorCode = errorCode, errorMessage = "Server error: ${
                                error.response()?.errorBody()?.string() ?: "Unknown"
                            }"
                        )
                    )
                }

                else -> currentState.copy(
                    errorState = ErrorState(
                        errorMessage = "Unknown error occurred: ${error?.message}"
                    )
                )
            }
        }
    }


    companion object {
        const val TAG: String = "ViewModel"
    }
}

data class TeammatesUiState(
    val user: User = User(),
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val errorState: ErrorState = ErrorState(),
    val questionnaires: List<Questionnaire> = emptyList(),
    val likedQuestionnaires: List<Questionnaire> = emptyList(),
    val userQuestionnaires: List<Questionnaire> = emptyList()
)


data class ErrorState(
    val isNetworkError: Boolean = false, val errorCode: Int = 0, val errorMessage: String? = null
)

sealed class UiEvent {
    data class ShowError(val message: String) : UiEvent()
    data object QuestionnaireCreated : UiEvent()
    data object LoggedOut : UiEvent()
}