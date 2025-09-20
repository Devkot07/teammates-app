package com.devkot.teammates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devkot.teammates.R
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.model.enums.AuthState
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.domain.usecase.CheckAuthenticationUseCase
import com.devkot.teammates.domain.usecase.LoadUserUseCase
import com.devkot.teammates.domain.usecase.LoginUseCase
import com.devkot.teammates.domain.usecase.LogoutUseCase
import com.devkot.teammates.state.StateManager
import com.devkot.teammates.ui.snackbar.SnackbarController
import com.devkot.teammates.ui.snackbar.SnackbarEvent
import com.devkot.teammates.utils.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(

    private val stateManager: StateManager,

    private val errorHandler: ErrorHandler,

    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkAuthenticationUseCase: CheckAuthenticationUseCase,
    private val loadUserUseCase: LoadUserUseCase,

    ) : ViewModel() {

    init {
        viewModelScope.launch { checkAuthentication() }
    }

    val authState = stateManager.authState

    private val _authUiEvent = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val authUiEvent: SharedFlow<AuthUiEvent> = _authUiEvent


    private suspend fun checkAuthentication() {
        stateManager.updateAuthState(AuthState.LOADING)
        runCatching {
            checkAuthenticationUseCase()
        }.onSuccess { authenticated ->
            val newAuthState =
                if (authenticated) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
            stateManager.updateUser(if (authenticated) loadUserUseCase() else User())
            stateManager.updateAuthState(newAuthState)

        }.onFailure { throwable ->
            stateManager.updateAuthState(AuthState.UNAUTHENTICATED)
            errorHandler.handleError(throwable)
        }
    }


    fun login(nickname: String, password: String) {
        viewModelScope.launch {
            stateManager.updateAuthState(AuthState.LOADING)
            loginUseCase(nickname, password).onSuccess {
                stateManager.updateUser(loadUserUseCase())
                stateManager.updateAuthState(AuthState.AUTHENTICATED)

                _authUiEvent.tryEmit(AuthUiEvent.LoggedIn)
            }.onFailure {
                stateManager.updateAuthState(AuthState.UNAUTHENTICATED)
                SnackbarController.sendEvent(SnackbarEvent(R.string.you_aren_t_logged_in))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            stateManager.updateAuthState(AuthState.LOADING)
            logoutUseCase().onSuccess {
                stateManager.updateUser(User())
                stateManager.updateAuthState(AuthState.UNAUTHENTICATED)
                stateManager.updateContentsState(ContentState.INITIAL)
                stateManager.updateQuestionnaires(emptyList())
                stateManager.updateLikedQuestionnaires(emptyList())
                stateManager.updateUserQuestionnaires(emptyList())

                stateManager.updateSelectedAuthor(User())
                stateManager.updateSelectedQuestionnaire(Questionnaire())
                stateManager.updateSelectedAuthorQuestionnaires(emptyList())

                stateManager.updateLikedAuthors(emptyList())

                _authUiEvent.tryEmit(AuthUiEvent.LoggedOut)
                SnackbarController.sendEvent(SnackbarEvent(R.string.logged_out))
            }.onFailure {
                stateManager.updateAuthState(AuthState.INITIAL)
            }
        }
    }
}


sealed class AuthUiEvent {
    data object LoggedOut : AuthUiEvent()
    data object LoggedIn : AuthUiEvent()
}