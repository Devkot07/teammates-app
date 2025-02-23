package com.pezont.teammates.ui

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.pezont.teammates.models.ContentType
import com.pezont.teammates.ui.screens.HomeDestination
import com.pezont.teammates.ui.screens.ProfileDestination
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateDestination
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesDestination

@Composable
fun TeammatesBackHandler(
    currentRoute: String?,
    onTabChange: (ContentType) -> Unit,
    navController: NavController,
    context: Context
) {
    val homeRoute = HomeDestination.route
    val bottomNavigationTabs = listOf(
        LikedQuestionnairesDestination.route,
        QuestionnaireCreateDestination.route,
        ProfileDestination.route
    )

    BackHandler {
        when (currentRoute) {
            homeRoute -> context.findActivity()?.finish()
            in bottomNavigationTabs -> {
                onTabChange(ContentType.Home)
                navController.navigate(homeRoute) {
                    popUpTo(homeRoute) { inclusive = false }
                }
            }

            UserQuestionnairesDestination.route -> navController.navigateUp()
            else -> {}
        }
    }
}

fun Context.findActivity(): Activity? = this as? Activity
