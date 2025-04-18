package com.pezont.teammates.ui

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.pezont.teammates.domain.model.BottomNavItem
import com.pezont.teammates.ui.items.HomeDestination
import com.pezont.teammates.ui.screens.UserProfileDestination
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateDestination
import com.pezont.teammates.ui.screens.questionnaires.UserQuestionnairesDestination

@Composable
fun TeammatesBackHandler(
    currentRoute: String?,
    onTabChange: (BottomNavItem) -> Unit,
    navController: NavController,
    context: Context
) {
    val homeRoute = HomeDestination.route
    val bottomNavigationTabs = listOf(
        LikedQuestionnairesDestination.route,
        QuestionnaireCreateDestination.route,
        UserProfileDestination.route
    )

    BackHandler {
        when (currentRoute) {
            homeRoute -> context.findActivity()?.finish()
            in bottomNavigationTabs -> {
                onTabChange(BottomNavItem.HOME)
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
