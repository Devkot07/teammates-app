package com.devkot.teammates.ui.components

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.devkot.teammates.domain.model.enums.BottomNavItem
import com.devkot.teammates.ui.navigation.Destinations


@Composable
fun TeammatesBackHandler(
    currentRoute: String?,
    onTabChange: (BottomNavItem) -> Unit,
    navController: NavController,
    context: Context
) {
    val homeRoute = Destinations.Home.route
    val bottomNavigationTabs = listOf(
        Destinations.LikedQuestionnaires.route,
        Destinations.QuestionnaireCreate.route,
        Destinations.UserProfile.route
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

            Destinations.UserQuestionnaires.route -> navController.navigateUp()
            Destinations.UserProfileEdit.route -> navController.navigateUp()
            else -> {}
        }
    }
}

fun Context.findActivity(): Activity? = this as? Activity
