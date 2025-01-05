package com.pezont.teammates.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
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
import com.pezont.teammates.ui.navigation.ProfileNavHost
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
        NavigationItemContent(ContentType.Home, Icons.Default.Home, stringResource(R.string.home)),
        NavigationItemContent(
            ContentType.Favorites,
            Icons.Default.Favorite,
            stringResource(R.string.favorites)
        ),
        NavigationItemContent(
            ContentType.Profile,
            Icons.Default.Drafts,
            stringResource(R.string.profile)
        )
    )

    val topAppBarTitle = stringResource(
        when (teammatesUiState.currentContent) {
            ContentType.Home -> R.string.home
            ContentType.Favorites -> R.string.favorites
            ContentType.Profile -> R.string.profile
        }
    )

    Scaffold(
        topBar = {
            if (teammatesUiState.currentContent != ContentType.Profile) {
                TeammatesTopAppBar(title = topAppBarTitle, canNavigateBack = false)
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentTab = teammatesUiState.currentContent,
                onTabPressed = onTabPressed,
                navigationItemContentList = navigationItemContentList
            )
        }
    ) { paddingValues ->
        when (teammatesUiState.currentContent) {
            ContentType.Home -> HomeContent(
                currentItem = currentItem,
                viewModel = viewModel,
                teammatesUiState = teammatesUiState,
                paddingValues = paddingValues
            )

            ContentType.Favorites -> Text(text = stringResource(R.string.favorites))
            ContentType.Profile -> ProfileNavHost(
                viewModel = viewModel,
                teammatesUiState = teammatesUiState,
                paddingValues = paddingValues
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    viewModel: TeammatesViewModel,
    teammatesUiState: TeammatesUiState.Home,
    currentItem: Int,
    paddingValues: PaddingValues
) {
    val isRefreshing = remember { mutableStateOf(false) }
    val pageIndex = remember { mutableIntStateOf(1) }
    val listState = remember(teammatesUiState.questionnaires) { LazyListState() }
    val isLoadingMore = remember { mutableStateOf(false) }
    val newCurrentItem = pageIndex.intValue * 10

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
                            viewModel.getNextFakeQuestionnaires(
                                teammatesUiState = teammatesUiState,
                                i = pageIndex.intValue,
                                newCurrentItem = newCurrentItem
                            )
                            pageIndex.intValue++
                        } finally {
                            Log.i("LOGIC", "Loading more items")
                        }
                    }
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

