package com.pezont.teammates.ui.screens

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pezont.teammates.R
import com.pezont.teammates.TeammatesBottomNavigationBar
import com.pezont.teammates.models.ContentType
import com.pezont.teammates.models.NavigationItemContent
import com.pezont.teammates.ui.Dots
import com.pezont.teammates.ui.TeammatesBackHandler
import com.pezont.teammates.ui.TeammatesTopAppBar
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.TeammatesViewModel
import com.pezont.teammates.ui.items.HomeDestination
import com.pezont.teammates.ui.navigation.TeammatesNavHost
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesScreen
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateScreen
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesScreen


//TODO: Screen
@Composable
fun TeammatesHomeScreen(
    teammatesUiState: TeammatesUiState.Home,
    viewModel: TeammatesViewModel,
    context: Context
) {
    val navController = rememberNavController()

    val navigationItemContentList = listOf(
        NavigationItemContent(
            ContentType.Home,
            Icons.Default.Home,
            stringResource(R.string.home)
        ),
        NavigationItemContent(
            ContentType.Liked,
            Icons.Default.Favorite,
            stringResource(R.string.favorites)
        ),
        NavigationItemContent(
            ContentType.Create,
            Icons.Default.AddCircle,
            stringResource(R.string.create)
        ),
        NavigationItemContent(
            ContentType.Profile,
            Icons.Default.Drafts,
            stringResource(R.string.profile)
        )
    )

    val bottomBarDestinations = listOf(
        HomeDestination.route,
        LikedQuestionnairesDestination.route,
        QuestionnaireCreateDestination.route,
        ProfileDestination.route
    )

    var currentTab by remember { mutableStateOf(ContentType.Home) }

    val currentRoute =
        navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry).value?.destination?.route

    val shouldShowBottomBar = currentRoute in bottomBarDestinations

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                TeammatesBottomNavigationBar(
                    currentTab = currentTab,
                    onTabPressed = { contentType ->
                        currentTab = contentType
                        when (contentType) {
                            ContentType.Home -> navController.navigate(
                                HomeDestination.route
                            )

                            ContentType.Liked -> navController.navigate(
                                LikedQuestionnairesDestination.route
                            )

                            ContentType.Create -> navController.navigate(
                                QuestionnaireCreateDestination.route
                            )

                            ContentType.Profile -> navController.navigate(
                                ProfileDestination.route
                            )
                        }
                    },
                    navigationItemContentList = navigationItemContentList,
                    modifier = Modifier.height(60.dp)
                )
            }
        }
    ) { paddingValues ->
        TeammatesBackHandler(currentRoute, { currentTab = it }, navController, context)

        NavHost(
            navController = navController,
            startDestination = HomeDestination.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },

            ) {
            composable(HomeDestination.route) {
                TeammatesHomeContent(
                    viewModel = viewModel,
                    teammatesUiState = teammatesUiState,
                    topBar = {
                        TeammatesTopAppBar(
                            title = stringResource(HomeDestination.titleRes),
                            canNavigateBack = false,
                        )
                    }
                )
            }
            composable(LikedQuestionnairesDestination.route) {
                LikedQuestionnairesScreen(
                    viewModel = viewModel,
                    teammatesUiState = teammatesUiState,
                    topBar = {
                        TeammatesTopAppBar(
                            title = stringResource(LikedQuestionnairesDestination.titleRes),
                            canNavigateBack = false,
                        )
                    }
                )
            }
            composable(QuestionnaireCreateDestination.route) {
                QuestionnaireCreateScreen(
                    createNewQuestionnaireAction = viewModel::createNewQuestionnaire
                )
            }
            composable(ProfileDestination.route) {
                ProfileScreen(
                    navigateToMyQuestionnaires = {
                        viewModel.tryGetQuestionnairesByUserId(teammatesUiState)
                        navController.navigate(
                            UserQuestionnairesDestination.route
                        )
                    },
                    logout = viewModel::clearUserData,
                    user = teammatesUiState.user,
                    topBar = {
                        TeammatesTopAppBar(
                            title = stringResource(ProfileDestination.titleRes),
                            canNavigateBack = false,
                            action = {
                                IconButton(
                                    {}
                                ) {
                                    Icon(
                                        imageVector = Dots,
                                        contentDescription = "Localized description",
                                    )
                                }

                            }
                        )
                    }
                )
            }
            composable(UserQuestionnairesDestination.route) {
                UserQuestionnairesScreen(
                    navigateToQuestionnaireCreate = {
                        currentTab = ContentType.Create
                        navController.navigate(
                            QuestionnaireCreateDestination.route
                        )
                    },
                    getUserQuestionnaires = viewModel::tryGetQuestionnairesByUserId,
                    teammatesUiState = teammatesUiState,
                    topBar = {
                        TeammatesTopAppBar(
                            title = stringResource(QuestionnaireCreateDestination.titleRes),
                            canNavigateBack = true,
                            navigateUp = { navController.navigateUp() },
                        )
                    }
                )
            }
            composable(QuestionnaireCreateDestination.route) {
                QuestionnaireCreateScreen(
                    createNewQuestionnaireAction = viewModel::createNewQuestionnaire,
                    topBar = {
                        TeammatesTopAppBar(
                            title = stringResource(QuestionnaireCreateDestination.titleRes),
                            canNavigateBack = false
                        )
                    }
                )
            }
        }
    }
}








