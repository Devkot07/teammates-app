package com.pezont.teammates.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.pezont.teammates.R
import com.pezont.teammates.models.ContentType
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.TeammatesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//TODO: Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeammatesHomeScreen(
    currentItem: Int,
    teammatesUiState: TeammatesUiState.Home,
    viewModel: TeammatesViewModel,
    onTabPressed: (ContentType) -> Unit,
) {


    val navigationItemContentList = listOf(
        NavigationItemContent(
            contentType = ContentType.Home,
            icon = Icons.Default.Home,
            text = stringResource(R.string.home)
        ),
        NavigationItemContent(
            contentType = ContentType.Favorites,
            icon = Icons.Default.Favorite,
            text = stringResource(R.string.favorites)
        ),
        NavigationItemContent(
            contentType = ContentType.Profile,
            icon = Icons.Default.Drafts,
            text = stringResource(R.string.profile)
        )
    )

    val topAppBarTitle = when (teammatesUiState.currentContent) {
        ContentType.Home -> stringResource(R.string.home)
        ContentType.Favorites -> stringResource(R.string.favorites)
        ContentType.Profile -> stringResource(R.string.profile)
    }

    Scaffold(
        topBar = { TeammatesTopAppBar(title = topAppBarTitle, canNavigateBack = false) },
        bottomBar = {
            BottomNavigationBar(
                currentTab = teammatesUiState.currentContent,
                onTabPressed = onTabPressed,
                navigationItemContentList = navigationItemContentList
            )
        }
    ) { paddingValues ->
        Log.i("LOGIC", "${teammatesUiState.currentContent}")

        when (teammatesUiState.currentContent) {
            ContentType.Home -> {
                val isRefreshing = remember { mutableStateOf(false) }

                val pageIndex = remember { mutableIntStateOf(1) }
                val listState = remember(teammatesUiState.questionnaires) { LazyListState() }
                val newCurrentItem = (pageIndex.intValue) * 10


                val isLoadingMore = remember { mutableStateOf(false) }

                PullToRefreshBox(
                    modifier = Modifier.padding(paddingValues),
                    isRefreshing = isRefreshing.value,
                    onRefresh = {
                        isRefreshing.value = true
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(1000L)
                                viewModel.tryGetQuestionnairesByGame()
                            } finally {
                                withContext(Dispatchers.Main) {
                                    isRefreshing.value = false
                                    pageIndex.intValue = 1
                                }
                            }
                        }
                    }
                ) {


                    QuestionnairesGridScreen(
                        viewModel = viewModel,
                        teammatesUiState = teammatesUiState,
                        questionnaires = teammatesUiState.questionnaires,
                        listState = listState,
                        contentPadding = paddingValues
                    )

                    LaunchedEffect(listState) {
                        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                            .collect { lastIndex ->
                                if (isLoadingMore.value) {
                                    isLoadingMore.value = false
                                    listState.scrollToItem(currentItem)
                                }

                                if (lastIndex == teammatesUiState.questionnaires.size && !isLoadingMore.value) {
                                    isLoadingMore.value = true

                                    try {
                                        Log.i("LOGIC", "$newCurrentItem")
                                        viewModel.getNextFakeQuestionnaires(
                                            teammatesUiState = teammatesUiState,
                                            i = pageIndex.intValue,
                                            newCurrentItem = newCurrentItem
                                        )
                                        Log.i("LOGIC", "--> $newCurrentItem")
                                        pageIndex.intValue++

                                    } finally {
                                        Log.i("LOGIC", "-> $currentItem")
                                    }

                                }

                            }
                    }


                }
            }

            ContentType.Favorites -> {
                Text("Favorites Screen")
            }

            ContentType.Profile -> {
                ProfileScreen(
                    teammatesUiState = teammatesUiState,
                    viewModel = viewModel,
                    paddingValues = paddingValues
                )
            }
        }
    }
}


// TODO in file TeammatesTopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeammatesTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                title,
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        }
    )
}

@Composable
private fun BottomNavigationBar(
    currentTab: ContentType,
    onTabPressed: ((ContentType) -> Unit),
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        for (navItem in navigationItemContentList) {
            NavigationBarItem(
                selected = currentTab == navItem.contentType,
                onClick = { onTabPressed(navItem.contentType) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.text
                    )
                }
            )
        }
    }
}

private data class NavigationItemContent(
    val contentType: ContentType,
    val icon: ImageVector,
    val text: String
)

