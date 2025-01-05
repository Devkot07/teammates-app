package com.pezont.teammates.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.TeammatesViewModel
import com.pezont.teammates.ui.screens.myQuestionnaires.MyQuestionnairesDestination
import com.pezont.teammates.ui.screens.myQuestionnaires.MyQuestionnairesScreen
import com.pezont.teammates.ui.screens.myQuestionnaires.QuestionnaireEntryDestination
import com.pezont.teammates.ui.screens.myQuestionnaires.QuestionnaireEntryScreen

@Composable
fun MyQuestionnairesNavHost(
    viewModel: TeammatesViewModel,
    teammatesUiState: TeammatesUiState.Home,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MyQuestionnairesDestination.route,
        modifier = modifier
    ) {

        composable(route = MyQuestionnairesDestination.route) {
            MyQuestionnairesScreen(
                navigateToQuestionnaireEntry = {
                    navController.navigate(
                        QuestionnaireEntryDestination.route
                    )
                },
                teammatesUiState,
                viewModel,
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
