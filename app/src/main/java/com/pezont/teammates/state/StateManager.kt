package com.pezont.teammates.state

import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.model.enums.AuthState
import com.pezont.teammates.domain.model.enums.ContentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StateManager @Inject constructor() {

    private val _authState = MutableStateFlow(AuthState.INITIAL)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _contentState = MutableStateFlow(ContentState.INITIAL)
    val contentState: StateFlow<ContentState> = _contentState.asStateFlow()

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()


    private val _questionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val questionnaires: StateFlow<List<Questionnaire>> = _questionnaires.asStateFlow()

    private val _likedQuestionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val likedQuestionnaires: StateFlow<List<Questionnaire>> = _likedQuestionnaires.asStateFlow()

    private val _userQuestionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val userQuestionnaires: StateFlow<List<Questionnaire>> = _userQuestionnaires.asStateFlow()


    private val _selectedAuthor = MutableStateFlow(User())
    val selectedAuthor: StateFlow<User> = _selectedAuthor.asStateFlow()

    private val _selectedQuestionnaire = MutableStateFlow(Questionnaire())
    val selectedQuestionnaire: StateFlow<Questionnaire> = _selectedQuestionnaire.asStateFlow()

    private val _selectedAuthorQuestionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val selectedAuthorQuestionnaires: StateFlow<List<Questionnaire>> = _selectedAuthorQuestionnaires.asStateFlow()






    fun updateAuthState(authState: AuthState) {
        _authState.value = authState
    }

    fun updateContentState(contentState: ContentState) {
        _contentState.value = contentState
    }

    fun updateUser(user: User) {
        _user.value = user
    }

    fun updateQuestionnaires(questionnaires: List<Questionnaire>) {
        _questionnaires.value = questionnaires
    }
    fun updateLikedQuestionnaires(likedQuestionnaires: List<Questionnaire>) {
        _likedQuestionnaires.value = likedQuestionnaires
    }
    fun updateUserQuestionnaires(userQuestionnaires: List<Questionnaire>) {
        _userQuestionnaires.value = userQuestionnaires
    }

    fun updateSelectedAuthor(selectedAuthor: User) {
        _selectedAuthor.value = selectedAuthor
    }

    fun updateSelectedQuestionnaire(selectedQuestionnaire: Questionnaire) {
        _selectedQuestionnaire.value = selectedQuestionnaire
    }
    fun updateSelectedAuthorQuestionnaires(selectedAuthorQuestionnaires: List<Questionnaire>) {
        _selectedAuthorQuestionnaires.value = selectedAuthorQuestionnaires
    }

}