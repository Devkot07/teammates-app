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
import com.pezont.teammates.ObserveAsEvents
import com.pezont.teammates.ObserveState
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.UiEvent
import com.pezont.teammates.domain.model.AuthState
import com.pezont.teammates.domain.model.BottomNavItem
import com.pezont.teammates.ui.TeammatesBackHandler
import com.pezont.teammates.ui.TeammatesTopAppBar
import com.pezont.teammates.ui.items.HomeDestination
import com.pezont.teammates.ui.items.LoadingDestination
import com.pezont.teammates.ui.items.TeammatesHomeItem
import com.pezont.teammates.ui.items.LoadingItem
import com.pezont.teammates.ui.screens.AuthorProfileDestination
import com.pezont.teammates.ui.screens.AuthorProfileScreen
import com.pezont.teammates.ui.screens.LoginDestination
import com.pezont.teammates.ui.screens.LoginScreen
import com.pezont.teammates.ui.screens.UserProfileDestination
import com.pezont.teammates.ui.screens.UserProfileScreen
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesScreen
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateScreen
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireDetailsDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireDetailsScreen
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesScreen

@Composable
fun TeammatesNavGraph(
    onTabChange: (BottomNavItem) -> Unit,
    navController: NavHostController,
    viewModel: TeammatesViewModel,
    paddingValues: PaddingValues,
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.LoggedOut -> {
                navController.navigate(LoginDestination.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }

            is UiEvent.LoggedIn -> {
                navController.navigate(HomeDestination.route) {
                    popUpTo(LoginDestination.route) { inclusive = true }
                }
            }

            is UiEvent.QuestionnaireCreated -> {
                viewModel.loadUserQuestionnaires()
                navController.navigate(UserQuestionnairesDestination.route) {
                    popUpTo(HomeDestination.route) { inclusive = false }
                }
            }
        }
    }

    ObserveState(uiState.authState) { authState ->
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
        startDestination = when (uiState.authState) {
            AuthState.LOADING -> LoadingDestination.route
            AuthState.UNAUTHENTICATED -> LoginDestination.route
            else -> HomeDestination.route
        },

        modifier = Modifier.padding(paddingValues),
//        enterTransition = { fadeIn(animationSpec = tween(500)) },
//        exitTransition = { fadeOut(animationSpec = tween(500)) },
    ) {
        composable(LoadingDestination.route) {
            LoadingItem()
        }

        composable(LoginDestination.route) {
            LoginScreen(onTabChange, viewModel)
        }

        composable(HomeDestination.route) {
            onTabChange(BottomNavItem.HOME)
            val questionnaires = uiState.questionnaires

            TeammatesHomeItem(
                viewModel = viewModel,
                questionnaires = questionnaires,
                onRefresh = viewModel::loadQuestionnaires,
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
            val likedQuestionnaires = uiState.likedQuestionnaires

            LikedQuestionnairesScreen(
                likedQuestionnaires = likedQuestionnaires,
                viewModel = viewModel,
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
                viewModel = viewModel,
                uiState = uiState,
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
            val user = uiState.user

            UserProfileScreen(
                navigateToMyQuestionnaires = {
                    viewModel.loadUserQuestionnaires()
                    navController.navigate(UserQuestionnairesDestination.route)
                },
                logout = {
                    viewModel.logout()
                },
                user = user,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(UserProfileDestination.titleRes),
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

        composable(UserQuestionnairesDestination.route) {
            val userQuestionnaires = uiState.userQuestionnaires

            UserQuestionnairesScreen(
                navigateToQuestionnaireCreate = {
                    navController.navigate(QuestionnaireCreateDestination.route)
                    onTabChange(BottomNavItem.CREATE)
                },
                userQuestionnaires = userQuestionnaires,
                onRefresh = viewModel::loadUserQuestionnaires,
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
            val user = uiState.user
            val selectedQuestionnaire = uiState.selectedQuestionnaire

            QuestionnaireDetailsScreen(
                viewModel = viewModel,
                uiState = uiState,
                questionnaire = selectedQuestionnaire,

                navigateToAuthorProfile = {
                    if (user.publicId == selectedQuestionnaire.authorId)
                        navController.navigate(UserProfileDestination.route)
                    else
                        navController.navigate(AuthorProfileDestination.route)
                },

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
            val selectedAuthor = uiState.selectedAuthor
            val selectedAuthorQuestionnaires = uiState.selectedAuthorQuestionnaires
            AuthorProfileScreen(
                viewModel = viewModel,
                contentState = uiState.contentState,
                starAction = {},
                author = selectedAuthor,
                authorQuestionnaires = selectedAuthorQuestionnaires,
                topBar = {
                    TeammatesTopAppBar(
                        title = selectedAuthor.nickname.toString(),
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