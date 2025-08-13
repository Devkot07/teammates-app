package com.devkot.teammates.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devkot.teammates.R
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.model.enums.AuthState
import com.devkot.teammates.domain.model.enums.ContentState
import com.devkot.teammates.domain.usecase.LikeAuthorUseCase
import com.devkot.teammates.domain.usecase.LoadAuthorProfileUseCase
import com.devkot.teammates.domain.usecase.LoadLikedAuthorsUseCase
import com.devkot.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.devkot.teammates.domain.usecase.UnlikeAuthorUseCase
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
class AuthorViewModel @Inject constructor(

    private val stateManager: StateManager,

    private val errorHandler: ErrorHandler,


    private val loadQuestionnairesUseCase: LoadQuestionnairesUseCase,

    private val loadAuthorProfileUseCase: LoadAuthorProfileUseCase,

    private val loadLikedAuthorsUseCase: LoadLikedAuthorsUseCase,
    private val likeAuthorUseCase: LikeAuthorUseCase,
    private val unlikeAuthorUseCase: UnlikeAuthorUseCase,


    ) : ViewModel() {

    init {
        viewModelScope.launch {
            stateManager.authState.collect { authState ->
                Log.d(TAG, "AUTH STATE CHANGED: $authState")
                if (authState == AuthState.AUTHENTICATED) loadLikedAuthors()
            }
        }
    }

    private val _authorUiEvent = MutableSharedFlow<AuthorUiEvent>(extraBufferCapacity = 1)
    val authorUiEvent: SharedFlow<AuthorUiEvent> = _authorUiEvent


    val selectedAuthor = stateManager.selectedAuthor
    val selectedQuestionnaire = stateManager.selectedQuestionnaire
    val selectedAuthorQuestionnaires = stateManager.selectedAuthorQuestionnaires

    val likedAuthors = stateManager.likedAuthors

    val authorState = stateManager.authorState

    fun loadAuthor(authorId: String) {
        if (selectedAuthor.value.publicId != authorId) {
            viewModelScope.launch {
                stateManager.updateAuthorState(ContentState.LOADING)
                stateManager.updateSelectedAuthor(User())
                loadAuthorProfileUseCase(authorId).onSuccess { author ->

                    stateManager.updateSelectedAuthor(author)

                    loadQuestionnairesUseCase(
                        game = null,
                        limit = 100,
                        authorId = author.publicId
                    ).onSuccess { (authorQuestionnaires, _) ->

                        stateManager.updateSelectedAuthorQuestionnaires(authorQuestionnaires)
                        stateManager.updateAuthorState(ContentState.LOADED)

                    }.onFailure { throwable ->

                        stateManager.updateAuthorState(ContentState.ERROR)
                        errorHandler.handleError(throwable)
                    }
                }.onFailure { throwable ->
                    stateManager.updateAuthorState(ContentState.ERROR)
                    errorHandler.handleError(throwable)
                }
            }
        }
    }

    fun updateSelectedQuestionnaire(questionnaire: Questionnaire) {
        stateManager.updateSelectedQuestionnaire(questionnaire)
    }

    private fun loadLikedAuthors() {
        viewModelScope.launch {
            stateManager.updateLikedAuthorsState(ContentState.LOADING)
            loadLikedAuthorsUseCase().onSuccess { result ->
                Log.d(TAG, result.toString())
                stateManager.updateLikedAuthors(result)
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                stateManager.updateLikedAuthorsState(ContentState.ERROR)
                errorHandler.handleError(throwable)
            }
        }

    }

    fun likeAuthor(likedAuthor: User) {
        viewModelScope.launch {
            stateManager.updateAuthorState(ContentState.LOADING)
            likeAuthorUseCase(likedUserId = likedAuthor.publicId).onSuccess { result ->
                Log.d(TAG, result.toString())
                val likedAuthors = likedAuthors.value.plus(likedAuthor)
                stateManager.updateLikedAuthors(likedAuthors = likedAuthors)
                stateManager.updateAuthorState(ContentState.LOADED)
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        R.string.you_subscribe,
                        listOf(likedAuthor.nickname)
                    )
                )
                _authorUiEvent.tryEmit(AuthorUiEvent.AuthorSubscribed)
            }.onFailure { throwable ->
                Log.d(TAG, throwable.toString())
                stateManager.updateAuthorState(ContentState.ERROR)
                errorHandler.handleError(throwable)
            }
        }
    }

    fun unlikeAuthor(unlikedAuthor: User) {
        viewModelScope.launch {
            stateManager.updateAuthorState(ContentState.LOADING)
            unlikeAuthorUseCase(unlikedUserId = unlikedAuthor.publicId).onSuccess { result ->
                Log.d(TAG, result.toString())
                val likedAuthors = likedAuthors.value.minus(unlikedAuthor)
                stateManager.updateLikedAuthors(likedAuthors = likedAuthors)
                stateManager.updateAuthorState(ContentState.LOADED)
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        R.string.you_unsubscribe,
                        listOf(unlikedAuthor.nickname)
                    )
                )
                _authorUiEvent.tryEmit(AuthorUiEvent.AuthorUnsubscribed)
            }.onFailure { throwable ->
                Log.e(TAG, throwable.toString())
                stateManager.updateAuthorState(ContentState.ERROR)
                errorHandler.handleError(throwable)
            }
        }
    }

    companion object {
        const val TAG = "AuthorVM"
    }
}


sealed class AuthorUiEvent {
    data object AuthorSubscribed : AuthorUiEvent()
    data object AuthorUnsubscribed : AuthorUiEvent()
}