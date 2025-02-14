package com.pezont.teammates.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pezont.teammates.models.Games
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.screens.ProfileDestination
import com.pezont.teammates.ui.screens.ProfileScreen
import com.pezont.teammates.ui.screens.myQuestionnaires.MyQuestionnairesDestination
import com.pezont.teammates.ui.screens.myQuestionnaires.MyQuestionnairesScreen
import com.pezont.teammates.ui.screens.myQuestionnaires.QuestionnaireEntryDestination
import com.pezont.teammates.ui.screens.myQuestionnaires.QuestionnaireEntryScreen
import okhttp3.MultipartBody

@Composable
fun ProfileNavHost(
    teammatesUiState: TeammatesUiState.Home,
    navController: NavHostController = rememberNavController(),

    logout: () -> Unit,
    createNewQuestionnaireAction: (header: String,
                                   description: String,
                                   selectedGame: Games,
                                   image: MultipartBody.Part?)  -> Unit,

    modifier: Modifier,
    paddingValues: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = ProfileDestination.route,
        modifier = modifier.padding(paddingValues)
    ) {

        composable(route = ProfileDestination.route) {
            ProfileScreen(
                navigateToMyQuestionnaires = {
                    navController.navigate(
                        MyQuestionnairesDestination.route
                    )
                },

                logout,
                teammatesUiState.user,

            )
        }

        composable(route = MyQuestionnairesDestination.route) {
            MyQuestionnairesScreen(
                navigateToQuestionnaireEntry = {
                    navController.navigate(
                        QuestionnaireEntryDestination.route
                    )
                },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable(route = QuestionnaireEntryDestination.route) {
            QuestionnaireEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                createNewQuestionnaireAction = createNewQuestionnaireAction,
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
