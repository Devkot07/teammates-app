package com.pezont.teammates.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    val context = LocalContext.current

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
                navigationItemContentList = navigationItemContentList,
                modifier = Modifier.height(60.dp)
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
                paddingValues = paddingValues,
                modifier = Modifier
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
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { teammatesUiState.questionnaires.size + 1 })
    val isLoadingMore = remember { mutableStateOf(false) }
    val newCurrentItem = pagerState.currentPage * 10

    PullToRefreshBox(
        modifier = Modifier.padding(paddingValues),
        isRefreshing = isRefreshing.value,
        onRefresh = {
            isRefreshing.value = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    delay(1000L)
                    viewModel.tryGetQuestionnaires()
                } finally {
                    withContext(Dispatchers.Main) {
                        isRefreshing.value = false
                        pagerState.scrollToPage(0)
                    }
                }
            }
        }
    ) {
        QuestionnairesPager(
            questionnaires = teammatesUiState.questionnaires,
            pagerState = pagerState,
            modifier = Modifier.fillMaxSize()
            )



        LaunchedEffect(pagerState.currentPage) {
            if (!isLoadingMore.value && pagerState.currentPage == teammatesUiState.questionnaires.size) {
                isLoadingMore.value = true
                try {
//                    viewModel.tryGetQuestionnaires(
//                        page = pagerState.currentPage/10 + 1,
//                    )
                    viewModel.getNextFakeQuestionnaires(
                        teammatesUiState = teammatesUiState,
                        i = pagerState.currentPage / 10 + 1,
                        newCurrentItem = newCurrentItem
                    )
                } finally {
                    Log.i("LOGIC", "Loading more items")
                    isLoadingMore.value = false
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

