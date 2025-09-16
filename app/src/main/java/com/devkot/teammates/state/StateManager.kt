package com.devkot.teammates.state

import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.model.enums.AuthState
import com.devkot.teammates.domain.model.enums.ContentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StateManager @Inject constructor() {

    private val _authState = MutableStateFlow(AuthState.INITIAL)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()


    private val _likedQuestionnairesState = MutableStateFlow(ContentState.INITIAL)
    val likedQuestionnairesState: StateFlow<ContentState> = _likedQuestionnairesState.asStateFlow()

    private val _likedAuthorsState = MutableStateFlow(ContentState.INITIAL)
    val likedAuthorsState: StateFlow<ContentState> = _likedAuthorsState.asStateFlow()

    private val _userQuestionnairesState = MutableStateFlow(ContentState.INITIAL)
    val userQuestionnairesState: StateFlow<ContentState> = _userQuestionnairesState.asStateFlow()

    private val _selectedQuestionnaireState = MutableStateFlow(ContentState.INITIAL)
    val selectedQuestionnaireState: StateFlow<ContentState> =
        _selectedQuestionnaireState.asStateFlow()

    private val _authorState = MutableStateFlow(ContentState.INITIAL)
    val authorState: StateFlow<ContentState> = _authorState.asStateFlow()

    private val _userProfileInfoState = MutableStateFlow(ContentState.INITIAL)
    val userProfileInfoState: StateFlow<ContentState> = _userProfileInfoState.asStateFlow()

    private val _newQuestionnaireState = MutableStateFlow(ContentState.INITIAL)
    val newQuestionnaireState: StateFlow<ContentState> = _newQuestionnaireState.asStateFlow()


    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()


    private val _questionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val questionnaires: StateFlow<List<Questionnaire>> = _questionnaires.asStateFlow()

    private val _likedQuestionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val likedQuestionnaires: StateFlow<List<Questionnaire>> = _likedQuestionnaires.asStateFlow()

    private val _likedAuthors = MutableStateFlow<List<User>>(emptyList())
    val likedAuthors: StateFlow<List<User>> = _likedAuthors.asStateFlow()

    private val _userQuestionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val userQuestionnaires: StateFlow<List<Questionnaire>> = _userQuestionnaires.asStateFlow()


    private val _selectedAuthor = MutableStateFlow(User())
    val selectedAuthor: StateFlow<User> = _selectedAuthor.asStateFlow()

    private val _selectedQuestionnaire = MutableStateFlow(Questionnaire())
    val selectedQuestionnaire: StateFlow<Questionnaire> = _selectedQuestionnaire.asStateFlow()

    private val _selectedAuthorQuestionnaires = MutableStateFlow<List<Questionnaire>>(emptyList())
    val selectedAuthorQuestionnaires: StateFlow<List<Questionnaire>> =
        _selectedAuthorQuestionnaires.asStateFlow()


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
    fun updateLikedAuthors(likedAuthors: List<User>) {
        _likedAuthors.value = likedAuthors
    }

    fun updateQuestionnaire(questionnaire: Questionnaire) {
        _questionnaires.value = _questionnaires.value.map {
            if (it.questionnaireId == questionnaire.questionnaireId) questionnaire else it
        }
        _likedQuestionnaires.value = _likedQuestionnaires.value.map {
            if (it.questionnaireId == questionnaire.questionnaireId) questionnaire else it
        }
        _userQuestionnaires.value = _userQuestionnaires.value.map {
            if (it.questionnaireId == questionnaire.questionnaireId) questionnaire else it
        }
        _selectedQuestionnaire.value = questionnaire
    }

    fun deleteQuestionnaire(questionnaireId: String) {
        _questionnaires.value = _questionnaires.value.filter { (it.questionnaireId != questionnaireId) }
        _likedQuestionnaires.value = _likedQuestionnaires.value.filter { (it.questionnaireId != questionnaireId) }
        _userQuestionnaires.value = _userQuestionnaires.value.filter { (it.questionnaireId != questionnaireId) }
        _selectedQuestionnaire.value = Questionnaire()
    }

    fun updateAuthState(authState: AuthState) {
        _authState.value = authState
    }
    fun updateLikedQuestionnairesState(contentState: ContentState) {
        _likedQuestionnairesState.value = contentState
    }
    fun updateLikedAuthorsState(contentState: ContentState) {
        _likedAuthorsState.value = contentState
    }
    fun updateUserQuestionnairesState(contentState: ContentState) {
        _userQuestionnairesState.value = contentState
    }
    fun updateSelectedQuestionnaireState(contentState: ContentState) {
        _selectedQuestionnaireState.value = contentState
    }
    fun updateAuthorState(contentState: ContentState) {
        _authorState.value = contentState
    }
    fun updateUserProfileInfoState(contentState: ContentState) {
        _userProfileInfoState.value = contentState
    }
    fun updateNewQuestionnaireState(contentState: ContentState) {
        _newQuestionnaireState.value = contentState
    }

    fun updateContentsState(contentState: ContentState) {
        updateLikedQuestionnairesState(contentState)
        updateLikedAuthorsState(contentState)
        updateUserQuestionnairesState(contentState)
        updateSelectedQuestionnaireState(contentState)
        updateAuthorState(contentState)
        updateUserProfileInfoState(contentState)
        updateNewQuestionnaireState(contentState)
    }

}