package com.pezont.teammates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.enums.ContentState
import com.pezont.teammates.domain.usecase.LoadAuthorProfileUseCase
import com.pezont.teammates.domain.usecase.LoadQuestionnairesUseCase
import com.pezont.teammates.state.StateManager
import com.pezont.teammates.utils.ErrorHandler
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



    ) : ViewModel() {


    private val _authorUiEvent = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val authorUiEvent: SharedFlow<AuthUiEvent> = _authorUiEvent



    val selectedAuthor = stateManager.selectedAuthor
    val selectedQuestionnaire = stateManager.selectedQuestionnaire
    val selectedAuthorQuestionnaires = stateManager.selectedAuthorQuestionnaires






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

                        stateManager.updateSelectedAuthorQuestionnaires(authorQuestionnaires)
                        stateManager.updateContentState(ContentState.LOADED)

                    }.onFailure { throwable ->

                        stateManager.updateContentState(ContentState.ERROR)
                        errorHandler.handleError(throwable)
                    }
                }.onFailure { throwable ->
                    stateManager.updateContentState(ContentState.ERROR)
                    errorHandler.handleError(throwable)
                }
            }
        }
    }

    fun updateSelectedQuestionnaire(questionnaire: Questionnaire) {
        stateManager.updateSelectedQuestionnaire(questionnaire)
    }


}


sealed class AuthorUiEvent {
    data object AuthorSubscribed : AuthorUiEvent()
}