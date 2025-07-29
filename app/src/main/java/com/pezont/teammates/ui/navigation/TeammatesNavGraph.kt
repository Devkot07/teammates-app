package com.pezont.teammates.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pezont.teammates.domain.model.enums.AuthState
import com.pezont.teammates.domain.model.enums.BottomNavItem
import com.pezont.teammates.ui.ObserveAsEvents
import com.pezont.teammates.ui.ObserveState
import com.pezont.teammates.ui.components.LoadingItemWithText
import com.pezont.teammates.ui.components.TeammatesBackHandler
import com.pezont.teammates.ui.components.TeammatesTopAppBar
import com.pezont.teammates.ui.screen.LoginScreen
import com.pezont.teammates.ui.screen.TeammatesHomeScreen
import com.pezont.teammates.ui.screen.author.AuthorProfileScreen
import com.pezont.teammates.ui.screen.questionnaire.LikedQuestionnairesScreen
import com.pezont.teammates.ui.screen.questionnaire.QuestionnaireCreateScreen
import com.pezont.teammates.ui.screen.questionnaire.QuestionnaireDetailsScreen
import com.pezont.teammates.ui.screen.questionnaire.UserQuestionnairesScreen
import com.pezont.teammates.ui.screen.user.UserProfileEditScreen
import com.pezont.teammates.ui.screen.user.UserProfileScreen
import com.pezont.teammates.viewmodel.AuthUiEvent
import com.pezont.teammates.viewmodel.AuthViewModel
import com.pezont.teammates.viewmodel.AuthorUiEvent
import com.pezont.teammates.viewmodel.AuthorViewModel
import com.pezont.teammates.viewmodel.QuestionnaireUiEvent
import com.pezont.teammates.viewmodel.QuestionnairesViewModel
import com.pezont.teammates.viewmodel.UserUiEvent
import com.pezont.teammates.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun TeammatesNavGraph(
    onTabChange: (BottomNavItem) -> Unit,
    navController: NavHostController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    questionnairesViewModel: QuestionnairesViewModel,
    authorViewModel: AuthorViewModel,
    userViewModel: UserViewModel,
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()


    val authState by authViewModel.authState.collectAsState()

    val user by userViewModel.user.collectAsState()


    val questionnaires by questionnairesViewModel.questionnaires.collectAsState()
    val likedQuestionnaires by questionnairesViewModel.likedQuestionnaires.collectAsState()
    val userQuestionnaires by questionnairesViewModel.userQuestionnaires.collectAsState()


    val selectedAuthor by authorViewModel.selectedAuthor.collectAsState()
    val selectedQuestionnaire by authorViewModel.selectedQuestionnaire.collectAsState()
    val selectedAuthorQuestionnaires by authorViewModel.selectedAuthorQuestionnaires.collectAsState()
    val likedAuthors by authorViewModel.likedAuthors.collectAsState()



    ObserveAsEvents(authViewModel.authUiEvent) { event ->
        when (event) {
            is AuthUiEvent.LoggedOut -> {
                navController.navigate(Destinations.Login.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }

            is AuthUiEvent.LoggedIn -> {
                navController.navigate(Destinations.Home.route) {
                    popUpTo(Destinations.Login.route) { inclusive = true }
                }
            }
        }
    }

    ObserveAsEvents(questionnairesViewModel.questionnaireUiEvent) { event ->
        when (event) {
            is QuestionnaireUiEvent.QuestionnaireCreated -> {
                coroutineScope.launch {
                    questionnairesViewModel.loadUserQuestionnaires()
                    navController.navigate(Destinations.UserQuestionnaires.route) {
                        popUpTo(Destinations.Home.route) { inclusive = false }
                    }
                }
            }

            QuestionnaireUiEvent.QuestionnaireLiked -> {}
            QuestionnaireUiEvent.QuestionnaireUnliked -> {}
        }
    }

    ObserveAsEvents(authorViewModel.authorUiEvent) { event ->
        when (event) {
            is AuthorUiEvent.AuthorSubscribed -> {}
            is AuthorUiEvent.AuthorUnsubscribed -> {}
        }
    }

    ObserveAsEvents(userViewModel.userUiEvent) { event ->
        when (event) {
            is UserUiEvent.UserProfileUpdated -> {
                navController.navigate(Destinations.UserProfile.route) {
                    popUpTo(Destinations.Home.route) { inclusive = false }
                }
            }
        }
    }

    ObserveState(authState) {
        when (authState) {
            AuthState.LOADING -> {
                navController.navigate(Destinations.Loading.route)
            }

            AuthState.UNAUTHENTICATED -> {
                if (navController.currentDestination?.route != Destinations.Login.route) {
                    navController.navigate(Destinations.Login.route)
                }
            }

            AuthState.AUTHENTICATED -> {
                if (navController.currentDestination?.route == Destinations.Login.route) {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                }
            }

            else -> {}
        }
    }

    var currentTab by remember { mutableStateOf(BottomNavItem.HOME) }
    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(
        initial = navController.currentBackStackEntry
    ).value?.destination?.route

    NavHost(
        navController = navController,
        startDestination = when (authState) {
            AuthState.LOADING -> Destinations.Loading.route
            AuthState.UNAUTHENTICATED -> Destinations.Login.route
            else -> Destinations.Home.route
        },

        modifier = Modifier.padding(paddingValues),
    ) {
        composable(Destinations.Loading.route) {
            LoadingItemWithText()
        }

        composable(Destinations.Login.route) {
            LoginScreen(onTabChange, authViewModel)
        }

        composable(Destinations.Home.route) {
            onTabChange(BottomNavItem.HOME)

            TeammatesHomeScreen(
                authorViewModel = authorViewModel,
                questionnairesViewModel = questionnairesViewModel,
                questionnaires = questionnaires,
                navigateToQuestionnaireDetails = {
                    navController.navigate(Destinations.QuestionnaireDetails.route)
                },
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(Destinations.Home.titleRes),
                        canNavigateBack = false,
                    )
                }
            )
            TeammatesBackHandler(
                currentRoute = currentRoute,
                onTabChange = { currentTab = it },
                navController = navController,
                context = context
            )


        }

        composable(Destinations.LikedQuestionnaires.route) {
            onTabChange(BottomNavItem.LIKED)

            LikedQuestionnairesScreen(
                likedQuestionnaires = likedQuestionnaires,
                authorViewModel = authorViewModel,
                questionnairesViewModel = questionnairesViewModel,
                navigateToQuestionnaireDetails = {
                    navController.navigate(Destinations.QuestionnaireDetails.route)
                },
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(Destinations.LikedQuestionnaires.titleRes),
                        canNavigateBack = false,
                    )
                }
            )
            TeammatesBackHandler(
                currentRoute = currentRoute,
                onTabChange = { currentTab = it },
                navController = navController,
                context = context
            )
        }

        composable(Destinations.QuestionnaireCreate.route) {
            onTabChange(BottomNavItem.CREATE)
            QuestionnaireCreateScreen(
                questionnairesViewModel = questionnairesViewModel,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(Destinations.QuestionnaireCreate.titleRes),
                        canNavigateBack = false,
                    )
                }
            )
            TeammatesBackHandler(
                currentRoute = currentRoute,
                onTabChange = { currentTab = it },
                navController = navController,
                context = context
            )
        }

        composable(Destinations.UserProfile.route) {
            onTabChange(BottomNavItem.PROFILE)
            UserProfileScreen(
                navigateToMyQuestionnaires = {
                    navController.navigate(Destinations.UserQuestionnaires.route)
                    coroutineScope.launch {
                        questionnairesViewModel.loadUserQuestionnaires()
                    }
                },
                navigateToUserProfileEditScreen = {
                    navController.navigate(Destinations.UserProfileEdit.route)
                },
                authViewModel = authViewModel,
                user = user,
            )
            TeammatesBackHandler(
                currentRoute = currentRoute,
                onTabChange = { currentTab = it },
                navController = navController,
                context = context
            )
        }

        composable(Destinations.UserProfileEdit.route) {
            UserProfileEditScreen(
                user = user,
                userViewModel = userViewModel,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(Destinations.UserProfileEdit.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navController.navigateUp() },
                    )
                },
                navigateUp = { navController.navigateUp() }

            )
            TeammatesBackHandler(
                currentRoute = currentRoute,
                onTabChange = { currentTab = it },
                navController = navController,
                context = context
            )

        }

        composable(Destinations.UserQuestionnaires.route) {
            UserQuestionnairesScreen(
                navigateToQuestionnaireCreate = {
                    navController.navigate(Destinations.QuestionnaireCreate.route)
                    onTabChange(BottomNavItem.CREATE)
                },
                userQuestionnaires = userQuestionnaires,
                navigateToQuestionnaireDetails = {
                    navController.navigate(Destinations.QuestionnaireDetails.route)
                },
                authorViewModel = authorViewModel,
                questionnairesViewModel = questionnairesViewModel,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(Destinations.UserQuestionnaires.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navController.navigateUp() },
                    )
                }
            )
            TeammatesBackHandler(
                currentRoute = currentRoute,
                onTabChange = { currentTab = it },
                navController = navController,
                context = context
            )
        }

        composable(Destinations.QuestionnaireDetails.route) {
            QuestionnaireDetailsScreen(
                author = selectedAuthor,
                questionnaire = selectedQuestionnaire,
                likedQuestionnaires = likedQuestionnaires,
                questionnairesViewModel = questionnairesViewModel,
                navigateToAuthorProfile = {
                    if (user.publicId == selectedQuestionnaire.authorId)
                        navController.navigate(Destinations.UserProfile.route)
                    else
                        navController.navigate(Destinations.AuthorProfile.route)
                },
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(Destinations.QuestionnaireDetails.titleRes),
                        canNavigateBack = true,
                        navigateUp = {
                            questionnairesViewModel.resetSelectedQuestionnaireState()
                            navController.navigateUp()
                        },
                    )
                }
            )
        }
        composable(Destinations.AuthorProfile.route) {
            AuthorProfileScreen(
                authorViewModel = authorViewModel,
                author = selectedAuthor,
                authorQuestionnaires = selectedAuthorQuestionnaires,
                likedAuthors = likedAuthors,
                topBar = {
                    TeammatesTopAppBar(
                        title = selectedAuthor.nickname,
                        canNavigateBack = true,
                        navigateUp = { navController.navigate(Destinations.Home.route) }
                    )
                },
                navigateToQuestionnaireDetails = {
                    navController.navigate(Destinations.QuestionnaireDetails.route)
                },
                modifier = Modifier
            )
        }
    }
}