package com.pezont.teammates.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pezont.teammates.TeammatesViewModel
import com.pezont.teammates.domain.model.BottomNavItem
import com.pezont.teammates.ui.TeammatesBackHandler
import com.pezont.teammates.ui.TeammatesTopAppBar
import com.pezont.teammates.ui.items.HomeDestination
import com.pezont.teammates.ui.items.LoadingDestination
import com.pezont.teammates.ui.items.TeammatesHomeItem
import com.pezont.teammates.ui.items.TeammatesLoadingItem
import com.pezont.teammates.ui.screens.ErrorDestination
import com.pezont.teammates.ui.screens.ErrorScreen
import com.pezont.teammates.ui.screens.LoginDestination
import com.pezont.teammates.ui.screens.LoginScreen
import com.pezont.teammates.ui.screens.ProfileDestination
import com.pezont.teammates.ui.screens.ProfileScreen
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesScreen
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateScreen
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesScreen
import com.pezont.teammates.ui.sendAuthToast
import com.pezont.teammates.ui.sendQuestionnairesToast
import kotlinx.coroutines.launch


@Composable
fun TeammatesNavGraph(
    onTabChange: (BottomNavItem) -> Unit,
    navController: NavHostController,
    viewModel: TeammatesViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
) {
    val context = LocalContext.current

    //TODO SnackBars
    LaunchedEffect(Unit) {
        launch {
            viewModel.authToastCode.collect { code ->
                code?.let { sendAuthToast(it, context) }
            }
        }
        launch {
            viewModel.questionnairesToastCode.collect { code ->
                code?.let { sendQuestionnairesToast(it, context) }
            }
        }
    }

    val teammatesAppState by viewModel.teammatesAppState.collectAsState()


    var currentTab by remember { mutableStateOf(BottomNavItem.HOME) }

    val currentRoute =
        navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry).value?.destination?.route

    LaunchedEffect(teammatesAppState) {
        when {
            teammatesAppState.errorState.isNetworkError -> {
                navController.navigate(ErrorDestination.route)
            }

            teammatesAppState.errorState.errorCode != 0 -> {
                navController.navigate(ErrorDestination.route)
            }

            teammatesAppState.isLoading -> {
                navController.navigate(LoadingDestination.route)
            }

            !teammatesAppState.isAuthenticated -> {
                navController.navigate(LoginDestination.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }

            teammatesAppState.isAuthenticated && navController.currentDestination?.route == LoginDestination.route -> {
                navController.navigate(HomeDestination.route) {
                    popUpTo(LoginDestination.route) { inclusive = true }
                }
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = when {
            teammatesAppState.isLoading -> LoadingDestination.route
            teammatesAppState.isAuthenticated -> LoginDestination.route
            else -> HomeDestination.route
        },

        modifier = Modifier.padding(paddingValues),
//        enterTransition = { fadeIn(animationSpec = tween(500)) },
//        exitTransition = { fadeOut(animationSpec = tween(500)) },
    ) {
        composable(LoadingDestination.route) {
            TeammatesLoadingItem()
        }

        composable(LoginDestination.route) {
            LoginScreen(onTabChange, viewModel)
        }

        composable(ErrorDestination.route) {
            ErrorScreen(
                onClick = {
                    viewModel.clearError()
                    navController.navigate(HomeDestination.route) {
                        popUpTo(ErrorDestination.route) { inclusive = true }
                    }
                },
                errorText = teammatesAppState.errorState.errorMessage
            )
        }

        composable(HomeDestination.route) {
            onTabChange(BottomNavItem.HOME)
            val questionnaires = teammatesAppState.questionnaires

            TeammatesHomeItem(
                viewModel = viewModel,
                questionnaires = questionnaires,
                onRefresh = viewModel::loadQuestionnaires,
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
            val likedQuestionnaires = teammatesAppState.likedQuestionnaires

            LikedQuestionnairesScreen(
                likedQuestionnaires = likedQuestionnaires,
                viewModel = viewModel,
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
                navigateToHome = {
                    navController.navigate(HomeDestination.route) {
                        popUpTo(HomeDestination.route) { inclusive = false }
                    }
                },
                createNewQuestionnaireAction = viewModel::createNewQuestionnaire,
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

        composable(ProfileDestination.route) {
            onTabChange(BottomNavItem.PROFILE)
            val user = teammatesAppState.user

            ProfileScreen(
                navigateToMyQuestionnaires = {
                    viewModel.loadUserQuestionnaires()
                    navController.navigate(UserQuestionnairesDestination.route)
                },
                logout = {
                    viewModel.logout()
                    navController.navigate(LoginDestination.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                user = user,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(ProfileDestination.titleRes),
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
            val userQuestionnaires = teammatesAppState.userQuestionnaires

            UserQuestionnairesScreen(
                navigateToQuestionnaireCreate = {
                    navController.navigate(QuestionnaireCreateDestination.route)
                    onTabChange(BottomNavItem.CREATE)
                },
                userQuestionnaires = userQuestionnaires,
                onRefresh = viewModel::loadUserQuestionnaires,
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
    }
}