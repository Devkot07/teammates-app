package com.pezont.teammates.ui.screens


import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.pezont.teammates.R
import com.pezont.teammates.TeammatesBottomNavigationBar
import com.pezont.teammates.models.ContentType
import com.pezont.teammates.models.NavigationItemContent
import com.pezont.teammates.ui.TeammatesBackHandler
import com.pezont.teammates.ui.items.HomeDestination
import com.pezont.teammates.ui.navigation.TeammatesNavGraph
import com.pezont.teammates.ui.screens.questionnaires.LikedQuestionnairesDestination
import com.pezont.teammates.ui.screens.questionnaires.QuestionnaireCreateDestination

@Composable
fun TeammatesApp() {

    val context = LocalContext.current

    val navController = rememberNavController()

    val navigationItemContentList = listOf(
        NavigationItemContent(
            ContentType.Home,
            Icons.Default.Home,
            stringResource(R.string.home)
        ),
        NavigationItemContent(
            ContentType.Liked,
            Icons.Default.Favorite,
            stringResource(R.string.favorites)
        ),
        NavigationItemContent(
            ContentType.Create,
            Icons.Default.AddCircle,
            stringResource(R.string.create)
        ),
        NavigationItemContent(
            ContentType.Profile,
            Icons.Default.Drafts,
            stringResource(R.string.profile)
        )
    )

    val bottomBarDestinations = listOf(
        HomeDestination.route,
        LikedQuestionnairesDestination.route,
        QuestionnaireCreateDestination.route,
        ProfileDestination.route
    )

    var currentTab by remember { mutableStateOf(ContentType.Home) }

    val currentRoute =
        navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry).value?.destination?.route

    val shouldShowBottomBar = currentRoute in bottomBarDestinations

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                TeammatesBottomNavigationBar(
                    currentTab = currentTab,
                    onTabPressed = { contentType ->
                        currentTab = contentType
                        when (contentType) {
                            ContentType.Home -> navController.navigate(
                                HomeDestination.route
                            )

                            ContentType.Liked -> navController.navigate(
                                LikedQuestionnairesDestination.route
                            )

                            ContentType.Create -> navController.navigate(
                                QuestionnaireCreateDestination.route
                            )

                            ContentType.Profile -> navController.navigate(
                                ProfileDestination.route
                            )
                        }
                    },
                    navigationItemContentList = navigationItemContentList,
                    modifier = Modifier.height(60.dp)
                )
            }
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) { paddingValues ->

        TeammatesBackHandler(
            currentRoute = currentRoute,
            onTabChange = { currentTab = it },
            navController = navController,
            context = context
        )
        TeammatesNavGraph(
            navController = navController,
            paddingValues = paddingValues
        )
    }

}










