package com.pezont.teammates.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.enums.BottomNavItem
import com.pezont.teammates.ui.ObserveAsEvents
import com.pezont.teammates.ui.components.TeammatesBottomNavigationBar
import com.pezont.teammates.ui.navigation.Destinations
import com.pezont.teammates.ui.navigation.NavigationItemContent
import com.pezont.teammates.ui.navigation.TeammatesNavGraph
import com.pezont.teammates.ui.snackbar.SnackbarController
import com.pezont.teammates.viewmodel.AuthViewModel
import com.pezont.teammates.viewmodel.AuthorViewModel
import com.pezont.teammates.viewmodel.QuestionnairesViewModel
import com.pezont.teammates.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun TeammatesApp() {
    val context = LocalContext.current

    val authorViewModel: AuthorViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val questionnairesViewModel: QuestionnairesViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val navController = rememberNavController()
    val navBackStackEntry by navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)

    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarDestinations = listOf(
        Destinations.Home.route,
        Destinations.LikedQuestionnaires.route,
        Destinations.QuestionnaireCreate.route,
        Destinations.UserProfile.route
    )
    val shouldShowBottomBar by remember(currentRoute) {
        derivedStateOf { currentRoute in bottomBarDestinations }
    }

    var currentTab by remember { mutableStateOf(BottomNavItem.HOME) }

    val navigationItemContentList = listOf(
        NavigationItemContent(
            BottomNavItem.HOME, Icons.Default.Home, stringResource(R.string.home)
        ), NavigationItemContent(
            BottomNavItem.LIKED, Icons.Default.Favorite, stringResource(R.string.favorites)
        ), NavigationItemContent(
            BottomNavItem.CREATE, Icons.Default.AddCircle, stringResource(R.string.create)
        ), NavigationItemContent(
            BottomNavItem.PROFILE, Icons.Default.Drafts, stringResource(R.string.profile)
        )
    )


    ObserveAsEvents(
        flow = SnackbarController.events,
        snackbarHostState
    ) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = context.getString(
                    event.messageId,
                    *event.formatArgs.toTypedArray()
                ),
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        bottomBar = {
            if (shouldShowBottomBar) {
                TeammatesBottomNavigationBar(
                    currentTab = currentTab,
                    onTabPressed = { contentType ->
                        currentTab = contentType
                        when (contentType) {
                            BottomNavItem.HOME -> navController.navigate(
                                Destinations.Home.route
                            )

                            BottomNavItem.LIKED -> navController.navigate(
                                Destinations.LikedQuestionnaires.route
                            )

                            BottomNavItem.CREATE -> navController.navigate(
                                Destinations.QuestionnaireCreate.route
                            )

                            BottomNavItem.PROFILE -> navController.navigate(
                                Destinations.UserProfile.route
                            )
                        }
                    },
                    navigationItemContentList = navigationItemContentList,
                    modifier = Modifier.height(60.dp)
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) { paddingValues ->

        TeammatesNavGraph(
            onTabChange = { currentTab = it },
            navController = navController,
            authorViewModel = authorViewModel,
            authViewModel = authViewModel,
            questionnairesViewModel = questionnairesViewModel,
            userViewModel = userViewModel,

            paddingValues = paddingValues,
        )
    }
}


