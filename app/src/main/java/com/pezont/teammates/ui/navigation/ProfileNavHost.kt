package com.pezont.teammates.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pezont.teammates.models.ContentType
import com.pezont.teammates.models.Games
import com.pezont.teammates.ui.TeammatesTopAppBar
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.screens.ProfileDestination
import com.pezont.teammates.ui.screens.ProfileScreen
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesScreen
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireEntryDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireEntryScreen
import okhttp3.MultipartBody

@Composable
fun ProfileNavHost(
    teammatesUiState: TeammatesUiState.Home,
    navController: NavHostController = rememberNavController(),

    logout: () -> Unit,
    createNewQuestionnaireAction: (
        header: String,
        description: String,
        selectedGame: Games,
        image: MultipartBody.Part?
    ) -> Unit,
    getUserQuestionnaires: (teammatesUiState: TeammatesUiState.Home) -> Unit,

    bottomBar: @Composable () -> Unit = {},
    ) {
    NavHost(
        navController = navController,
        startDestination = ProfileDestination.route,
    ) {

        composable(route = ProfileDestination.route) {
            ProfileScreen(
                navigateToMyQuestionnaires = {
                    getUserQuestionnaires(teammatesUiState)
                    navController.navigate(UserQuestionnairesDestination.route)
                },

                logout = logout,
                user = teammatesUiState.user,
                bottomBar = bottomBar,
            )
        }

        composable(route = UserQuestionnairesDestination.route) {
            UserQuestionnairesScreen(
                navigateToQuestionnaireEntry = {
                    navController.navigate(
                        QuestionnaireEntryDestination.route
                    )
                },
                onNavigateUp = { navController.navigateUp() },
                getUserQuestionnaires = getUserQuestionnaires,
                teammatesUiState = teammatesUiState,
            )
        }
        composable(route = QuestionnaireEntryDestination.route) {
            QuestionnaireEntryScreen(
                createNewQuestionnaireAction = createNewQuestionnaireAction,
                topBar = {
                    TeammatesTopAppBar(
                        title = stringResource(QuestionnaireEntryDestination.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navController.navigateUp() }
                    )
                }
            )
        }

//        composable(
//            route = QuestionnaireEditDestination.routeWithArgs,
//            arguments = listOf(navArgument(QuestionnaireEditDestination.itemIdArg) {
//                type = NavType.IntType
//            })
//        ) {
//            ItemEditScreen(
//                navigateBack = { navController.popBackStack() },
//                onNavigateUp = { navController.navigateUp() }
//            )
//        }


    }


}
