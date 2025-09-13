package com.devkot.teammates.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.devkot.teammates.R
import com.devkot.teammates.domain.model.enums.AuthState
import com.devkot.teammates.domain.model.enums.BottomNavItem
import com.devkot.teammates.ui.ObserveAsEvents
import com.devkot.teammates.ui.ObserveState
import com.devkot.teammates.ui.components.Dots
import com.devkot.teammates.ui.components.DropdownItem
import com.devkot.teammates.ui.components.LoadingItemWithText
import com.devkot.teammates.ui.components.TeammatesBackHandler
import com.devkot.teammates.ui.components.TeammatesDropdownMenu
import com.devkot.teammates.ui.components.TeammatesTopAppBar
import com.devkot.teammates.ui.screen.LoginScreen
import com.devkot.teammates.ui.screen.TeammatesHomeScreen
import com.devkot.teammates.ui.screen.author.AuthorProfileScreen
import com.devkot.teammates.ui.screen.questionnaire.LikedQuestionnairesScreen
import com.devkot.teammates.ui.screen.questionnaire.QuestionnaireCreateScreen
import com.devkot.teammates.ui.screen.questionnaire.QuestionnaireDetailsScreen
import com.devkot.teammates.ui.screen.questionnaire.QuestionnaireEditScreen
import com.devkot.teammates.ui.screen.questionnaire.UserQuestionnairesScreen
import com.devkot.teammates.ui.screen.user.UserProfileEditScreen
import com.devkot.teammates.ui.screen.user.UserProfileScreen
import com.devkot.teammates.viewmodel.AuthUiEvent
import com.devkot.teammates.viewmodel.AuthViewModel
import com.devkot.teammates.viewmodel.AuthorUiEvent
import com.devkot.teammates.viewmodel.AuthorViewModel
import com.devkot.teammates.viewmodel.QuestionnaireUiEvent
import com.devkot.teammates.viewmodel.QuestionnairesViewModel
import com.devkot.teammates.viewmodel.UserUiEvent
import com.devkot.teammates.viewmodel.UserViewModel
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
            QuestionnaireUiEvent.QuestionnaireUpdated -> {
                coroutineScope.launch {
                    questionnairesViewModel.loadUserQuestionnaires()
                    navController.navigate(Destinations.QuestionnaireDetails.route) {
                        popUpTo(Destinations.Home.route) { inclusive = false }
                    }
                }
            }
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
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 300
                )
            )
        },
        exitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 300
                )
            )
        },
        popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) }
    ) {
        composable(
            Destinations.Loading.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 500
                    )
                )
            },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 100)) },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 500
                    )
                )
            },
            popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 100)) }
        ) {
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
                    val isUserQuestionnaire =
                        userQuestionnaires.any { it.questionnaireId == selectedQuestionnaire.questionnaireId }
                    var showDropDownMenu by remember { mutableStateOf(false) }
                    val items = listOf(
                        DropdownItem(
                            text = stringResource(R.string.edit_questionnaire),
                            icon = Icons.Filled.ModeEditOutline,
                            onClick = {
                                navController.navigate(Destinations.QuestionnaireEdit.route)
                                showDropDownMenu = false
                            }
                        ),
                    )
                    TeammatesTopAppBar(
                        title = stringResource(Destinations.QuestionnaireDetails.titleRes),
                        actions = {
                            if (isUserQuestionnaire) {
                                IconButton(onClick = { showDropDownMenu = !showDropDownMenu }) {
                                    Icon(Dots, contentDescription = null)
                                }
                                TeammatesDropdownMenu(
                                    expanded = showDropDownMenu,
                                    items = items,
                                    onDismissRequest = { showDropDownMenu = false },
                                )
                            }
                        },
                        canNavigateBack = true,
                        navigateUp = {
                            questionnairesViewModel.resetSelectedQuestionnaireState()
                            navController.navigateUp()
                        },
                    )
                }
            )
            TeammatesBackHandler(
                currentRoute = currentRoute,
                onTabChange = { currentTab = it },
                navController = navController,
                context = context,
                onBack = { questionnairesViewModel.resetSelectedQuestionnaireState() }
            )

        }

        composable(Destinations.QuestionnaireEdit.route) {
            QuestionnaireEditScreen(
                questionnaire = selectedQuestionnaire,
                questionnairesViewModel = questionnairesViewModel,
                navigateUp = { navController.navigateUp() }

            )
            TeammatesBackHandler(
                currentRoute = currentRoute,
                onTabChange = { currentTab = it },
                navController = navController,
                context = context
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