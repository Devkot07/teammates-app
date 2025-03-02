package com.pezont.teammates.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pezont.teammates.ui.Dots
import com.pezont.teammates.ui.TeammatesTopAppBar
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.TeammatesViewModel
import com.pezont.teammates.ui.items.HomeDestination
import com.pezont.teammates.ui.screens.ProfileDestination
import com.pezont.teammates.ui.screens.ProfileScreen
import com.pezont.teammates.ui.items.TeammatesHomeItem
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesScreen
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateScreen
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesScreen


@Composable
fun TeammatesNavHost(
    teammatesUiState: TeammatesUiState.Home,
    viewModel: TeammatesViewModel,
    navController: NavHostController,
    paddingValues: PaddingValues,
    navigateToQuestionnaireCreate:() -> Unit,
) {

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = Modifier.padding(paddingValues),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },

        ) {
        composable(HomeDestination.route) {
            TeammatesHomeItem(
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
                navigateToQuestionnaireCreate = navigateToQuestionnaireCreate,
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