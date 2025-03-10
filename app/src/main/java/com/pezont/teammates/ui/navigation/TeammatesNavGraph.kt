package com.pezont.teammates.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pezont.teammates.ui.TeammatesTopAppBar
import com.pezont.teammates.ui.TeammatesViewModel
import com.pezont.teammates.ui.items.HomeDestination
import com.pezont.teammates.ui.items.TeammatesHomeItem
import com.pezont.teammates.ui.items.TeammatesLoadingItem
import com.pezont.teammates.ui.screens.ErrorScreen
import com.pezont.teammates.ui.screens.LoginScreen
import com.pezont.teammates.ui.screens.ProfileDestination
import com.pezont.teammates.ui.screens.ProfileScreen
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesScreen
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateScreen
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesScreen


@Composable
fun TeammatesNavGraph(
    onTabChange: (ContentType) -> Unit,
    navController: NavHostController,
    viewModel: TeammatesViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

//    LaunchedEffect(Unit) {
//        launch {
//            viewModel.authToastCode.collect { code ->
//                code?.let { sendAuthToast(it, context) }
//            }
//        }
//        launch {
//            viewModel.questionnairesToastCode.collect { code ->
//                code?.let { sendQuestionnairesToast(it, context) }
//            }
//        }
//    }

    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorState by viewModel.errorState.collectAsState()

    var currentTab by remember { mutableStateOf(ContentType.Home) }

    val currentRoute =
        navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry).value?.destination?.route

    LaunchedEffect(teammatesAppState) {
        when {
            teammatesAppState.errorState.isNetworkError -> {
                navController.navigate(ErrorDestination.route)
            }
        }
    }

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        } else if (navController.currentDestination?.route == Routes.LOGIN) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = when {
            isLoading -> Routes.LOADING
            !isAuthenticated -> Routes.LOGIN
            else -> Routes.HOME
        },

        modifier = Modifier.padding(paddingValues),
//        enterTransition = { fadeIn(animationSpec = tween(500)) },
//        exitTransition = { fadeOut(animationSpec = tween(500)) },
    ) {
        composable(Routes.LOADING) {
            TeammatesLoadingItem()
        }

        composable(Routes.LOGIN) {
            LoginScreen(viewModel)
        }

        composable(Routes.ERROR) {
            ErrorScreen(onClick = {
                viewModel.clearError()
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.ERROR) { inclusive = true }
                }
            })
        }

        composable(HomeDestination.route) {
            onTabChange(ContentType.Home)
            val questionnaires = teammatesAppState.questionnaires

            TeammatesHomeItem(
                viewModel = viewModel,
                questionnaires = questionnaires,
                onRefresh = viewModel::fetchQuestionnaires,
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
            val likedQuestionnaires by viewModel.likedQuestionnaires.collectAsState()

            LikedQuestionnairesScreen(
                likedQuestionnaires = likedQuestionnaires,
                onRefresh = viewModel::fetchLikedQuestionnaires,
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
            onTabChange(ContentType.Create)
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
            val user by viewModel.currentUser.collectAsState()

            ProfileScreen(
                navigateToMyQuestionnaires = {
                    viewModel.loadUserQuestionnaires()
                    navController.navigate(UserQuestionnairesDestination.route)
                },
                logout = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) {
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
            val userQuestionnaires by viewModel.userQuestionnaires.collectAsState()

            UserQuestionnairesScreen(
                navigateToQuestionnaireCreate = {
                    navController.navigate(QuestionnaireCreateDestination.route)
                    //Todo current tab
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