package com.pezont.teammates.utils

import android.util.Log
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.enums.AuthState
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.usecase.LogoutUseCase
import com.pezont.teammates.state.StateManager
import com.pezont.teammates.ui.snackbar.SnackbarController
import com.pezont.teammates.ui.snackbar.SnackbarEvent
import com.pezont.teammates.viewmodel.TeammatesViewModel.Companion.TAG
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(
    private val logoutUseCase: LogoutUseCase, private val stateManager: StateManager
) {

    suspend fun handleError(error: Throwable) {
        Log.e(TAG, "error: $error")
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

                    error.code() == 400 -> {
                        SnackbarController.sendEvent(
                            SnackbarEvent(R.string.invalid_request_error)
                        )
                    }

                    errorBody?.contains(
                        "Not authenticated", ignoreCase = true
                    ) == true || error.code() == 401 -> {
                        SnackbarController.sendEvent(
                            SnackbarEvent(R.string.authorization_failed)
                        )

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
                        }

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