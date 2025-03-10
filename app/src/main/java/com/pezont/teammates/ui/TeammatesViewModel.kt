package com.pezont.teammates.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.data.AuthRepository
import com.pezont.teammates.data.QuestionnairesRepository
import com.pezont.teammates.data.UserDataRepository
import com.pezont.teammates.models.Games
import com.pezont.teammates.models.Questionnaire
import com.pezont.teammates.models.User
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
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository,
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
        userDataRepository.user.collect { user ->
            val isLoggedIn = user.publicId != null
            _teammatesAppState.update {
                it.copy(
                    isAuthenticated = isLoggedIn,
                    currentUser = user,
                    isLoading = false
                )
            }
        }
    }

    fun login(nickname: String, password: String) {
        viewModelScope.launch {
            _teammatesAppState.update { it.copy(isLoading = true) }
            try {
                val authResult = authRepository.login(nickname, password)
                if (authResult.isSuccess) {
                    val response = authResult.getOrNull()
                    response?.let {
                        userDataRepository.saveAccessToken(it.accessToken)
                        userDataRepository.saveRefreshToken(it.refreshToken)
                        userDataRepository.saveUser(it.user)
                        _teammatesAppState.update { state -> state.copy(isAuthenticated = true, currentUser = it.user) }
                    }
                } else {
                    handleError(authResult.exceptionOrNull())
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _teammatesAppState.update { it.copy(isLoading = false) }
            }
        }
    }
    fun fetchQuestionnaires(game: Games? = null, page: Int = 1, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val user = userDataRepository.user.first()
                if (user.publicId == null) {
                    _teammatesAppState.update { it.copy(isAuthenticated = false) }
                    return@launch
                }

                val questionnairesResult = questionnairesRepository.getQuestionnairesFromRepo(
                    token = userDataRepository.accessToken.first(),
                    userId = user.publicId,
                    page = page,
                    limit = limit,
                    game = game,
                    authorId = null,
                    questionnaireId = null
                )

                if (questionnairesResult.isSuccess) {
                    val response = questionnairesResult.getOrNull()
                    response?.let {newQuestionnaires ->
                        if (page == 1) {
                            _teammatesAppState.update { it.copy(questionnaires = newQuestionnaires) }
                        } else {
                            _teammatesAppState.update { it.copy(questionnaires = teammatesAppState.value.questionnaires + newQuestionnaires) }
                        }
                    }
                } else {
                    handleError(questionnairesResult.exceptionOrNull())
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun fetchLikedQuestionnaires() {
        TODO()
    }

    fun loadUserQuestionnaires() {
        viewModelScope.launch {
            try {
                val user = userDataRepository.user.first()
                if (user.publicId == null) {
                    _teammatesAppState.update { it.copy(isAuthenticated = false) }
                    return@launch
                }

                val questionnairesResult = questionnairesRepository.getQuestionnairesFromRepo(
                    token = userDataRepository.accessToken.first(),
                    userId = user.publicId,
                    page = null,
                    limit = 100,
                    game = null,
                    authorId = user.publicId,
                    questionnaireId = null
                )

                if (questionnairesResult.isSuccess) {
                    val response = questionnairesResult.getOrNull()
                    response?.let {
                        _teammatesAppState.update { state -> state.copy(userQuestionnaires = it) }
                    }
                } else {
                    handleError(questionnairesResult.exceptionOrNull())
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _teammatesAppState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createNewQuestionnaire(
        header: String,
        description: String,
        selectedGame: Games,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _teammatesAppState.update { it.copy(isLoading = true) }

            try {
                val user = userDataRepository.user.first()
                if (user.publicId == null) {
                    _teammatesAppState.update { it.copy(isAuthenticated = false) }
                    return@launch
                }

                val createQuestionnaireResult = questionnairesRepository.createQuestionnaire(
                    token = userDataRepository.accessToken.first(),
                    header = header,
                    game = selectedGame,
                    description = description,
                    authorId = user.publicId,
                    image = image,
                )

                if (createQuestionnaireResult.isSuccess) {
                    loadUserQuestionnaires()
                    _questionnairesToastCode.tryEmit(200)
                } else {
                    handleError(createQuestionnaireResult.exceptionOrNull())
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
            userDataRepository.saveAccessToken("")
            userDataRepository.saveRefreshToken("")
            userDataRepository.saveUser(User())
            _teammatesAppState.update {
                it.copy(
                    isAuthenticated = false,
                    isLoading = false,
                    currentUser = User(),
                    questionnaires = emptyList(),
                    likedQuestionnaires = emptyList(),
                    userQuestionnaires = emptyList()
                )
            }
            _authToastCode.tryEmit(1)
        }
    }

    fun clearError() {
        _teammatesAppState.update { it.copy(errorState = ErrorState()) }
    }


    private fun handleError(error: Throwable?) {
        _teammatesAppState.update { currentState ->
            val newErrorState = when (error) {
                is IOException -> ErrorState(
                    isNetworkError = true,
                    errorMessage = "Network error. Please check your connection."
                )

                is HttpException -> {
                    val errorCode = error.code()
                    if (errorCode == 401) {
                        _authToastCode.tryEmit(errorCode)
                        return@update currentState.copy(isAuthenticated = false)
                    } else {
                        _questionnairesToastCode.tryEmit(errorCode)
                        ErrorState(
                            errorCode = errorCode,
                            errorMessage = "Server error ${error.response()?.errorBody()?.string() ?: "Unknown"}"
                        )
                    }
                }

                else -> ErrorState(errorMessage = "Unknown error occurred: ${error?.message}")
            }

            currentState.copy(errorState = newErrorState)
        }
    }


    companion object {
        const val TAG: String = "ViewModel"
    }
}

data class TeammatesUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorState: ErrorState = ErrorState(),
    val currentUser: User = User(),
    val questionnaires: List<Questionnaire> = emptyList(),
    val likedQuestionnaires: List<Questionnaire> = emptyList(),
    val userQuestionnaires: List<Questionnaire> = emptyList()
)

data class ErrorState(
    val isNetworkError: Boolean = false,
    val errorCode: Int = 0,
    val errorMessage: String = ""
)

sealed class UiEvent {
    data class ShowError(val message: String) : UiEvent()
    data object QuestionnaireCreated : UiEvent()
    data object LoggedOut : UiEvent()
}