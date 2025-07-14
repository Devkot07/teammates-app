package com.pezont.teammates.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.enums.AuthState
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.usecase.CheckAuthenticationUseCase
import com.pezont.teammates.domain.usecase.LoadUserUseCase
import com.pezont.teammates.domain.usecase.LoginUseCase
import com.pezont.teammates.domain.usecase.LogoutUseCase
import com.pezont.teammates.state.StateManager
import com.pezont.teammates.ui.snackbar.SnackbarController
import com.pezont.teammates.ui.snackbar.SnackbarEvent
import com.pezont.teammates.utils.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
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
            checkAuthenticationUseCase().first()
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
                stateManager.updateContentState(ContentState.INITIAL)
                stateManager.updateQuestionnaires(emptyList())
                stateManager.updateLikedQuestionnaires(emptyList())
                stateManager.updateUserQuestionnaires(emptyList())

                stateManager.updateSelectedAuthor(User())
                stateManager.updateSelectedQuestionnaire(Questionnaire())
                stateManager.updateSelectedAuthorQuestionnaires(emptyList())

                stateManager.updateLikedAuthors(emptyList())

                _authUiEvent.tryEmit(AuthUiEvent.LoggedOut)
                SnackbarController.sendEvent(SnackbarEvent(R.string.logged_out))
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                stateManager.updateAuthState(AuthState.INITIAL)
            }
        }
    }

    companion object {
        const val TAG = "AuthVM"
    }

}


sealed class AuthUiEvent {
    data object LoggedOut : AuthUiEvent()
    data object LoggedIn : AuthUiEvent()
}