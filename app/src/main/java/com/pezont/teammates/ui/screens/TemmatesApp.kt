package com.pezont.teammates.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Text


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

import com.pezont.teammates.R
import com.pezont.teammates.models.ContentType
import com.pezont.teammates.ui.TeammatesUiState
import com.pezont.teammates.ui.TeammatesViewModel
import com.pezont.teammates.ui.navigation.ProfileNavHost
import com.pezont.teammates.ui.screens.sendToasts.SendNetworkErrorToast

@Composable
fun TeammatesApp(

    viewModel: TeammatesViewModel,

){
    val context = LocalContext.current

    when (val teammatesUiState = viewModel.teammatesUiState.collectAsState().value) {
        is TeammatesUiState.Loading -> TeammatesLoadingScreen()
        is TeammatesUiState.Login -> {
            SendNetworkErrorToast(teammatesUiState.statusResponse, context)
            LoginScreen(viewModel, teammatesUiState)
        }

        is TeammatesUiState.Home -> {
            TeammatesHomeScreen(
                currentItem = teammatesUiState.currentItem,
                onTabPressed = { contentType: ContentType ->
                    viewModel.updateCurrentContent(contentType)
                },
                teammatesUiState = teammatesUiState,
                viewModel = viewModel
            )


        }
        is TeammatesUiState.Error -> Text(
            "${stringResource(R.string.error)}${teammatesUiState.statusResponse}",
            textAlign = TextAlign.Center
            )
        is TeammatesUiState.ErrorNetwork -> ErrorNetworkScreen(viewModel::initState)
    }

}










