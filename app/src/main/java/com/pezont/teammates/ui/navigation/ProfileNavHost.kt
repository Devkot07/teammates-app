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
import com.pezont.teammates.ui.screens.NavigationItemContent
import com.pezont.teammates.ui.screens.ProfileDestination
import com.pezont.teammates.ui.screens.ProfileScreen
import com.pezont.teammates.ui.screens.questionnaires.MyQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.MyQuestionnairesScreen
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


    onTabPressed: (ContentType) -> Unit,
    navigationItemContentList: List<NavigationItemContent>,

    ) {
    NavHost(
        navController = navController,
        startDestination = ProfileDestination.route,
    ) {

        composable(route = ProfileDestination.route) {
            ProfileScreen(
                navigateToMyQuestionnaires = {
                    getUserQuestionnaires(teammatesUiState)
                    navController.navigate(MyQuestionnairesDestination.route)
                },

                logout = logout,
                user = teammatesUiState.user,
                onTabPressed = onTabPressed,
                navigationItemContentList = navigationItemContentList,
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
