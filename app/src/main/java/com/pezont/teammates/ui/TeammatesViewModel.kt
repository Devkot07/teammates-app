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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _questionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val questionnaires: StateFlow<List<Questionnaire>> = _questionnaires.asStateFlow()

    private val _likedQuestionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val likedQuestionnaires: StateFlow<List<Questionnaire>> = _likedQuestionnaires.asStateFlow()

    private val _userQuestionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val userQuestionnaires: StateFlow<List<Questionnaire>> = _userQuestionnaires.asStateFlow()

    private val _authToastCode = MutableSharedFlow<Int?>(extraBufferCapacity = 1)
    val authToastCode: SharedFlow<Int?> = _authToastCode

    private val _questionnairesToastCode = MutableSharedFlow<Int?>(extraBufferCapacity = 1)
    val questionnairesToastCode: SharedFlow<Int?> = _questionnairesToastCode

    private val _errorState = MutableStateFlow(ErrorState())
    val errorState: StateFlow<ErrorState> = _errorState.asStateFlow()

    data class ErrorState(
        val isNetworkError: Boolean = false,
        val errorCode: Int = 0,
        val errorMessage: String = ""
    )

    init {
        viewModelScope.launch {
            checkAuthentication()
        }
    }

    private suspend fun checkAuthentication() {
        _isLoading.value = true
        userDataRepository.user.collect { user ->
            val isLoggedIn = user.publicId != null

            if (isLoggedIn) {
                _currentUser.value = user
                _isAuthenticated.value = true
                fetchQuestionnaires()
            } else {
                _isAuthenticated.value = false
            }
            _isLoading.value = false
        }
    }

    fun login(nickname: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authResult = authRepository.login(nickname, password)
                if (authResult.isSuccess) {
                    val response = authResult.getOrNull()
                    response?.let {
                        userDataRepository.saveAccessToken(it.accessToken)
                        userDataRepository.saveRefreshToken(it.refreshToken)
                        userDataRepository.saveUser(it.user)
                        _currentUser.value = it.user
                        _isAuthenticated.value = true
                        _isLoading.value = false
                    }
                } else {
                    handleError(authResult.exceptionOrNull())
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchQuestionnaires(game: Games? = null, page: Int = 1, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val user = userDataRepository.user.first()
                if (user.publicId == null) {
                    _isAuthenticated.value = false
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
                    response?.let {
                        if (page == 1) {
                            _questionnaires.value = it
                        } else {
                            _questionnaires.value += it
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
                    _isAuthenticated.value = false
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
                        _userQuestionnaires.value = it
                    }
                } else {
                    handleError(questionnairesResult.exceptionOrNull())
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
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
            _isLoading.value = true
            try {
                val user = userDataRepository.user.first()
                if (user.publicId == null) {
                    _isAuthenticated.value = false
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
                    // Implement success handling
                    // Refresh user questionnaires
                    loadUserQuestionnaires()
                    _questionnairesToastCode.tryEmit(200) // Success toast
                } else {
                    handleError(createQuestionnaireResult.exceptionOrNull())
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userDataRepository.saveAccessToken("")
            userDataRepository.saveRefreshToken("")
            userDataRepository.saveUser(User())
            _currentUser.value = User()
            _isAuthenticated.value = false
            _questionnaires.value = emptyList()
            _likedQuestionnaires.value = emptyList()
            _userQuestionnaires.value = emptyList()
            _authToastCode.tryEmit(1) // Logout toast
        }
    }

    fun clearError() {
        _errorState.value = ErrorState()
    }

    private fun handleError(error: Throwable?) {
        when (error) {
            is IOException -> {
                _errorState.value = ErrorState(
                    isNetworkError = true,
                    errorMessage = "Network error. Please check your connection."
                )
            }
            is HttpException -> {
                val errorCode = error.code()
                if (errorCode == 401) {
                    _isAuthenticated.value = false
                    _authToastCode.tryEmit(errorCode)
                } else {
                    _errorState.value = ErrorState(
                        errorCode = errorCode,
                        errorMessage = "Server error: ${error.message()}"
                    )
                    _questionnairesToastCode.tryEmit(errorCode)
                }
            }
            else -> {
                _errorState.value = ErrorState(
                    errorMessage = "Unknown error occurred: ${error?.message}"
                )
            }
        }
    }

    companion object {
        const val TAG: String = "ViewModel"
    }
}