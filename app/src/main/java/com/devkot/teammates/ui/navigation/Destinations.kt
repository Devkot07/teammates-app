package com.devkot.teammates.ui.navigation

import com.devkot.teammates.R


sealed class Destinations(
    val route: String,
    val titleRes: Int
) {
    data object Login : Destinations("login", R.string.login)
    data object Home : Destinations("home", R.string.home)
    data object Loading : Destinations("loading", R.string.loading)
    data object AuthorProfile : Destinations("author_profile", R.string.profile)
    data object LikedQuestionnaires : Destinations("liked_questionnaires", R.string.favorites)
    data object QuestionnaireCreate : Destinations("create_questionnaire", R.string.create_questionnaire)
    data object QuestionnaireDetails : Destinations("questionnaires_details", R.string.questionnaire)
    data object UserQuestionnaires : Destinations("user_questionnaires", R.string.user_questionnaires)
    data object UserProfile : Destinations("user_profile", R.string.profile)
    data object UserProfileEdit : Destinations("user_profile_edit", R.string.profile)




    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { append("/$it") }
        }
    }
}
