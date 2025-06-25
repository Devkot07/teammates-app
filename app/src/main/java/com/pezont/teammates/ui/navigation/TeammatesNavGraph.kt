package com.pezont.teammates.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.pezont.teammates.ui.components.LoadingDestination
import com.pezont.teammates.ui.components.LoadingItemWithText
import com.pezont.teammates.ui.components.TeammatesBackHandler
import com.pezont.teammates.ui.components.TeammatesTopAppBar
import com.pezont.teammates.ui.screen.HomeDestination
import com.pezont.teammates.ui.screen.LoginDestination
import com.pezont.teammates.ui.screen.LoginScreen
import com.pezont.teammates.ui.screen.TeammatesHomeScreen
import com.pezont.teammates.ui.screen.author.AuthorProfileDestination
import com.pezont.teammates.ui.screen.author.AuthorProfileScreen
import com.pezont.teammates.ui.screen.questionnaire.LikedQuestionnairesDestination
import com.pezont.teammates.ui.screen.questionnaire.LikedQuestionnairesScreen
import com.pezont.teammates.ui.screen.questionnaire.QuestionnaireCreateDestination
import com.pezont.teammates.ui.screen.questionnaire.QuestionnaireCreateScreen
import com.pezont.teammates.ui.screen.questionnaire.QuestionnaireDetailsDestination
import com.pezont.teammates.ui.screen.questionnaire.QuestionnaireDetailsScreen
import com.pezont.teammates.ui.screen.questionnaire.UserQuestionnairesDestination
import com.pezont.teammates.ui.screen.questionnaire.UserQuestionnairesScreen
import com.pezont.teammates.ui.screen.user.UserProfileDestination
import com.pezont.teammates.ui.screen.user.UserProfileEditDestination
import com.pezont.teammates.ui.screen.user.UserProfileEditScreen
import com.pezont.teammates.ui.screen.user.UserProfileScreen
import com.pezont.teammates.viewmodel.AuthUiEvent
import com.pezont.teammates.viewmodel.AuthViewModel
import com.pezont.teammates.viewmodel.QuestionnaireUiEvent
import com.pezont.teammates.viewmodel.QuestionnairesViewModel
import com.pezont.teammates.viewmodel.TeammatesViewModel
import com.pezont.teammates.viewmodel.UiEvent

@Composable
fun TeammatesNavGraph(
    onTabChange: (BottomNavItem) -> Unit,
    navController: NavHostController,
    viewModel: TeammatesViewModel,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    questionnairesViewModel: QuestionnairesViewModel,
) {
    val context = LocalContext.current

    val authState by authViewModel.authState.collectAsState()

    val contentState by viewModel.contentState.collectAsState()

    val questionnaires by questionnairesViewModel.questionnaires.collectAsState()
    val likedQuestionnaires by questionnairesViewModel.likedQuestionnaires.collectAsState()
    val userQuestionnaires by questionnairesViewModel.userQuestionnaires.collectAsState()


    val selectedAuthor by viewModel.selectedAuthor.collectAsState()
    val selectedQuestionnaire by viewModel.selectedQuestionnaire.collectAsState()
    val selectedAuthorQuestionnaires by viewModel.selectedAuthorQuestionnaires.collectAsState()



    ObserveAsEvents(authViewModel.authUiEvent) { event ->
        when (event) {
            is AuthUiEvent.LoggedOut -> {
                navController.navigate(LoginDestination.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }

            is AuthUiEvent.LoggedIn -> {
                navController.navigate(HomeDestination.route) {
                    popUpTo(LoginDestination.route) { inclusive = true }
                }
            }
        }
    }

    ObserveAsEvents(questionnairesViewModel.questionnaireUiEvent) { event ->
        when (event) {
            is QuestionnaireUiEvent.QuestionnaireCreated -> {
                questionnairesViewModel.loadUserQuestionnaires()
                navController.navigate(UserQuestionnairesDestination.route) {
                    popUpTo(HomeDestination.route) { inclusive = false }
                }
            }
        }
    }

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.UserProfileUpdated -> {
                navController.navigate(UserProfileDestination.route) {
                    popUpTo(HomeDestination.route) { inclusive = false }
                }
            }
        }
    }

    ObserveState(authState) {
        when (authState) {
            AuthState.LOADING -> {
                navController.navigate(LoadingDestination.route)
            }

            AuthState.UNAUTHENTICATED -> {
                if (navController.currentDestination?.route != LoginDestination.route) {
                    navController.navigate(LoginDestination.route)
                }
            }

            AuthState.AUTHENTICATED -> {
                if (navController.currentDestination?.route == LoginDestination.route) {
                    navController.navigate(HomeDestination.route) {
                        popUpTo(LoginDestination.route) { inclusive = true }
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
            AuthState.LOADING -> LoadingDestination.route
            AuthState.UNAUTHENTICATED -> LoginDestination.route
            else -> HomeDestination.route
        },

        modifier = Modifier.padding(paddingValues),
//        enterTransition = { fadeIn(animationSpec = tween(500)) },
//        exitTransition = { fadeOut(animationSpec = tween(500)) },
    ) {
        composable(LoadingDestination.route) {
            LoadingItemWithText()
        }

        composable(LoginDestination.route) {
            LoginScreen(onTabChange, authViewModel)
        }

        composable(HomeDestination.route) {
            onTabChange(BottomNavItem.HOME)

            TeammatesHomeScreen(
                viewModel = viewModel,
                questionnairesViewModel = questionnairesViewModel,
                questionnaires = questionnaires,
                onRefresh = questionnairesViewModel::loadQuestionnaires,
                navigateToQuestionnaireDetails = {
                    navController.navigate(QuestionnaireDetailsDestination.route)

                },
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(HomeDestination.titleRes),
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

        composable(LikedQuestionnairesDestination.route) {
            onTabChange(BottomNavItem.LIKED)

            LikedQuestionnairesScreen(
                likedQuestionnaires = likedQuestionnaires,
                viewModel = viewModel,
                questionnairesViewModel = questionnairesViewModel,
                navigateToQuestionnaireDetails = {
                    navController.navigate(QuestionnaireDetailsDestination.route)
                },
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(LikedQuestionnairesDestination.titleRes),
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

        composable(QuestionnaireCreateDestination.route) {
            onTabChange(BottomNavItem.CREATE)
            QuestionnaireCreateScreen(
                questionnairesViewModel = questionnairesViewModel,
                contentState = contentState,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(QuestionnaireCreateDestination.titleRes),
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

        composable(UserProfileDestination.route) {
            onTabChange(BottomNavItem.PROFILE)
            val user by viewModel.user.collectAsState()

            UserProfileScreen(
                navigateToMyQuestionnaires = {
                    questionnairesViewModel.loadUserQuestionnaires()
                    navController.navigate(UserQuestionnairesDestination.route)
                },
                navigateToUserProfileEditScreen = {
                    navController.navigate(UserProfileEditDestination.route)
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

        composable(UserProfileEditDestination.route) {
            val user by viewModel.user.collectAsState()



            UserProfileEditScreen(
                contentState = contentState,
                user = user,
                viewModel = viewModel,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(UserProfileEditDestination.titleRes),
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

        composable(UserQuestionnairesDestination.route) {

            UserQuestionnairesScreen(
                navigateToQuestionnaireCreate = {
                    navController.navigate(QuestionnaireCreateDestination.route)
                    onTabChange(BottomNavItem.CREATE)
                },
                userQuestionnaires = userQuestionnaires,
                onRefresh = questionnairesViewModel::loadUserQuestionnaires,
                navigateToQuestionnaireDetails = {
                    navController.navigate(QuestionnaireDetailsDestination.route)
                },
                viewModel = viewModel,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(UserQuestionnairesDestination.titleRes),
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

        composable(QuestionnaireDetailsDestination.route) {
            val user by viewModel.user.collectAsState()

            QuestionnaireDetailsScreen(
                author = selectedAuthor,
                questionnaire = selectedQuestionnaire,

                navigateToAuthorProfile = {
                    if (user.publicId == selectedQuestionnaire.authorId)
                        navController.navigate(UserProfileDestination.route)
                    else
                        navController.navigate(AuthorProfileDestination.route)
                },
                contentState = contentState,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(QuestionnaireDetailsDestination.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navController.navigateUp() },
                    )
                }
            )
        }
        composable(AuthorProfileDestination.route) {
            AuthorProfileScreen(
                viewModel = viewModel,
                contentState = contentState,
                starAction = {},
                author = selectedAuthor,
                authorQuestionnaires = selectedAuthorQuestionnaires,
                topBar = {
                    TeammatesTopAppBar(
                        title = selectedAuthor.nickname ?: "",
                        canNavigateBack = true,
                        navigateUp = { navController.navigate(HomeDestination.route) }
                    )
                },
                navigateToQuestionnaireDetails = {
                    navController.navigate(QuestionnaireDetailsDestination.route)
                },
                modifier = Modifier
            )
        }
    }
}