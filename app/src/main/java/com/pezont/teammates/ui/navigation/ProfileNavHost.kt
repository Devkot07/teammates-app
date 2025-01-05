package com.pezont.teammates.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.TeammatesViewModel
import com.pezont.teammates.ui.screens.ProfileDestination
import com.pezont.teammates.ui.screens.ProfileScreen
import com.pezont.teammates.ui.screens.myQuestionnaires.MyQuestionnairesDestination
import com.pezont.teammates.ui.screens.myQuestionnaires.MyQuestionnairesScreen
import com.pezont.teammates.ui.screens.myQuestionnaires.QuestionnaireEntryDestination
import com.pezont.teammates.ui.screens.myQuestionnaires.QuestionnaireEntryScreen

@Composable
fun ProfileNavHost(
    viewModel: TeammatesViewModel,
    teammatesUiState: TeammatesUiState.Home,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
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
                teammatesUiState,
                viewModel,

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
                teammatesUiState = teammatesUiState,
                viewModel = viewModel,
            )
        }
        composable(route = QuestionnaireEntryDestination.route) {
            QuestionnaireEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel,
                teammatesUiState = teammatesUiState
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
