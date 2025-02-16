package com.pezont.teammates.ui.screens.myQuestionnaires

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import com.pezont.teammates.R
import com.pezont.teammates.ui.TeammatesTopAppBar
import com.pezont.teammates.ui.navigation.NavigationDestination

object MyQuestionnairesDestination : NavigationDestination {
    override val route = "my_questionnaires"
    override val titleRes = R.string.my_questionnaires
}

@Composable
fun MyQuestionnairesScreen(
    onNavigateUp: () -> Unit,
    navigateToQuestionnaireEntry: () -> Unit,
) {
    Scaffold(
        topBar = {
            TeammatesTopAppBar(
                title = stringResource(MyQuestionnairesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToQuestionnaireEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
    ) { innerPadding ->
        Text("My cards", Modifier.padding(innerPadding))
    }


}
