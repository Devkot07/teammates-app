package com.pezont.teammates.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pezont.teammates.TeammatesApplication
import com.pezont.teammates.data.AuthRepository
import com.pezont.teammates.data.QuestionnairesRepository
import com.pezont.teammates.data.UserDataRepository
import com.pezont.teammates.dummy.UserDummy
import com.pezont.teammates.fake.FakeQuestionnaireRepository
import com.pezont.teammates.models.ContentType
import com.pezont.teammates.models.Games
import com.pezont.teammates.models.Questionnaire
import com.pezont.teammates.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException


class TeammatesViewModel(

    private val fakeQuestionnairesRepository: FakeQuestionnaireRepository,
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository,
    //private val userDummyRepository: UserDummyRepository,
    private val authRepository: AuthRepository,

    ) : ViewModel() {


    private val _teammatesUiState: MutableStateFlow<TeammatesUiState> = MutableStateFlow(TeammatesUiState.Loading)
    val teammatesUiState: StateFlow<TeammatesUiState> = _teammatesUiState.asStateFlow()

    private val _loginToastCode = MutableSharedFlow<Int?>(extraBufferCapacity = 1)
    val loginToastCode: SharedFlow<Int?> = _loginToastCode

    private var isInitialized = false

    init {
        viewModelScope.launch {
            userDataRepository.user.collect { user ->

                Log.d(TAG, "publicId: ${user.publicId}")
                Log.d(TAG, ":====")
                when (user.publicId) {
                    null -> _teammatesUiState.value = TeammatesUiState.Login(true)
                    else -> {
                        if (!isInitialized) {
                            _teammatesUiState.value =
                                TeammatesUiState.Home(user = user, questionnaires = listOf())
                            isInitialized = true
                        } else {
                            _teammatesUiState.value = TeammatesUiState.Login(false)

                        }
                    }
                }
            }
        }
    }

    fun initState() {
        viewModelScope.launch {
            _teammatesUiState.value = TeammatesUiState.Loading
            userDataRepository.user.collect { user ->
                when (user.publicId) {
                    null -> _teammatesUiState.value = TeammatesUiState.Login(true)
                    else -> {
                        if (!isInitialized) {
                            _teammatesUiState.value =
                                TeammatesUiState.Home(user = user, questionnaires = listOf())
                            isInitialized = true
                        } else {
                            _teammatesUiState.value = TeammatesUiState.Login(false)
                        }
                    }
                }
            }
        }
    }

    // TODO clear
    fun tryGetQuestionnairesByPageAndGame(
        teammatesUiState: TeammatesUiState.Home,
        game: Games? = null,
        page: Int = 1,
        limit: Int = 10
    ) {

        viewModelScope.launch {
            val questionnairesResult = questionnairesRepository.getQuestionnairesFromRepo(
                token = userDataRepository.accessToken.first(),
                userId = userDataRepository.user.first().publicId!!,
                page = page,
                limit = limit,
                game = game,
                authorId = null,
                questionnaireId = null
            )
            if (questionnairesResult.isSuccess) {
                val response = questionnairesResult.getOrNull()
                Log.d(TAG, "Fetched questionnaires: $response")

                response?.let {

                    Log.d(TAG, "Count ${response.size}")
                    _teammatesUiState.value = TeammatesUiState.Home(
                        user = userDataRepository.user.first(),
                        questionnaires = teammatesUiState.questionnaires + response,
                    )

                }
            } else {
                val error = questionnairesResult.exceptionOrNull()
                Log.e(TAG, "Failed to fetch questionnaires: $error")
                when (error) {
                    is IOException -> {
                        _teammatesUiState.value = TeammatesUiState.ErrorNetwork
                    }

                    is HttpException -> {
                        val incorrectAccessToken = error.code() == 401
                        Log.e(TAG, "incorrectAccessToken: $incorrectAccessToken")
                        _teammatesUiState.value =
                            TeammatesUiState.Login(false, error.code())
                            _loginToastCode.tryEmit( error.code())
                            //sendLoginErrorToast(error.code(), context)

                    }

                    else -> _teammatesUiState.value = TeammatesUiState.Login(false, 0)
                }
            }


        }

    }



//    fun tryLoginWithDummyAccessToken(isLoggedOut: Boolean, token: String) {
//        viewModelScope.launch {
//            Log.e(TAG, "---$isLoggedOut")
//            _teammatesUiState.value = TeammatesUiState.Loading
//            if(!isLoggedOut) {
//                val userResult = userDummyRepository.getCurrentUser(token)
//                if (userResult.isSuccess) {
//                    val response = userResult.getOrNull()
//                    Log.d(TAG, "Logged in as: $response")
//
//                    response?.let {
//
//                        //_teammatesUiState.value = TeammatesUiState.Home( userDummy = it, questionnaires = listOf())
//                        //tryGetQuestionnairesByGame(false)
//                    }
//                } else {
//                    val error = userResult.exceptionOrNull()
//                    Log.e(TAG, "Failed to fetch user: $error")
//                    when (error) {
//                        is IOException -> {
//                            _teammatesUiState.value = TeammatesUiState.ErrorNetwork
//                        }
//                        is HttpException -> {
//                            val incorrectAccessToken = error.code() == 401
//                            Log.e(TAG, "incorrectAccessToken: $incorrectAccessToken")
//                            _teammatesUiState.value =
//                                TeammatesUiState.Login(false, error.code())
//                        }
//
//                        else -> _teammatesUiState.value = TeammatesUiState.Login(false, 0)
//
//
//                    }
//
//
//                }
//            } else { _teammatesUiState.value = TeammatesUiState.Login(false, 0)}
//
//        }
//    }
//    fun tryLoginWithInfoInDummy(isLoggedOut:Boolean, username: String, password: String) {

//        viewModelScope.launch {
//            _teammatesUiState.value = TeammatesUiState.Loading
//            if(isLoggedOut) userDataRepository.saveAccessToken("0")
//            else {
//
//                val result = userDummyRepository.login(username, password)
//                if (result.isSuccess) {
//                    val response = result.getOrNull()
//                    Log.d(TAG, "Logged in as: ${response?.accessToken}")
//
//                    response?.let {
//                        withContext(Dispatchers.IO) {
//                            it.accessToken.let { token -> userDataRepository.saveAccessToken(token) }
//                            it.refreshToken.let { refreshToken ->
//                                userDataRepository.saveRefreshToken(
//                                    refreshToken
//                                )
//                            }
//                        }
//                        userDataRepository.accessToken.collect { token ->
//                            when (token) {
//                                "-1" -> tryLoginWithDummyAccessToken(true, token)
//                                else -> tryLoginWithDummyAccessToken(false, token)
//                            }
//                        }
//                    }
//                } else {
//                    val error = result.exceptionOrNull()
//                    Log.e(TAG, "Login failed: $error")
//
//                    when (error) {
//                        is IOException -> {
//                            _teammatesUiState.value = TeammatesUiState.ErrorNetwork
//                        }
//
//                        is HttpException -> {
//                            _teammatesUiState.value =
//                                TeammatesUiState.Login(false, error.code())
//                        }
//
//                        else -> _teammatesUiState.value = TeammatesUiState.Login(false, 0)
//
//
//                    }
//                }
//
//            }
//        }
//    }


    fun tryLoginWithInfoInTeammates(isLoggedOut: Boolean, nickname: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "user: $isLoggedOut")
            _teammatesUiState.value = TeammatesUiState.Loading
            val result = authRepository.login(nickname, password)
            if (result.isSuccess) {
                val response = result.getOrNull()
                Log.d(TAG, "accessToken: ${response?.accessToken}")
                Log.d(TAG, "refreshToken: ${response?.refreshToken}")
                Log.d(TAG, "user: ${response?.user}")

                response?.let {

                    it.accessToken.let { accessToken ->
                        Log.i(TAG, accessToken)
                        userDataRepository.saveAccessToken(accessToken)
                    }
                    it.refreshToken.let { refreshToken ->
                        Log.i(TAG, refreshToken)
                        userDataRepository.saveRefreshToken(refreshToken)
                    }
                    it.user.let { user -> userDataRepository.saveUser(user) }



                }

                _teammatesUiState.value = TeammatesUiState.Home(
                    user = userDataRepository.user.first(),
                    questionnaires = listOf(),
                )
                tryGetQuestionnairesByPageAndGame(TeammatesUiState.Home(
                    user = userDataRepository.user.first(),
                    questionnaires = listOf(),
                ))

            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Login failed: $error")
                when (error) {
                    is IOException -> {
                        _teammatesUiState.value = TeammatesUiState.ErrorNetwork
                    }

                    is HttpException -> {
                        _teammatesUiState.value = TeammatesUiState.Login(false, error.code())
                        _loginToastCode.tryEmit( error.code())

                    }

                    else -> _teammatesUiState.value = TeammatesUiState.Login(false, 0)
                }
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
            Log.d(TAG, userDataRepository.accessToken.first())
            val questionnaireResult = questionnairesRepository.createQuestionnaire(
                token = userDataRepository.accessToken.first(),
                header = header,
                game = selectedGame,
                description = description,
                authorId = 1,
                image = image,
            ) //TODO end creating, navigate
            Log.d(TAG, "$questionnaireResult")
        }
    }


    fun getFakeQuestionnaires() {
        viewModelScope.launch {
            fakeQuestionnairesRepository.getRecommendedQuestionnaires()

        }
    }

//    suspend fun getNextFakeQuestionnaires(
//        teammatesUiState: TeammatesUiState.Home,
//        i: Int,
//    ) {
//        delay(2000L)
//        val newQuestionnaires = fakeQuestionnairesRepository.getNextRecommendedQuestionnaires(i)
//        Log.d(TAG, "Fetched new questionnaires: $newQuestionnaires")
//        Log.d(TAG, "===================: $i")
//        _teammatesUiState.value = TeammatesUiState.Home(
//            currentContent = teammatesUiState.currentContent,
//            questionnaires = teammatesUiState.questionnaires + newQuestionnaires,
//            user = teammatesUiState.user,
//        )
//    }


    fun clearUserData() {
        viewModelScope.launch {
            Log.e(TAG, "cleaning")
            withContext(Dispatchers.IO) {
                userDataRepository.saveAccessToken("-1")
                userDataRepository.saveRefreshToken("-1")
                userDataRepository.saveUser(User())
            }
            _teammatesUiState.value = TeammatesUiState.Login(true, 1)
            _loginToastCode.tryEmit( 1)


        }

    }

    fun updateCurrentContent(contentType: ContentType) {
        _teammatesUiState.update { currentState ->
            when (currentState) {
                is TeammatesUiState.Home -> currentState.copy(currentContent = contentType)
                else -> currentState
            }
        }
    }


    companion object {
        const val TAG: String = "ViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TeammatesApplication)
                TeammatesViewModel(
                    FakeQuestionnaireRepository(),
                    application.container.questionnairesRepository,
                    application.userDataRepository,
                    //application.container.userDummyRepository,
                    application.container.authRepository

                )
            }
        }
    }
}


sealed interface TeammatesUiState {

    data object Loading : TeammatesUiState

    data class Login(
        val isLoggedOut: Boolean,
        val statusResponse: Int = 0,
    ) : TeammatesUiState

    data class Error(
        val statusResponse: Int
    ) : TeammatesUiState

    data class Home(
        val currentContent: ContentType = ContentType.Home,
        val userDummy: UserDummy = UserDummy(),
        val user: User,
        var questionnaires: List<Questionnaire>
    ) : TeammatesUiState

    data object ErrorNetwork : TeammatesUiState


}